package com.stypox.mastercom_workbook;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Subject extends ConstraintLayout {
    private String name;
    private String id;

    private TextView nameView;
    private TextView teacherView;

    Subject(Context context, JSONObject data) throws JSONException {
        super(context);

        this.name = data.getString("nome");
        this.id = data.getString("id");

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

        nameView.setText(name);
    }
}
