package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SubjectExtractor {
    private static final String subjectsUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_subjects";
    private static final String marksUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&id_materia={subject_id}";

    public static Observable<SubjectData> fetchSubjects() {
        return Observable
                .create((ObservableOnSubscribe<SubjectData>) emitter -> {
                    boolean jsonAlreadyParsed = false;
                    try {
                        URL url = new URL(subjectsUrl
                                .replace("{APIUrl}", ExtractorData.getAPIUrl()));

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
                                        .replace("{APIUrl}", ExtractorData.getAPIUrl())
                                        .replace("{subject_id}", subjectData.getId()));

                                JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                                jsonAlreadyParsed = true;

                                JSONArray list = jsonResponse.getJSONArray("result");
                                List<MarkData> marks = new ArrayList<>();
                                for (int i = 0; i < list.length(); i++) {
                                    marks.add(new MarkData(list.getJSONObject(i)));
                                }
                                subjectData.setMarks(marks);
                            } catch (Throwable e) {
                                subjectData.setError(ExtractorError.asExtractorError(e, jsonAlreadyParsed));
                            }

                            return subjectData;
                        })
                        .subscribeOn(Schedulers.newThread())))
                .subscribeOn(Schedulers.io());
    }
}
