package com.stypox.mastercom_workbook.data;

import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SubjectData implements Serializable {
    private final String id;
    private final String name;
    private String teacher;

    @Nullable private List<MarkData> marks;
    @Nullable private ExtractorError markExtractionError;

    @Nullable private List<TopicData> topics;

    public SubjectData(final JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = JsonUtils.getUnescapedString(json, "nome");
    }


    public void setMarks(@Nullable final List<MarkData> marks) {
        if (marks != null) {
            for (final MarkData mark : marks) {
                mark.setSubject(name);
            }

            if (marks.isEmpty()) {
                teacher = null;
            } else {
                teacher = marks.get(0).getTeacher();
            }

            // sort from latest to oldest
            Collections.sort(marks, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        }

        this.marks = marks;
    }

    public void setMarkExtractionError(final ExtractorError extractorError) {
        this.markExtractionError = extractorError;
        teacher = null;
        marks = null;
    }

    public void setTopics(@Nullable final List<TopicData> topics) {
        if (topics != null) {
            for (final TopicData topic : topics) {
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


    public float getAverage(final SecondTermStart secondTermStart,
                            final int termToConsider) throws ArithmeticException {
        if (marks == null) {
            throw new ArithmeticException();
        }

        float marksSum = 0;
        int numberOfMarks = 0;

        for (final MarkData mark : marks) {
            if (mark.getValue().isNumber()
                    && secondTermStart.getTerm(mark.getDate()) == termToConsider) {
                marksSum += mark.getValue().getNumber();
                ++numberOfMarks;
            }
        }

        if (numberOfMarks == 0) {
            throw new ArithmeticException();
        }

        return marksSum / numberOfMarks;
    }

    public float getNeededMark(final SecondTermStart secondTermStart,
                               final float aimMark,
                               final int remainingTests) throws ArithmeticException {
        if (marks == null || remainingTests == 0) {
            throw new ArithmeticException();
        }

        float marksSum = 0;
        int numberOfMarks = 0;

        int currentTerm = secondTermStart.currentTerm();
        for (final MarkData mark : marks) {
            if (mark.getValue().isNumber()
                    && secondTermStart.getTerm(mark.getDate()) == currentTerm) {
                marksSum += mark.getValue().getNumber();
                ++numberOfMarks;
            }
        }

        return (aimMark * (numberOfMarks + remainingTests) - marksSum) / remainingTests;
    }
}
