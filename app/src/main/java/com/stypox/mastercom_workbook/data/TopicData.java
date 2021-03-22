package com.stypox.mastercom_workbook.data;

import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class TopicData {

    private final Date date;
    private final String teacher;
    private final String title;
    private final String description;
    private final String assignment;
    @Nullable private String subject;

    public TopicData(JSONObject json) throws JSONException, ParseException {
        date = JsonUtils.getDateTime(json, "data");
        teacher = JsonUtils.getUnescapedString(json, "docente");
        title = JsonUtils.getUnescapedString(json, "modulo");
        description = JsonUtils.getUnescapedString(json, "descrizione");
        assignment = JsonUtils.getUnescapedString(json, "assegnazioni");
    }


    void setSubject(@Nullable String subject) {
        this.subject = subject;
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

    public String getSubject() {
        return subject == null ? "" : subject;
    }
}
