package com.stypox.mastercom_workbook.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
    private static final int endOfSchoolMonth = 7;

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }


    public static int getTerm(Date date) {
        if (date.getMonth() > endOfSchoolMonth) {
            return 0; // first term
        } else {
            return 1; // second term
        }
    }

    public static int currentTerm() {
        return getTerm(Calendar.getInstance().getTime());
    }

    /**
     * Calculates the first year of the school year the date is in.
     * For example, if the date is in the school year 2018/2019,
     * the return value is 2018.
     */
    public static int schoolYear(Date date) {
        int year = new GregorianCalendar() {{ setTime(date); }}.get(Calendar.YEAR);
        if (DateUtils.getTerm(date) == 1) {
            return year - 1;
        } else {
            return year;
        }
    }
}
