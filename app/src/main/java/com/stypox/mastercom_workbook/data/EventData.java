package com.stypox.mastercom_workbook.data;

/**
 * The object that represents a single event
 */
public class EventData {
    private final String title;
    private final String teacher;
    private final String description;

    private final String day;
    private final String month;

    private final String timeEnd;
    private final String timeStart;

    public EventData(final String title,
                     final String description,
                     final String teacher,
                     final String day,
                     final String month,
                     final String timeStart,
                     final String timeEnd) {

        this.title = title;
        this.description = description;
        this.teacher = teacher;
        this.day = day;
        this.month = month;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
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

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }
}
