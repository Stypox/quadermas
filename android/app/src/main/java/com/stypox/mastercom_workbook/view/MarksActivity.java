package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;

import java.util.ArrayList;

public class MarksActivity extends AppCompatActivity
    implements Toolbar.OnMenuItemClickListener {
    public static final String subjectsIntentKey = "subjects";

    private ArrayList<SubjectData> subjects;

    private LinearLayout marksLayout;

    private ArrayList<MarkDetailItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        subjects = (ArrayList<SubjectData>) getIntent().getSerializableExtra(subjectsIntentKey);

        marksLayout = findViewById(R.id.marksLayout);

        buildItemsArray();
        showMarks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marks, menu);
        return true;
    }

    private void buildItemsArray() {
        items = new ArrayList<>();
        for (SubjectData subject : subjects) {
            for (MarkData mark : subject.getMarks()) {
                items.add(new MarkDetailItem(getApplicationContext(), mark));
            }
        }
    }

    private void showMarks() {
        marksLayout.removeAllViews();
        for (MarkDetailItem item : items) {
            marksLayout.addView(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.sortByDateAction:
                Snackbar.make(findViewById(android.R.id.content), "sort by date", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.sortByValueAction:
                Snackbar.make(findViewById(android.R.id.content), "sort by value", Snackbar.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}
