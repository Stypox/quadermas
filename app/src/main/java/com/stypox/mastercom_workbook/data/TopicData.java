package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TopicData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ROOT);

    private final Date date;
    private final String teacher;
    private final String title;
    private final String description;
    private final String assignment;

    public TopicData(JSONObject json) throws JSONException, ParseException {
        date = dateFormat.parse(json.getString("data"));
        teacher = json.getString("docente");
        title = json.getString("modulo");
        description = json.getString("descrizione");
        assignment = json.getString("assegnazioni");
    }

    public Date getDate() {
        return date;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignment() {
        return assignment;
    }
}
