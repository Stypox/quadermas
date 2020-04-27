package com.stypox.mastercom_workbook.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class SubjectData implements Serializable {
    private final String id;
    private final String name;
    private String teacher;

    @Nullable private List<MarkData> marks;
    @Nullable private ExtractorError markExtractionError;

    @Nullable private List<TopicData> topics;

    public SubjectData(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = JsonUtils.getUnescapedString(json, "nome");
    }


    public void setMarks(@Nullable List<MarkData> marks) {
        if (marks != null) {
            for (MarkData mark : marks) {
                mark.setSubject(name);
            }

            if (marks.isEmpty()) {
                teacher = null;
            } else {
                teacher = marks.get(0).getTeacher();
            }
        }

        this.marks = marks;
    }

    public void setMarkExtractionError(ExtractorError extractorError) {
        this.markExtractionError = extractorError;
        teacher = null;
        marks = null;
    }

    public void setTopics(@Nullable List<TopicData> topics) {
        if (topics != null) {
            for (TopicData topic : topics) {
                topic.setSubject(name);
            }
        }

        this.topics = topics;
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

    @Nullable
    public List<MarkData> getMarks() {
        return marks;
    }

    @Nullable
    public ExtractorError getMarkExtractionError() {
        return markExtractionError;
    }

    @Nullable
    public List<TopicData> getTopics() {
        return topics;
    }


    public float getAverage(int termToConsider) throws ArithmeticException {
        float marksSum = 0;
        int numberOfMarks = 0;

        for (MarkData mark : marks) {
            if (DateUtils.getTerm(mark.getDate()) == termToConsider && mark.getValue().isNumber()) {
                marksSum += mark.getValue().getNumber();
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
            if (DateUtils.getTerm(mark.getDate()) == currentTerm && mark.getValue().isNumber()) {
                marksSum += mark.getValue().getNumber();
                ++numberOfMarks;
            }
        }

        return (aimMark * (numberOfMarks + remainingTests) - marksSum) / remainingTests;
    }
}
