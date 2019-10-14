package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.DocumentData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class DocumentsExtractor {
    private static final String documentsUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/documents/{year_id}";


    public static Observable<DocumentData> fetchDocuments(String yearId) {
        return Observable
                .create((ObservableOnSubscribe<DocumentData>) emitter -> {
                    boolean jsonAlreadyParsed = false;
                    try {
                        URL url = new URL(documentsUrl
                                .replace("{api_url}", ExtractorData.getAPIUrl())
                                .replace("{year_id}", yearId));

                        JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                        jsonAlreadyParsed = true;

                        JSONArray list = jsonResponse.getJSONArray("results");
                        for (int i = 0; i < list.length(); i++) {
                            emitter.onNext(new DocumentData(list.getJSONObject(i)));
                        }
                        emitter.onComplete();
                    } catch (Throwable e) {
                        throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
