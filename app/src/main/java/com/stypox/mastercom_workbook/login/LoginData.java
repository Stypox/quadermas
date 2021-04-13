package com.stypox.mastercom_workbook.login;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginData {
    private static final String loginPreferenceKey = "login";
    private static final String APIUrlKey = "APIUrl";
    private static final String passwordKey = "password";
    private static final String userKey = "user";

    public static boolean isLoggedIn(final Context context) {
        final SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(APIUrlKey, null) != null &&
                sp.getString(userKey, null) != null &&
                sp.getString(passwordKey, null) != null;
    }

    public static String getAPIUrl(final Context context) {
        return getSharedPreferences(context).getString(APIUrlKey, "");
    }

    public static String getUser(final Context context) {
        return getSharedPreferences(context).getString(userKey, "");
    }

    public static String getPassword(final Context context) {
        return getSharedPreferences(context).getString(passwordKey, "");
    }

    public static void setCredentials(final Context context,
                                      final String APIUrl,
                                      final String user,
                                      final String password) {

        getSharedPreferences(context).edit()
                .putString(APIUrlKey, APIUrl)
                .putString(userKey, user)
                .putString(passwordKey, password)
                .apply();
    }

    public static void logout(final Context context) {
        getSharedPreferences(context).edit()
                .remove(passwordKey)
                .apply();
    }


    private static SharedPreferences getSharedPreferences(final Context context) {
        return context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
    }
}
