package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SubjectExtractor {
    private static final String subjectsUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_subjects";
    private static final String marksUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&id_materia={subject_id}";


    public interface OnMarkExtractionError {
        void onMarkExtractionError(String subjectName);
    }


    /**
     * @param onMarkError will be called on a thread different from the main one
     */
    public static Observable<SubjectData> fetchSubjects(OnMarkExtractionError onMarkError) {
        return Observable
                .create((ObservableOnSubscribe<SubjectData>) emitter -> {
                    boolean jsonAlreadyParsed = false;
                    try {
                        URL url = new URL(subjectsUrl
                                .replace("{api_url}", ExtractorData.getAPIUrl()));

                        JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                        jsonAlreadyParsed = true;

                        JSONArray list = jsonResponse.getJSONArray("result");
                        for (int i = 0; i < list.length(); i++) {
                            emitter.onNext(new SubjectData(list.getJSONObject(i)));
                        }
                        emitter.onComplete();
                    } catch (Throwable e) {
                        throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
                    }
                })
                .flatMap(subjectData1 -> Observable.defer(() -> Observable.just(subjectData1)
                        .map(subjectData -> {
                            boolean jsonAlreadyParsed = false;
                            try {
                                URL url = new URL(marksUrl
                                        .replace("{api_url}", ExtractorData.getAPIUrl())
                                        .replace("{subject_id}", subjectData.getId()));

                                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                                jsonAlreadyParsed = true;

                                JSONArray list = jsonResponse.getJSONArray("result");
                                List<MarkData> marks = new ArrayList<>();
                                for (int i = 0; i < list.length(); i++) {
                                    try {
                                        marks.add(new MarkData(list.getJSONObject(i)));
                                    } catch (Throwable e) {
                                        onMarkError.onMarkExtractionError(subjectData.getName());
                                    }
                                }
                                subjectData.setMarks(marks);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                subjectData.setMarks(new ArrayList<>());
                                subjectData.setError(ExtractorError.asExtractorError(e, jsonAlreadyParsed));
                            }

                            return subjectData;
                        })
                        .subscribeOn(Schedulers.newThread())))
                .subscribeOn(Schedulers.io());
    }
}
