package com.stypox.mastercom_workbook.extractor;

import android.content.Context;
import android.os.Environment;

import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;
import com.stypox.mastercom_workbook.util.FileDownloader;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
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


    public static void downloadDocument(DocumentData documentData, Context context) {
        FileDownloader.download(
                documentDownloadUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{file_id}", documentData.getId()),
                AuthenticationExtractor.getCookie(),
                documentData.getName(), documentData.getSubject(),
                Environment.DIRECTORY_DOWNLOADS, documentData.getName(),
                context);
    }

    // see https://en.wikipedia.org/wiki/JSONP
    private static JSONObject fetchJsonP(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = UrlConnectionUtils.readAll(urlConnection);

        return new JSONObject(response.substring(1, response.length() - 2));
    }
}
