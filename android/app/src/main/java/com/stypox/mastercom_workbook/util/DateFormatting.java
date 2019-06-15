package com.stypox.mastercom_workbook.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatting {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
}
