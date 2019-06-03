package com.stypox.mastercom_workbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.view.MarkItem;

public class SubjectActivity extends AppCompatActivity {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData data;
    private LinearLayout marksLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        marksLayout = findViewById(R.id.marksLayout);

        showInfo();
        showMarks();
    }

    private void showInfo() {
        ((TextView)findViewById(R.id.subject_name)).setText(data.getName());
        ((TextView)findViewById(R.id.teacher)).setText(data.getTeacher());
    }
    private void showMarks() {
        marksLayout.removeAllViews();
        for (MarkData mark : data.getMarks()) {
            marksLayout.addView(new MarkItem(getApplicationContext(), mark));
        }
    }
}
