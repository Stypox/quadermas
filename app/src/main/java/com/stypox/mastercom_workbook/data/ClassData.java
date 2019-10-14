package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ClassData {
    private final String year;
    private final String id;


    public ClassData(JSONObject json) throws JSONException {
        year = json.getString("year");
        id = json.getString("id");
    }

    public String getYear() {
        return year;
    }
    public String getId() {
        return id;
    }
}
