package com.stypox.mastercom_workbook;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.stypox.mastercom_workbook.data.SubjectData;

import org.json.JSONException;

public class Subject extends ConstraintLayout {
    private SubjectData data;

    private TextView nameView;
    private TextView teacherView;

    public Subject(Context context, SubjectData data) {
        super(context);

        this.data = data;

        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.subject, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        nameView = findViewById(R.id.name);
        teacherView = findViewById(R.id.teacher);

        nameView.setText(data.getName());
    }
}
