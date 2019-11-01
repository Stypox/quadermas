package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarkData implements Serializable {
    private final float value;
    private final MarkType type;
    private final Date date;
    private final String description;
    private final String teacher;

    private String subject;

    public MarkData(JSONObject json) throws JSONException, InvalidKeyException, ParseException {
        subject = null;
        value = Float.parseFloat(json.getString("valore"));
        type = MarkType.parseType(json.getString("tipo"));
        date = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").parse(json.getString("data"));
        description = json.getString("note");
        teacher = json.getString("docente");
    }

    public String getSubject() {
        return subject == null ? "" : subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public float getValue() {
        return value;
    }

    public MarkType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacher() {
        return teacher;
    }
}
