package com.stypox.mastercom_workbook.data;

import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

public class MarkData implements Serializable {

    private final MarkValue value;
    private final MarkType type;
    private final Date date;
    private final String description;
    private final String teacher;
    @Nullable private String subject;

    public MarkData(JSONObject json) throws JSONException, ParseException {
        subject = null;
        value = new MarkValue(json.getString("valore"));
        type = MarkType.parseType(json.getString("tipo"));
        date = JsonUtils.getDateTime(json, "data");
        description = JsonUtils.getUnescapedString(json, "note");
        teacher = JsonUtils.getUnescapedString(json, "docente");
    }


    void setSubject(@Nullable String subject) {
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

    public String getSubject() {
        return subject == null ? "" : subject;
    }
}
