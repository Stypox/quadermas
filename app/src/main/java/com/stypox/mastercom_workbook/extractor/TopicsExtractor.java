package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.TopicData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class TopicsExtractor {
    private static final String topicsUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_assignments_subject&id_materia={subject_id}";

    public static Single<List<TopicData>> fetchTopics(String subjectId) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(topicsUrl
                        .replace("{api_url}", ExtractorData.getAPIUrl())
                        .replace("{subject_id}", subjectId));

                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                JSONArray jsonDocuments = jsonResponse.getJSONArray("result");
                List<TopicData> documents = new ArrayList<>();
                for (int i = 0; i < jsonDocuments.length(); i++) {
                    documents.add(new TopicData(jsonDocuments.getJSONObject(i)));
                }
                return documents;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        })
                .subscribeOn(Schedulers.io());
    }
}
