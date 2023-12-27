package com.stypox.mastercom_workbook.extractor;

import android.text.TextUtils;

import com.stypox.mastercom_workbook.data.FakeFetchedData;
import com.stypox.mastercom_workbook.util.FullNameFormatting;
import com.stypox.mastercom_workbook.util.UrlConnectionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
    private static String fullName = "";

    public static Single<String> authenticateMain(final boolean reload) {
        if (Extractor.isFakeAccount()) {
            if (FakeFetchedData.PASSWORD.equals(Extractor.getPassword()) &&
                    FakeFetchedData.USER.equals(Extractor.getUser())) {
                fullName = FakeFetchedData.FULL_NAME;
                return Single.just(FakeFetchedData.FULL_NAME);
            } else {
                return Single.error(new ExtractorError(ExtractorError.Type.invalid_credentials));
            }
        }

        if (!reload && !TextUtils.isEmpty(phpsessidCookie) && !TextUtils.isEmpty(fullName)) {
            // already authenticated, do not authenticate again
            return Single.just(fullName);
        }

        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                final URL url = new URL(phpsessidAuthenticationUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{user}", Extractor.getUser())
                        .replace("{password}", Extractor.getPassword()));
                final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                final String response = UrlConnectionUtils.readAll(urlConnection);

                phpsessidCookie = urlConnection.getHeaderField("Set-Cookie")
                        .substring(0, "PHPSESSID=00000000000000000000000000".length());
                final JSONObject jsonResponse = new JSONObject(response);
                jsonAlreadyParsed = true;

                //noinspection PointlessBooleanExpression
                if (jsonResponse.getBoolean("auth") == false) {
                    throw new ExtractorError(ExtractorError.Type.invalid_credentials);
                }

                final String fullNameUppercase
                        = jsonResponse.getJSONObject("result").getString("full_name");
                fullName = FullNameFormatting.capitalize(fullNameUppercase);
                return fullName;

            } catch (final Throwable e) {
                if (e instanceof FileNotFoundException || e instanceof UnknownHostException) {
                    // error 404: the API url is invalid
                    throw new ExtractorError(ExtractorError.Type.invalid_api_url, e);
                }
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static String generateMessengerCookie() {
        return "messenger=" + UUID.randomUUID().toString();
    }

    public static Completable authenticateMessenger(final boolean reload) {
        if (Extractor.isFakeAccount()) {
            return Completable.complete();
        }

        if (!reload && !TextUtils.isEmpty(messengerCookie)) {
            // already authenticated, do not authenticate again
            return Completable.complete();
        }

        return Completable.fromAction(() -> {
            try {
                messengerCookie = generateMessengerCookie();

                final RequestBody body = RequestBody.create(messengerAuthenticationMediaType,
                        messengerAuthenticationBody
                                .replace("{api_url}", Extractor.getAPIUrl())
                                .replace("{user}", Extractor.getUser())
                                .replace("{password}", Extractor.getPassword()));

                final Request request = new Request.Builder()
                        .url(messengerAuthenticationUrl
                                .replace("{api_url}", Extractor.getAPIUrl()))
                        .addHeader("Cookie", messengerCookie)
                        .post(body)
                        .build();

                okHttpClient.newCall(request).execute();
            } catch (final Throwable throwable) {
                // reset messenger cookie so that it is forcibly reloaded if authenticateMessenger
                // is called again
                messengerCookie = "";
                throw throwable;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * To be called on logout
     */
    public static void removeAllData() {
        phpsessidCookie = "";
        messengerCookie = "";
        fullName = "";
    }

    /**
     * To be called when sure that authentication has already been done
     */
    public static String getFullName() {
        return fullName;
    }


    static String getCookie() {
        return phpsessidCookie + "; " + messengerCookie;
    }

    static JSONObject fetchJsonAuthenticated(final URL url) throws IOException, JSONException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Cookie", getCookie()); // auth cookie
        final String response = UrlConnectionUtils.readAll(urlConnection);
        return new JSONObject(response);
    }
}
