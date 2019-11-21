package com.stypox.mastercom_workbook.data;

public enum MarkType {
    written,
    oral,
    practical;

    public static MarkType parseType(String string) {
        // from the strings provided in the `get_grades_subject` json
        switch (string) {
            case "Scritto":
                return written;
            case "Pratico":
                return practical;
            case "Orale": default:
                return oral;
        }
    }
}
