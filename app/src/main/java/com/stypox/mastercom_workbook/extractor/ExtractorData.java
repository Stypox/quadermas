package com.stypox.mastercom_workbook.extractor;

public class ExtractorData {
    private static final String APIUrlToShow = "{APIUrl}.registroelettronico.com";
    private static String APIUrl; // e.g. marconi-tn


    public static void setAPIUrl(String newAPIUrl) {
        APIUrl = newAPIUrl;
    }

    public static String getAPIUrl() {
        return APIUrl;
    }

    public static String getFullAPIUrlToShow() {
        return APIUrlToShow.replace("{APIUrl}", APIUrl);
    }
}
