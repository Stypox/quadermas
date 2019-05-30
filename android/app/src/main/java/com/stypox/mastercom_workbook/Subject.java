package com.stypox.mastercom_workbook;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;

public class SubjectView extends ConstraintLayout {
    SubjectView(Context context) {
        super(context);
        inflate(context);
    }

    private void inflate(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.subject, this);
        onFinishInflate();
    }
}
