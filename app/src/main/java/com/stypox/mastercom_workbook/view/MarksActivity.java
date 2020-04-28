package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.NavigationHelper;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.MarkDetailItemHolder;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends ThemedActivity
        implements Toolbar.OnMenuItemClickListener {

    private ItemArrayAdapter<MarkData> marksArrayAdapter;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_title_marks));


        List<SubjectData> subjects = NavigationHelper.getSelectedSubjects(getIntent());
        ArrayList<MarkData> marks = new ArrayList<>();
        for (SubjectData subject : subjects) {
            assert subject.getMarks() != null;
            marks.addAll(subject.getMarks());
        }
        if (subjects.size() == 1) {
            actionBar.setSubtitle(subjects.get(0).getName());
        }

        RecyclerView marksView = findViewById(R.id.marksList);
        marksView.setLayoutManager(new LinearLayoutManager(this));
        marksArrayAdapter = new ItemArrayAdapter<>(R.layout.item_mark_detail, marks, MarkDetailItemHolder.getFactory());
        marksView.setAdapter(marksArrayAdapter);

        sortMarksByDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marks, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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


    ///////////////////
    // SORTING MARKS //
    ///////////////////

    private void sortMarksByDate() {
        marksArrayAdapter.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
    }

    private void sortMarksByValue() {
        marksArrayAdapter.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    }
}
