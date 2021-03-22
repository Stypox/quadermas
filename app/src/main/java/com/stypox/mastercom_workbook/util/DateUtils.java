package com.stypox.mastercom_workbook.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final int endOfSchoolMonth = 7;

    public static int getTerm(Date date) {
        if (getCalendarField(date, Calendar.MONTH) > endOfSchoolMonth) {
            return 0; // first term
        } else {
            return 1; // second term
        }
    }

    public static int currentTerm() {
        return getTerm(new Date());
    }

    /**
     * Calculates the first year of the school year the date is in.
     * For example, if the date is in the school year 2018/2019,
     * the return value is 2018.
     */
    public static int schoolYear(final Date date) {
        final int year = getCalendarField(date, Calendar.YEAR);
        if (DateUtils.getTerm(date) == 1) {
            return year - 1;
        } else {
            return year;
        }
    }


    public static Date buildDate(final int year, final int month, final int dayOfMonth) {
        return new GregorianCalendar(year, month, dayOfMonth).getTime();
    }

    public static int getCalendarField(final Date date, final int field) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    public static int dateToIndex(final Date date) {
        return getCalendarField(date, Calendar.YEAR) * 10000
                + getCalendarField(date, Calendar.MONTH) * 100
                + getCalendarField(date, Calendar.DAY_OF_MONTH);
    }

    public static Date indexToDate(final int index) {
        return buildDate(index / 10000, (index / 100) % 100, index % 100);
    }

    public static Date addDaysToDate(final Date date, final int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static int addDaysToDateIndex(final int dateIndex, final int days) {
        return dateToIndex(addDaysToDate(indexToDate(dateIndex), days));
    }

    public static String dateIndexToSecondsSinceEpoch(final int index) {
        return String.valueOf(indexToDate(index).getTime() / 1000);
    }
}
