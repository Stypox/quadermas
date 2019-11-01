package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class SubjectData implements Serializable {
    private final String id;
    private final String name;
    private String teacher;
    private List<MarkData> marks;
    private ExtractorError extractorError;

    public SubjectData(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = json.getString("nome");
    }

    public void setMarks(List<MarkData> marks) {
        for (MarkData mark : marks) {
            mark.setSubject(name);
        }

        this.marks = marks;
        if(this.marks.isEmpty()) {
            teacher = null;
        } else {
            teacher = this.marks.get(0).getTeacher();
        }
    }

    public void setError(ExtractorError extractorError) {
        this.extractorError = extractorError;
        teacher = null;
        marks = null;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getTeacher() {
        return teacher;
    }
    public List<MarkData> getMarks() {
        return marks;
    }
    public ExtractorError getError() { return extractorError; }

    public float getAverage(int termToConsider) throws ArithmeticException {
        float marksSum = 0;
        int numberOfMarks = 0;

        for (MarkData mark : marks) {
            if (DateUtils.getTerm(mark.getDate()) == termToConsider) {
                marksSum += mark.getValue();
                ++numberOfMarks;
            }
        }

        if (numberOfMarks == 0)
            throw new ArithmeticException();

        return marksSum / numberOfMarks;
    }
    public float getNeededMark(float aimMark, int remainingTests) {
        float marksSum = 0;
        int numberOfMarks = 0;

        int currentTerm = DateUtils.currentTerm();
        for (MarkData mark : marks) {
            if (DateUtils.getTerm(mark.getDate()) == currentTerm) {
                marksSum += mark.getValue();
                ++numberOfMarks;
            }
        }

        return (aimMark*(numberOfMarks + remainingTests) - marksSum) / remainingTests;
    }
}
