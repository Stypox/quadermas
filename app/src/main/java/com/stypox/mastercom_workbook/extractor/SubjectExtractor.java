package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubjectExtractor {
    private static final String subjectsUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_subjects";
    private static final String marksUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&id_materia={subject_id}";
    private static final String topicsUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_assignments_subject&id_materia={subject_id}";


    static Single<List<SubjectData>> fetchSubjects(ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(subjectsUrl
                        .replace("{api_url}", Extractor.getAPIUrl()));

                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                if (jsonResponse.isNull("result")) {
                    throw new ExtractorError(ExtractorError.Type.user_has_no_subjects);
                }

                JSONArray list = jsonResponse.getJSONArray("result");
                List<SubjectData> subjects = new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    try {
                        subjects.add(new SubjectData(list.getJSONObject(i)));
                    } catch (Throwable e) {
                        itemErrorHandler.onItemError(ExtractorError.asExtractorError(e, true));
                    }
                }
                return subjects;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }

    static Single<SubjectData> fetchMarks(SubjectData subjectData, ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            subjectData.setMarks(null); // remove old marks
            boolean jsonAlreadyParsed = false;

            try {
                URL url = new URL(marksUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{subject_id}", subjectData.getId()));

                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                JSONArray list = jsonResponse.getJSONArray("result");
                List<MarkData> marks = new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    try {
                        marks.add(new MarkData(list.getJSONObject(i)));
                    } catch (Throwable e) {
                        itemErrorHandler.onItemError(e);
                    }
                }
                subjectData.setMarks(marks);
            } catch (Throwable e) {
                e.printStackTrace();
                subjectData.setMarkExtractionError(
                        ExtractorError.asExtractorError(e, jsonAlreadyParsed));
            }

            return subjectData;
        }).subscribeOn(Schedulers.io());
    }

    static Single<SubjectData> fetchTopics(SubjectData subjectData, ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            subjectData.setTopics(null); // remove old topics
            boolean jsonAlreadyParsed = false;

            try {
                URL url = new URL(topicsUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{subject_id}", subjectData.getId()));

                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                JSONArray jsonTopics = jsonResponse.getJSONArray("result");
                List<TopicData> topics = new ArrayList<>();
                for (int i = 0; i < jsonTopics.length(); i++) {
                    try {
                        topics.add(new TopicData(jsonTopics.getJSONObject(i)));
                    } catch (Throwable e) {
                        itemErrorHandler.onItemError(e);
                    }
                }
                subjectData.setTopics(topics);
            } catch (Throwable e) {
                e.printStackTrace();
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }

            return subjectData;
        }).subscribeOn(Schedulers.io());
    }
}
