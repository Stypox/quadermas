package com.stypox.mastercom_workbook.extractor;

import android.util.Log;
import android.util.Pair;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.ExtractorError.Type;
import com.stypox.mastercom_workbook.util.FullNameFormatting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Extractor {
    private static final String APIUrlToShow = "{APIUrl}.registroelettronico.com";
    private static final String authenticationUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?user={user}&password={password}";
    private static final String subjectsUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_subjects";
    private static final String marksUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&id_materia={subject_id}";

    private static String authenticationCookie;
    private static String APIUrl; // e.g. rosmini-tn


    ///////////
    // UTILS //
    ///////////

    private static String readAll(HttpURLConnection urlConnection) throws IOException {
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            urlConnection.disconnect();
        }
    }

    private static JSONObject fetchJsonAuthenticated(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Cookie", authenticationCookie); // auth cookie
        String response = readAll(urlConnection);

        return new JSONObject(response);
    }

    private static ExtractorError asExtractorError(Throwable throwable, boolean jsonAlreadyParsed) {
        if (throwable instanceof UnknownHostException || throwable instanceof MalformedURLException) {
            return new ExtractorError(Type.malformed_url, throwable);
        } else if (throwable instanceof JSONException) {
            if (jsonAlreadyParsed) {
                return new ExtractorError(Type.unsuitable_json, throwable);
            } else {
                return new ExtractorError(Type.not_json, throwable);
            }
        } else { // throwable instanceof IOException
            return new ExtractorError(Type.network, throwable);
        }
    }


    /////////////////////
    // API URL SETTING //
    /////////////////////

    public static void setAPIUrl(String newAPIUrl) {
        APIUrl = newAPIUrl;
    }

    public static String getFullAPIUrlToShow() {
        return APIUrlToShow.replace("{APIUrl}", APIUrl);
    }


    ////////////////////
    // AUTHENTICATION //
    ////////////////////

    public static Single<String> authenticate(String user, String password) {
        return Single.fromCallable(() -> {
            try {
                URL url = new URL(authenticationUrl
                        .replace("{APIUrl}", APIUrl)
                        .replace("{user}", user)
                        .replace("{password}", password));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = readAll(urlConnection);

                String cookieToSet = urlConnection.getHeaderField("Set-Cookie"); // only takes the last Set-Cookie it finds
                JSONObject jsonResponse = new JSONObject(response);
                return new Pair<>(cookieToSet, jsonResponse);
            } catch (Throwable e) {
                throw asExtractorError(e, false);
            }
        }).map(cookieAndResponse -> {
            try {
                //noinspection PointlessBooleanExpression
                if (cookieAndResponse.second.getBoolean("auth") == false) {
                    throw new ExtractorError(Type.invalid_credentials);
                }

                authenticationCookie = cookieAndResponse.first.substring(0, "PHPSESSID=00000000000000000000000000".length());
                String fullNameUppercase = cookieAndResponse.second.getJSONObject("result").getString("full_name");
                return FullNameFormatting.capitalize(fullNameUppercase);
            } catch (JSONException e) {
                throw asExtractorError(e, true);
            }
        }).subscribeOn(Schedulers.io());
    }


    //////////////
    // SUBJECTS //
    //////////////

    public static Observable<SubjectData> fetchSubjects() {
        return Observable
                .create((ObservableOnSubscribe<SubjectData>) emitter -> {
                    boolean jsonAlreadyParsed = false;
                    try {
                        URL url = new URL(subjectsUrl
                                .replace("{APIUrl}", APIUrl));

                        JSONObject jsonResponse = fetchJsonAuthenticated(url);
                        jsonAlreadyParsed = true;

                        JSONArray list = jsonResponse.getJSONArray("result");
                        for (int i = 0; i < list.length(); i++) {
                            emitter.onNext(new SubjectData(list.getJSONObject(i)));
                        }
                        emitter.onComplete();
                    } catch (Throwable e) {
                        throw asExtractorError(e, jsonAlreadyParsed);
                    }
                })
                .flatMap(subjectData1 -> Observable.defer(() -> Observable.just(subjectData1)
                        .map(subjectData -> {
                            boolean jsonAlreadyParsed = false;
                            try {
                                URL url = new URL(marksUrl
                                        .replace("{APIUrl}", APIUrl)
                                        .replace("{subject_id}", subjectData.getId()));

                                JSONObject jsonResponse = fetchJsonAuthenticated(url);
                                jsonAlreadyParsed = true;

                                JSONArray list = jsonResponse.getJSONArray("result");
                                List<MarkData> marks = new ArrayList<>();
                                for (int i = 0; i < list.length(); i++) {
                                    marks.add(new MarkData(list.getJSONObject(i)));
                                }
                                subjectData.setMarks(marks);
                            } catch (Throwable e) {
                                subjectData.setError(asExtractorError(e, jsonAlreadyParsed));
                            }

                            Log.w("MAP", Thread.currentThread().getName());
                            return subjectData;
                        })
                        .subscribeOn(Schedulers.newThread())))
                .subscribeOn(Schedulers.io());
    }
}
