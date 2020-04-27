package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.StudentData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class StudentExtractor {
    private static final String studentDataUrl = "https://{api_url}.registroelettronico.com/mastercom/ws/checkStudente.php?user={user}&password={password}";


    // see https://en.wikipedia.org/wiki/JSONP
    private static JSONObject fetchJsonP(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = UrlConnectionUtils.readAll(urlConnection);

        return new JSONObject(response.substring(1, response.length() - 2));
    }

    public static Single<StudentData> fetchStudent(ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(studentDataUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{user}", Extractor.getUser())
                        .replace("{password}", Extractor.getPassword()));

                JSONObject jsonResponse = fetchJsonP(url);
                jsonAlreadyParsed = true;

                return new StudentData(jsonResponse, itemErrorHandler);
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }
}
