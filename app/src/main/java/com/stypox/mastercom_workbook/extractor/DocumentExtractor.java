package com.stypox.mastercom_workbook.extractor;

import android.content.Context;
import android.os.Environment;

import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;
import com.stypox.mastercom_workbook.util.FileDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DocumentExtractor {
    private static final String documentsUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/documents/{year_id}";
    private static final String documentDownloadUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/messages/{file_id}/raw/attachment";


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
                })
                .subscribeOn(Schedulers.io());
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
}
