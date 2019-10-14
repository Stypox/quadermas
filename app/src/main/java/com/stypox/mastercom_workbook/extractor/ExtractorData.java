package com.stypox.mastercom_workbook.extractor;

public class ExtractorData {
    private static final String APIUrlToShow = "{api_url}.registroelettronico.com";

    private static String APIUrl;
    private static String user;
    private static String password;


    public static void setAPIUrl(String APIUrl) {
        ExtractorData.APIUrl = APIUrl;
    }
    public static void setUser(String user) {
        ExtractorData.user = user;
    }
    public static void setPassword(String password) {
        ExtractorData.password = password;
    }

    public static String getAPIUrl() {
        return APIUrl;
    }
    public static String getUser() {
        return user;
    }
    public static String getPassword() {
        return password;
    }

    public static String getFullAPIUrlToShow() {
        return APIUrlToShow.replace("{api_url}", APIUrl);
    }
}
