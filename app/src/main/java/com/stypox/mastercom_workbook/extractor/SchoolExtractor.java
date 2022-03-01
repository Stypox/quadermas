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
            = "https://mp.registroelettronico.com/v3/scuole/?format=json&token=";

    static Single<List<SchoolData>> fetchSchools(
            final Extractor.ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                final URL url = new URL(schoolsUrl + generateSchoolsToken());

                final JSONArray jsonResponse = fetchJsonArray(url);
                jsonAlreadyParsed = true;

                // make sure not to add duplicate schools
                final Set<String> seenAPIUrls = new HashSet<>();
                final List<SchoolData> schools = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    try {
                        final SchoolData schoolData = new SchoolData(jsonResponse.getJSONObject(i));
                        if (schoolData.isValid() && !seenAPIUrls.contains(schoolData.getAPIUrl())) {
                            schools.add(schoolData);
                            seenAPIUrls.add(schoolData.getAPIUrl());
                        }
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

    private static String generateSchoolsToken() {
        final String dateString = new SimpleDateFormat("yyyyMMdd", Locale.ITALY).format(new Date())
                + "secret";

        try {
            //noinspection CharsetObjectCanBeUsed
            final byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(dateString.getBytes("UTF-8"));

            final StringBuilder result = new StringBuilder();
            for (final byte b : digest) {
                final String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    result.append('0');
                }
                result.append(hexString);
            }
            return result.toString();

        } catch (final Exception e) {
            throw new RuntimeException("Failed to generate schools token", e);
        }
    }

    private static JSONArray fetchJsonArray(final URL url) throws IOException, JSONException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        final String response = UrlConnectionUtils.readAll(urlConnection);
        return new JSONArray(response);
    }
}
