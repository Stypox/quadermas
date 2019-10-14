package com.stypox.mastercom_workbook.extractor;

import android.util.Pair;

import com.stypox.mastercom_workbook.util.FullNameFormatting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AuthenticationExtractor {
    private static final String authenticationUrl = "https://{APIUrl}.registroelettronico.com/mastercom/register_manager.php?user={user}&password={password}";
    private static String authenticationCookie;


    private static String readAll(HttpURLConnection urlConnection) throws IOException {
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            urlConnection.disconnect();
        }
    }


    public static Single<String> authenticate(String user, String password) {
        return Single.fromCallable(() -> {
            try {
                URL url = new URL(authenticationUrl
                        .replace("{APIUrl}", ExtractorData.getAPIUrl())
                        .replace("{user}", user)
                        .replace("{password}", password));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = readAll(urlConnection);

                String cookieToSet = urlConnection.getHeaderField("Set-Cookie"); // only takes the last Set-Cookie it finds
                JSONObject jsonResponse = new JSONObject(response);
                return new Pair<>(cookieToSet, jsonResponse);
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, false);
            }
        }).map(cookieAndResponse -> {
            try {
                //noinspection PointlessBooleanExpression
                if (cookieAndResponse.second.getBoolean("auth") == false) {
                    throw new ExtractorError(ExtractorError.Type.invalid_credentials);
                }

                authenticationCookie = cookieAndResponse.first.substring(0, "PHPSESSID=00000000000000000000000000".length());
                String fullNameUppercase = cookieAndResponse.second.getJSONObject("result").getString("full_name");
                return FullNameFormatting.capitalize(fullNameUppercase);
            } catch (JSONException e) {
                throw ExtractorError.asExtractorError(e, true);
            }
        }).subscribeOn(Schedulers.io());
    }



    static JSONObject fetchJsonAuthenticated(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Cookie", authenticationCookie); // auth cookie
        String response = readAll(urlConnection);

        return new JSONObject(response);
    }
}
