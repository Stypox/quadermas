package com.stypox.mastercom_workbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.MarkFormatting;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.MarkItemHolder;

import java.util.ArrayList;

public class SubjectActivity extends ThemedActivity {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData data;

    private Spinner termSpinner;
    private TextView averageView;
    private EditText aimMarkEdit;
    private EditText remainingTestsEdit;
    private TextView neededMarkView;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        data = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        assert data != null;
        if (data.getMarks() == null || data.getMarks().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SubjectActivity with 0 marks");
        }


        actionBar.setTitle(data.getName());
        actionBar.setSubtitle(data.getTeacher());


        termSpinner = findViewById(R.id.termSpinner);
        averageView = findViewById(R.id.averageTextView);
        aimMarkEdit = findViewById(R.id.aimMarkEdit);
        remainingTestsEdit = findViewById(R.id.remainingTestsEdit);
        neededMarkView = findViewById(R.id.neededMarkTextView);
        View marksButton = findViewById(R.id.marksButton);
        View statisticsButton = findViewById(R.id.statisticsButton);
        View topicsButton = findViewById(R.id.topicsButton);

        marksButton.setOnClickListener((v) -> openMarksActivity());
        statisticsButton.setOnClickListener((v) -> openStatisticsActivity());
        topicsButton.setOnClickListener((v) -> openTopicsActivity());


        int selectedTerm = DateUtils.getTerm(data.getMarks().get(0).getDate());
        termSpinner.setSelection(selectedTerm, false);
        try {
            aimMarkEdit.setText(String.valueOf(Math.max(6, (int)Math.ceil(data.getAverage(selectedTerm)))));
        } catch (ArithmeticException e) {
            aimMarkEdit.setText(String.valueOf(6));
        }

        updateAverage();
        updateNeededMark();


        RecyclerView marksLayout = findViewById(R.id.marksList);
        marksLayout.setAdapter(new ItemArrayAdapter<>(R.layout.item_mark, data.getMarks(), new MarkItemHolder.Factory()));
        setupListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupListeners() {
        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    updateAverage();
                } catch (ArithmeticException e) {
                    // change selection; should be safe since data is guaranteed to have at least one mark
                    if (position == 0) {
                        termSpinner.setSelection(1, true);
                    } else /* position == 1 */ {
                        termSpinner.setSelection(0, true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                termSpinner.setSelection(0, true);
            }
        });

        aimMarkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateNeededMark();
            }
        });

        remainingTestsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && s.charAt(0) == '0') {
                    s.clear();
                }
                updateNeededMark();
            }
        });
    }

    private ArrayList<SubjectData> getSubjectAsList() {
        ArrayList<SubjectData> subjects = new ArrayList<>();
        subjects.add(this.data);
        return subjects;
    }

    private void openMarksActivity() {
        Intent intent = new Intent(this, MarksActivity.class);
        intent.putExtra(MarksActivity.subjectsIntentKey, getSubjectAsList());
        startActivity(intent);
    }

    private void openStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(StatisticsActivity.subjectsIntentKey, getSubjectAsList());
        startActivity(intent);
    }

    private void openTopicsActivity() {
        Intent intent = new Intent(this, TopicsActivity.class);
        intent.putExtra(TopicsActivity.subjectsIntentKey, getSubjectAsList());
        startActivity(intent);
    }



    /////////////
    // AVERAGE //
    /////////////

    private void updateAverage() throws ArithmeticException {
        try {
            float average = data.getAverage(termSpinner.getSelectedItemPosition());
            averageView.setText(MarkFormatting.floatToString(average, 3));
            averageView.setTextColor(MarkFormatting.colorOf(this, average));
        } catch (Throwable e) {
            averageView.setText("");
        }
    }

    private void updateNeededMark() {
        try {
            float neededMark = data.getNeededMark(Float.parseFloat(aimMarkEdit.getText().toString()),
                    Integer.parseInt(remainingTestsEdit.getText().toString()));
            neededMarkView.setText(MarkFormatting.floatToString(neededMark, 3));
        } catch (Throwable e) {
            neededMarkView.setText("");
        }
    }

}
