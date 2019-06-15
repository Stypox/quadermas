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

    public static String valueRepresentation(float value) {
        float quarterPrecision = ((float)Math.round(value*4))/4; // 0.25 intervals: 0.0; 0.25; 0.5; 0.75; 1.0; ...
        int baseValue = (int)Math.floor(quarterPrecision);

        float delta = quarterPrecision-baseValue;
        if        (delta == 0.00) {
            return String.valueOf(baseValue);
        } else if (delta == 0.25) {
            return baseValue + "+";
        } else if (delta == 0.50) {
            return baseValue + "½";
        } else {//(delta == 0.75)
            return (baseValue+1) + "-";
        }
    }
}
