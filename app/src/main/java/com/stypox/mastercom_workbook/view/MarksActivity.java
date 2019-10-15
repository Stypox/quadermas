package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.MarkDetailItemHolder;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends AppCompatActivity
    implements Toolbar.OnMenuItemClickListener {
    public static final String subjectsIntentKey = "subjects";

    private ItemArrayAdapter<MarkData> marksArrayAdapter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        List<SubjectData> subjects = (ArrayList<SubjectData>) getIntent().getSerializableExtra(subjectsIntentKey);
        ArrayList<MarkData> marks = new ArrayList<>();
        for (SubjectData subject : subjects) {
            marks.addAll(subject.getMarks());
        }

        ListView marksView = findViewById(R.id.marksList);
        marksArrayAdapter = new ItemArrayAdapter<>(this, R.layout.item_mark_detail, marks, new MarkDetailItemHolder.Factory());
        marksView.setAdapter(marksArrayAdapter);

        sortMarksByDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marks, menu);
        return true;
    }

    private void sortMarksByDate() {
        marksArrayAdapter.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
    }
    private void sortMarksByValue() {
        marksArrayAdapter.sort((o1, o2) -> Float.compare(o2.getValue(), o1.getValue()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.sortByDateAction:
                sortMarksByDate();
                return true;
            case R.id.sortByValueAction:
                sortMarksByValue();
                return true;
            default:
                return false;
        }
    }
}
