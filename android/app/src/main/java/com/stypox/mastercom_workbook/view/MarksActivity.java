package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;

import java.util.ArrayList;

public class MarksActivity extends AppCompatActivity {
    public static final String subjectsIntentKey = "subjects";

    private ArrayList<SubjectData> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subjects = (ArrayList<SubjectData>) getIntent().getSerializableExtra(subjectsIntentKey);
    }
}
