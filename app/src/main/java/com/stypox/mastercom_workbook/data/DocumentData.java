package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentData {
    private final String name;
    private final String id;
    private final String mime;
    private final String owner;
    private final String subject;

    public DocumentData(JSONObject json) throws JSONException {
        name = json.getString("name");
        id = json.getString("id");
        mime = json.getString("mime");
        owner = json.getString("owner_name") + " " + json.getString("owner_surname");

        String quotedSubject = json.getJSONArray("tags").getString(0);
        subject = quotedSubject.substring(1, quotedSubject.length()-1);
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public String getMime() {
        return mime;
    }
    public String getOwner() {
        return owner;
    }
    public String getSubject() {
        return subject;
    }
}
