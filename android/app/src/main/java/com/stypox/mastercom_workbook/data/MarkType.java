package com.stypox.mastercom_workbook.data;

import java.security.InvalidKeyException;

public enum MarkType {
    written,
    oral,
    practical;

    public static MarkType parseType(String string) throws InvalidKeyException {
        // from the strings provided in the `get_grades_subject` json
        switch (string) {
            case "Scritto":
                return written;
            case "Orale":
                return oral;
            case "Pratico":
                return practical;
            default:
                throw new InvalidKeyException("string must be one of \"Scritto\", \"Orale\" and \"Pratico\": " + string);
        }
    }
}
