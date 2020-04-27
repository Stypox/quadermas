package com.stypox.mastercom_workbook.data;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ClassData {
    private final String year;
    private final String id;

    @Nullable private List<DocumentData> documents;


    public ClassData(JSONObject json) throws JSONException {
        year = json.getString("year");
        id = json.getString("id");
    }

    public void setDocuments(@Nullable List<DocumentData> documents) {
        this.documents = documents;
    }


    @Nullable
    public List<DocumentData> getDocuments() {
        return documents;
    }

    public String getYear() {
        return year;
    }

    public String getId() {
        return id;
    }
}
