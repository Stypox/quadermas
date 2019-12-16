package com.stypox.mastercom_workbook.data;

import java.io.Serializable;

public class MarkValue implements Comparable<MarkValue>, Serializable {
    private Float numericalValue;
    private String textValue;

    public MarkValue(String textValue) {
        this.textValue = textValue;
        try {
            this.numericalValue = Float.parseFloat(textValue);
        } catch (NumberFormatException e) {
            this.numericalValue = null;
        }
    }

    public boolean isNumber() {
        return numericalValue != null;
    }

    public float getNumber() {
        return numericalValue;
    }

    public String getText() {
        return textValue;
    }


    @Override
    public int compareTo(MarkValue o) {
        if (!this.isNumber()) {
            return (o.isNumber() ? -1 : 0);
        } else if (!o.isNumber()) {
            return 1;
        }

        return this.numericalValue.compareTo(o.numericalValue);
    }
}
