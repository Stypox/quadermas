package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class TimetableEventData {

    private final String subject;
    private final String teacher;
    private final Date begin;
    private final Date end;

    public TimetableEventData(final JSONObject json) throws JSONException, ParseException {
        subject = JsonUtils.getUnescapedString(json, "materia");
        teacher = JsonUtils.getUnescapedString(json, "professore");
        begin = JsonUtils.getDateTime(json, "inizio");
        end = JsonUtils.getDateTime(json, "fine");
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public int getBeginDateAsIndex() {
        return DateUtils.dateToIndex(begin);
    }
}
