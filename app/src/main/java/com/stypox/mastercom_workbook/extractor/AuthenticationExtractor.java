package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.util.FullNameFormatting;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthenticationExtractor {
    private static final String phpsessidAuthenticationUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?user={user}&password={password}";
    private static final String messengerAuthenticationUrl = "https://{api_url}.registroelettronico.com/messenger/1.0/authentication";
    private static final MediaType messengerAuthenticationMediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
    private static final String messengerAuthenticationBody = "name={user}%40{api_url}.registroelettronico.com&certificate={password}";
    private static final OkHttpClient okHttpClient = new OkHttpClient();

    private static String phpsessidCookie = "";
    private static String messengerCookie = "";


    public static Single<String> authenticateMain() {
        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                URL url = new URL(phpsessidAuthenticationUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{user}", Extractor.getUser())
                        .replace("{password}", Extractor.getPassword()));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = UrlConnectionUtils.readAll(urlConnection);

                phpsessidCookie = urlConnection.getHeaderField("Set-Cookie").substring(0, "PHPSESSID=00000000000000000000000000".length());
                JSONObject jsonResponse = new JSONObject(response);
                jsonAlreadyParsed = true;

                //noinspection PointlessBooleanExpression
                if (jsonResponse.getBoolean("auth") == false) {
                    throw new ExtractorError(ExtractorError.Type.invalid_credentials);
                }

                String fullNameUppercase = jsonResponse.getJSONObject("result").getString("full_name");
                return FullNameFormatting.capitalize(fullNameUppercase);
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static String generateMessengerCookie() {
        return "messenger=" + UUID.randomUUID().toString();
    }

    public static Completable authenticateMessenger() {
        return Completable.fromAction(() -> {
            messengerCookie = generateMessengerCookie();

            RequestBody body = RequestBody.create(messengerAuthenticationMediaType,
                    messengerAuthenticationBody
                            .replace("{api_url}", Extractor.getAPIUrl())
                            .replace("{user}", Extractor.getUser())
                            .replace("{password}", Extractor.getPassword()));

            Request request = new Request.Builder()
                    .url(messengerAuthenticationUrl
                            .replace("{api_url}", Extractor.getAPIUrl()))
                    .addHeader("Cookie", messengerCookie)
                    .post(body)
                    .build();

            okHttpClient.newCall(request).execute();
        }).subscribeOn(Schedulers.io());
    }


    static String getCookie() {
        return phpsessidCookie + "; " + messengerCookie;
    }

    static JSONObject fetchJsonAuthenticated(URL url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Cookie", getCookie()); // auth cookie
        String response = UrlConnectionUtils.readAll(urlConnection);

        return new JSONObject(response);
    }
}
