package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentData {
    private final String name;
    private final String id;
    private final String owner;
    private final Date date;
    private final String subject;

    public DocumentData(JSONObject json) throws JSONException, ParseException {
        name = json.getString("name");
        id = json.getString("id");
        owner = json.getString("owner_name") + " " + json.getString("owner_surname");
        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(json.getString("received"));

        String quotedSubject = json.getJSONArray("tags").getString(0);
        if (quotedSubject.startsWith("\"") && quotedSubject.endsWith("\"")) {
            subject = quotedSubject.substring(1, quotedSubject.length()-1);
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
