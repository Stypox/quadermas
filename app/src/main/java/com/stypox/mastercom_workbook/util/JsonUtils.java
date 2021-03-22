package com.stypox.mastercom_workbook.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unbescape.html.HtmlEscape;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonUtils {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static Date getDateTime(final JSONObject json, final String key)
            throws JSONException, ParseException {
        return DATE_FORMAT.parse(json.getString(key));
    }

    public static String getUnescapedString(final JSONObject json, final String key)
            throws JSONException {
        return HtmlEscape.unescapeHtml(json.getString(key)).trim();
    }

    public static String getUnescapedString(final JSONArray json, final int index)
            throws JSONException {
        return HtmlEscape.unescapeHtml(json.getString(index)).trim();
    }
}
