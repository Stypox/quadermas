package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.util.FullNameFormatting;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * The object that represents a single event
 */
public class EventData {

    public enum Type {
        annotation, event
    }

    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd MMM HH:mm", Locale.ITALY);

    private final Type type;
    private final String title;
    private final String description;
    private final String teacher;

    private final Date begin;
    private final Date end;

    public EventData(final Type type,
                     final String title,
                     final String description,
                     final String teacher,
                     final String day,
                     final String month,
                     final String dateTime) throws ExtractorError {

        this.type = type;
        // swap title with description if title is empty
        this.title = title.isEmpty() ? description : title;
        this.description = title.isEmpty() ? title : description;
        this.teacher = FullNameFormatting.capitalize(teacher);

        final String[] dateTimePieces = dateTime.split(" ");
        try {
            if (dateTimePieces[0].contains(":")) {
                // <hour start>:<minute start> <hour end>:<minute end>
                begin = DATE_FORMAT.parse(day + " " + month + " " + dateTimePieces[0]);
                end = DATE_FORMAT.parse(day + " " + month + " " + dateTimePieces[1]);
            } else {
                // <full date start> <full date end> (with other spaces in between)
                begin = DATE_FORMAT.parse(
                        dateTimePieces[1] + " " + dateTimePieces[2] + " " + dateTimePieces[3]);
                end = DATE_FORMAT.parse(
                        dateTimePieces[5] + " " + dateTimePieces[6] + " " + dateTimePieces[7]);
            }

            if (begin == null || end == null) {
                throw new NullPointerException();
            }

            begin.setYear(SecondTermStart.yearFromMonth(begin.getMonth() + 1) - 1900);
            end.setYear(SecondTermStart.yearFromMonth(end.getMonth() + 1) - 1900);
        } catch (final Throwable e) {
            throw new ExtractorError(ExtractorError.Type.unsuitable_date, e);
        }
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getDescription() {
        return description;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EventData eventData = (EventData) o;
        return type == eventData.type &&
                title.equals(eventData.title) &&
                description.equals(eventData.description) &&
                teacher.equals(eventData.teacher) &&
                begin.equals(eventData.begin) &&
                end.equals(eventData.end);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{type, title, description, teacher, begin, end});
    }
}
