package com.stypox.mastercom_workbook.util;

import android.content.Context;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkType;
import com.stypox.mastercom_workbook.data.MarkValue;

import static com.stypox.mastercom_workbook.util.ThemedActivity.resolveColor;

public class MarkFormatting {

    public static String floatToString(float f, int maxLength) {
        int nonDecimalDigits = String.valueOf((int) f).length();
        if (nonDecimalDigits > maxLength) {
            throw new IllegalArgumentException();
        } else {
            int decimalDigits = maxLength - nonDecimalDigits;
            String string = String.format("%." + decimalDigits + "f", f + Math.pow(10, -decimalDigits - 2)); // sum is to prevent floating number problems when the number ends with 5

            // remove padding zeros
            while (decimalDigits > 0) {
                if (string.endsWith("0")) {
                    string = string.substring(0, string.length() - (decimalDigits == 1 ? 2 : 1)); // also remove point
                    --decimalDigits;
                } else {
                    break;
                }
            }
            return string;
        }
    }


    public static int colorOf(Context context, float value) {
        if (value < 6) {
            return resolveColor(context, R.attr.color_mark_failing);
        } else if (value < 8) {
            return resolveColor(context, R.attr.color_mark_halfway);
        } else {
            return resolveColor(context, R.attr.color_mark_excellent);
        }
    }

    public static int colorOf(Context context, MarkValue markValue) {
        if (markValue.isNumber()) {
            return colorOf(context, markValue.getNumber());
        } else {
            return resolveColor(context, R.attr.color_mark_not_classified);
        }
    }


    public static String valueRepresentation(float value) {
        float quarterPrecision = ((float) Math.round(value * 4)) / 4; // 0.25 intervals: 0.0; 0.25; 0.5; 0.75; 1.0; ...
        int baseValue = (int) Math.floor(quarterPrecision);

        float delta = quarterPrecision - baseValue;
        if (delta == 0.00) {
            return String.valueOf(baseValue);
        } else if (delta == 0.25) {
            return baseValue + "+";
        } else if (delta == 0.50) {
            return baseValue + "½";
        } else {//(delta == 0.75)
            return (baseValue + 1) + "-";
        }
    }

    public static String valueRepresentation(MarkValue markValue) {
        if (markValue.isNumber()) {
            return valueRepresentation(markValue.getNumber());
        } else {
            return markValue.getText();
        }
    }


    public static String typeRepresentation(Context context, MarkType markType) {
        switch (markType) {
            case written:
                return context.getString(R.string.type_written);
            case oral:
                return context.getString(R.string.type_oral);
            case practical:
                return context.getString(R.string.type_practical);
            default:
                return ""; // useless
        }
    }
}
