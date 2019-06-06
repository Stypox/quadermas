package com.stypox.mastercom_workbook.util;

import android.content.Context;

import com.stypox.mastercom_workbook.R;

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

    public static int colorOf(Context context, float mark) {
        if (mark < 6) {
            return context.getResources().getColor(R.color.failingMark);
        } else if (mark < 8) {
            return context.getResources().getColor(R.color.halfwayMark);
        } else {
            return context.getResources().getColor(R.color.excellentMark);
        }
    }
}
