package com.stypox.mastercom_workbook.extractor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.stypox.mastercom_workbook.R;
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
import java.util.Scanner;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class Extractor {
    private static final String APIUrlToShow = "{APIUrl}.registroelettronico.com";
    private static final String authenticationUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?user={user}&password={password}";
    private static final String subjectsUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_subjects";
    private static final String marksUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&id_materia={subject_id}";

    private static String authenticationCookie;
    private static String APIUrl; // e.g. rosmini-tn


    public enum Error {
        malformed_url,
        network,
        not_json,
        unsuitable_json,
        invalid_credentials;

        public String toString(Context context) {
            switch (this) {
                case malformed_url:
                    return context.getResources().getString(R.string.error_malformed_url);
                case network:
                    return context.getResources().getString(R.string.error_network);
                case not_json:
                    return context.getResources().getString(R.string.error_not_json);
                case unsuitable_json:
                    return context.getResources().getString(R.string.error_unsuitable_json);
                case invalid_credentials: default: // default is useless
                    return context.getResources().getString(R.string.error_invalid_credentials);
            }
        }
    }


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

    private static ExtractorError asExtractorError(Throwable throwable, boolean jsonAlreadyParsed) throws ExtractorError {
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
        });
    }


    //////////////
    // SUBJECTS //
    //////////////

    public static Single<ArrayList<SubjectData>> fetchSubjects() {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(subjectsUrl
                        .replace("{APIUrl}", APIUrl));

                JSONObject jsonResponse = fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;
                JSONArray result = jsonResponse.getJSONArray("result");

                ArrayList<SubjectData> subjects = new ArrayList<SubjectData>();
                for (int i = 0; i < result.length(); i++) {
                    subjects.add(new SubjectData(result.getJSONObject(i)));
                }
                return subjects;
            } catch (Throwable e) {
                throw asExtractorError(e, jsonAlreadyParsed);
            }
        });
    }



    //////////////
    // SUBJECTS //
    //////////////

    private static class FetchMarksTask extends AsyncTask<URL, Error, Void> {
        private FetchMarksCallback callback;
        private JSONObject jsonResponse = null;

        FetchMarksTask(FetchMarksCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(URL... urls) {
            try {
                jsonResponse = fetchJsonAuthenticated(urls[0]);
            } catch (UnknownHostException e) {
                publishProgress(Error.malformed_url);
            } catch (IOException e) {
                publishProgress(Error.network);
            } catch (JSONException e) {
                publishProgress(Error.not_json);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Error... error) {
            callback.onError(error[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (jsonResponse != null) {
                Extractor.fetchMarksCallback(jsonResponse, callback);
            }
        }
    }

    private static void fetchMarksCallback(JSONObject jsonResponse, FetchMarksCallback callback) {
        try {
            JSONArray result = jsonResponse.getJSONArray("result");

            ArrayList<MarkData> marks = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                marks.add(new MarkData(result.getJSONObject(i)));
            }

            callback.onFetchMarksCompleted(marks);
        } catch (Throwable e) {
            callback.onError(Error.unsuitable_json);
        }
    }

    public static void fetchMarks(String subjectId, FetchMarksCallback callback) {
        FetchMarksTask fetchMarksTask = new FetchMarksTask(callback);
        try {
            fetchMarksTask.execute(new URL(marksUrl
                    .replace("{APIUrl}", APIUrl)
                    .replace("{subject_id}", subjectId)));
        } catch (MalformedURLException e) {
            callback.onError(Error.malformed_url);
        }
    }

}
