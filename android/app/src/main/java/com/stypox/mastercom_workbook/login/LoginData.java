package com.stypox.mastercom_workbook.login;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginData {
    private static final String loginPreferenceKey = "login";
    private static final String APIUrlKey = "APIUrl";
    private static final String passwordKey = "password";
    private static final String userKey = "user";

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
        return sp.getString(APIUrlKey, null) != null &&
                sp.getString(userKey, null) != null &&
                sp.getString(passwordKey, null) != null;
    }

    public static String getAPIUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
        return sp.getString(APIUrlKey, "");
    }
    public static String getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
        return sp.getString(userKey, "");
    }
    public static String getPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
        return sp.getString(passwordKey, "");
    }

    public static void setCredentials(Context context, String APIUrl, String user, String password) {
        SharedPreferences sp = context.getSharedPreferences(loginPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(APIUrlKey, APIUrl);
        editor.putString(userKey, user);
        editor.putString(passwordKey, password);
        editor.apply();
    }
}
