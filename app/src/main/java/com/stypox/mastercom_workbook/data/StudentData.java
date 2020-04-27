package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentData {
    private final List<ClassData> classes;

    public StudentData(JSONObject json, ItemErrorHandler itemErrorHandler) throws JSONException {
        classes = new ArrayList<>();
        JSONObject channels = json.getJSONObject("result").getJSONObject("channels");

        JSONArray studenti = channels.getJSONArray("studenti");
        for (int i = 0; i < studenti.length(); ++i) {
            try {
                classes.add(new ClassData(studenti.getJSONObject(i)));
            } catch (Throwable throwable) {
                itemErrorHandler.onItemError(throwable);
            }
        }

        JSONArray professori = channels.getJSONArray("professori");
        for (int i = 0; i < professori.length(); ++i) {
            try {
                classes.add(new ClassData(professori.getJSONObject(i)));
            } catch (Throwable throwable) {
                itemErrorHandler.onItemError(throwable);
            }
        }
    }

    public List<ClassData> getClasses() {
        return classes;
    }

    public List<ClassData> getClassesForYear(String year) {
        List<ClassData> classesForYear = new ArrayList<>();
        for (ClassData classData : classes) {
            if (year.equals(classData.getYear())) {
                classesForYear.add(classData);
            }
        }
        return classesForYear;
    }
}
