package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        sortMarksByDate();
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

    private void sortMarksByDate() {
        Collections.sort(items, new Comparator<MarkDetailItem>() {
            @Override
            public int compare(MarkDetailItem o1, MarkDetailItem o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }
    private void sortMarksByValue() {
        Collections.sort(items, new Comparator<MarkDetailItem>() {
            @Override
            public int compare(MarkDetailItem o1, MarkDetailItem o2) {
                return Float.compare(o2.getValue(), o1.getValue());
            }
        });
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
                sortMarksByDate();
                showMarks();
                break;
            case R.id.sortByValueAction:
                sortMarksByValue();
                showMarks();
                break;
        }
        return true;
    }
}
