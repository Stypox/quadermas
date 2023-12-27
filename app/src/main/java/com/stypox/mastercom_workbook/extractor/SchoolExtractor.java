package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.SchoolData;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchoolExtractor {
    public static final String schoolsUrl
            = "https://raw.githubusercontent.com/Stypox/mastercom-workbook/master/schools/schools.json";

    static Single<List<SchoolData>> fetchSchools(
            final Extractor.ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                final URL url = new URL(schoolsUrl);
                final JSONArray jsonResponse = fetchJsonArray(url);
                jsonAlreadyParsed = true;

                final List<SchoolData> schools = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    try {
                        schools.add(new SchoolData(jsonResponse.getJSONObject(i)));
                    } catch (Throwable e) {
                        itemErrorHandler.onItemError(ExtractorError.asExtractorError(e, true));
                    }
                }
                return schools;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static JSONArray fetchJsonArray(final URL url) throws IOException, JSONException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        final String response = UrlConnectionUtils.readAll(urlConnection);
        return new JSONArray(response);
    }
}
