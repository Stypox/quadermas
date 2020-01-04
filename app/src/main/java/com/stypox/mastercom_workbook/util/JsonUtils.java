package com.stypox.mastercom_workbook.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unbescape.html.HtmlEscape;

public class JsonUtils {
    public static String getUnescapedString(JSONObject json, String key) throws JSONException {
        return HtmlEscape.unescapeHtml(json.getString(key)).trim();
    }

    public static String getUnescapedString(JSONArray json, int index) throws JSONException {
        return HtmlEscape.unescapeHtml(json.getString(index)).trim();
    }
}
