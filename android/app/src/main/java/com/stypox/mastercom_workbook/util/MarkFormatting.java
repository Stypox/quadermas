package com.stypox.mastercom_workbook.util;

public class MarkFormatting {
    public static String floatToString(float f, int maxLength) {
        if (f > Math.pow(10, maxLength-1)) {
            throw new IllegalArgumentException();
        }

        String str = String.valueOf(f);
        str = str.substring(0, Math.min(maxLength, str.length()));
        if (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }
}
