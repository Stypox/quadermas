package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocumentData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);

    private final String name;
    private final String id;
    private final String owner;
    private final Date date;
    private final String subject;

    public DocumentData(JSONObject json) throws JSONException, ParseException {
        name = JsonUtils.getUnescapedString(json, "name");
        id = json.getString("id");
        owner = JsonUtils.getUnescapedString(json, "owner_name") + " " + JsonUtils.getUnescapedString(json, "owner_surname");
        date = dateFormat.parse(json.getString("received"));

        String quotedSubject = JsonUtils.getUnescapedString(json.getJSONArray("tags"), 0);
        if (quotedSubject.startsWith("\"") && quotedSubject.endsWith("\"")) {
            subject = quotedSubject.substring(1, quotedSubject.length() - 1);
        } else {
            subject = quotedSubject;
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public Date getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }
}
