package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class TopicExtractor {
    private static final String topicsUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_assignments_subject&id_materia={subject_id}";

    /**
     * @param onTopicError will be called on a thread different from the main one
     */
    public static Single<SubjectData> fetchTopics(SubjectData subjectData, Runnable onTopicError) {
        return Single
                .fromCallable(() -> {
                    subjectData.setTopics(null); // remove old topics
                    boolean jsonAlreadyParsed = false;

                    try {
                        URL url = new URL(topicsUrl
                                .replace("{api_url}", ExtractorData.getAPIUrl())
                                .replace("{subject_id}", subjectData.getId()));

                        JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                        jsonAlreadyParsed = true;

                        JSONArray jsonTopics = jsonResponse.getJSONArray("result");
                        List<TopicData> topics = new ArrayList<>();
                        for (int i = 0; i < jsonTopics.length(); i++) {
                            try {
                                topics.add(new TopicData(jsonTopics.getJSONObject(i)));
                            } catch (Throwable e) {
                                onTopicError.run();
                            }
                        }
                        subjectData.setTopics(topics);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
                    }

                    return subjectData;
                })
                .subscribeOn(Schedulers.io());
    }
}
