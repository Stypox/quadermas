package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.FetchMarksCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SubjectData implements Serializable {
    private final String id;
    private final String name;
    private String teacher;
    private ArrayList<MarkData> marks;

    public SubjectData(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = json.getString("nome");
    }

    public void fetchMarks(final FetchMarksCallback callback) {
        Extractor.fetchMarks(id, new FetchMarksCallback() {
            @Override
            public void onFetchMarksCompleted(ArrayList<MarkData> marks) {
                SubjectData.this.marks = marks;
                if(marks.isEmpty()) {
                    teacher = null;
                } else {
                    teacher = marks.get(0).getTeacher();
                }
                callback.onFetchMarksCompleted(marks);
            }

            @Override
            public void onError(Extractor.Error error) {
                callback.onError(error);
            }
        });
    }

    public String getName() {
        return name;
    }
    public String getTeacher() {
        return teacher;
    }
    public ArrayList<MarkData> getMarks() {
        return marks;
    }
}
