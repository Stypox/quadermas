package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkData implements Serializable {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ROOT);

    private final MarkValue value;
    private final MarkType type;
    private final Date date;
    private final String description;
    private final String teacher;
    private String subject;

    public MarkData(JSONObject json) throws JSONException, ParseException {
        subject = null;
        value = new MarkValue(json.getString("valore"));
        type = MarkType.parseType(json.getString("tipo"));
        date = dateFormat.parse(json.getString("data"));
        description = json.getString("note");
        teacher = json.getString("docente");
    }

    public String getSubject() {
        return subject == null ? "" : subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MarkValue getValue() {
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
