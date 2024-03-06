package com.stypox.mastercom_workbook.extractor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.stypox.mastercom_workbook.BuildConfig;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DocumentExtractor {
    private static final String documentsUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/documents/{year_id}";
    private static final String documentDownloadUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/messages/{file_id}/raw/attachment";
    private static final String studentDataUrl = "https://{api_url}.registroelettronico.com/mastercom/ws/checkStudente.php?user={user}&password={password}";


    public static Single<List<ClassData>> fetchClasses(ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(studentDataUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{user}", Extractor.getUser())
                        .replace("{password}", Extractor.getPassword()));

                JSONObject jsonResponse = fetchJsonP(url);
                jsonAlreadyParsed = true;
                JSONObject channels = jsonResponse.getJSONObject("result").getJSONObject("channels");
                List<ClassData> classes = new ArrayList<>();

                JSONArray studenti = channels.getJSONArray("studenti");
                for (int i = 0; i < studenti.length(); ++i) {
                    try {
                        classes.add(new ClassData(studenti.getJSONObject(i)));
                    } catch (Throwable throwable) {
                        itemErrorHandler.onItemError(throwable);
                    }
                }

                JSONArray professori = channels.getJSONArray("professori");
                for (int i = 0; i < professori.length(); ++i) {
                    try {
                        classes.add(new ClassData(professori.getJSONObject(i)));
                    } catch (Throwable throwable) {
                        itemErrorHandler.onItemError(throwable);
                    }
                }

                return classes;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Single<ClassData> fetchDocuments(ClassData classData, ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(documentsUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{year_id}", classData.getId()));

                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                JSONArray jsonDocuments = jsonResponse.getJSONArray("results");
                List<DocumentData> documents = new ArrayList<>();
                for (int i = 0; i < jsonDocuments.length(); i++) {
                    try {
                        documents.add(new DocumentData(jsonDocuments.getJSONObject(i)));
                    } catch (Throwable e) {
                        itemErrorHandler.onItemError(e);
                    }
                }

                classData.setDocuments(documents);
                return classData;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }


    public interface OnProgressUpdate {
        void onProgressUpdate(float progress);
    }

    public static Completable downloadDocument(
            final DocumentData doc,
            final Context context,
            final OnProgressUpdate onProgressUpdate
    ) {
        return Completable.fromAction(() -> downloadDocumentImpl(doc, context, onProgressUpdate))
                .subscribeOn(Schedulers.io());
    }

    private static void downloadDocumentImpl(
            final DocumentData doc,
            final Context context,
            final OnProgressUpdate onProgressUpdate
    ) throws IOException {
        onProgressUpdate.onProgressUpdate(0.0f); // i.e. starting to download

        final String url;
        if (Extractor.isFakeAccount()) {
            url = doc.getId();
        } else {
            url = documentDownloadUrl
                    .replace("{api_url}", Extractor.getAPIUrl())
                    .replace("{file_id}", doc.getId());
        }

        final File file = File.createTempFile(doc.getName(), ".part", context.getCacheDir());
        final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("Cookie", AuthenticationExtractor.getCookie());
        urlConnection.connect();

        try (FileOutputStream fos = new FileOutputStream(file);
             InputStream in = urlConnection.getInputStream()) {

            final long totalSize = urlConnection.getContentLength();
            long downloadedSize = 0;
            final byte[] buffer = new byte[32768];
            int readSize;
            while ((readSize = in.read(buffer)) > 0) {
                fos.write(buffer, 0, readSize);
                downloadedSize += readSize;
                onProgressUpdate.onProgressUpdate((float)downloadedSize / totalSize);
            }
        }
        onProgressUpdate.onProgressUpdate(1.0f); // i.e. no download in progress

        final String authority = BuildConfig.APPLICATION_ID + ".provider";
        final Uri uri = FileProvider.getUriForFile(context, authority, file);
        if (uri == null) {
            throw new IOException("Cannot get content uri");
        }

        final String[] extensions = doc.getName().split("\\.");
        final String extension = extensions[extensions.length - 1];
        final Intent shareIntent = new ShareCompat.IntentBuilder(context)
            .setChooserTitle(doc.getName())
            .setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension))
            .setStream(uri)
            .setSubject(doc.getSubject())
            .setText(doc.getName())
            .getIntent();
        shareIntent.setData(uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
    }

    // see https://en.wikipedia.org/wiki/JSONP
    private static JSONObject fetchJsonP(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = UrlConnectionUtils.readAll(urlConnection);

        return new JSONObject(response.substring(1, response.length() - 2));
    }
}
