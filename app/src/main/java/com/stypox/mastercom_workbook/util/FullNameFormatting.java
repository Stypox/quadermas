package com.stypox.mastercom_workbook.util;

public class FullNameFormatting {
    public static String capitalize(String fullNameUppercase) {
        char[] chars = fullNameUppercase.toLowerCase().toCharArray();
        boolean afterSpace = true;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i]) && afterSpace) {
                chars[i] = Character.toUpperCase(chars[i]);
                afterSpace = false;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '\'') {
                afterSpace = true;
            }
        }
        return String.valueOf(chars);
    }
}
