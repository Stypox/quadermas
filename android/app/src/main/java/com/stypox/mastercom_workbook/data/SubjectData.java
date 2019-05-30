package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SubjectData {
    private final String id;
    private final String name;

    public SubjectData(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = json.getString("nome");
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
