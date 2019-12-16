package com.stypox.mastercom_workbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.MarkFormatting;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.MarkItemHolder;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData data;

    private Toolbar toolbar;
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        data = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        if (data.getMarks().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SubjectActivity with 0 marks");
        }


        actionBar.setTitle(data.getName());
        actionBar.setSubtitle(data.getTeacher());


        termSpinner = findViewById(R.id.termSpinner);
        averageView = findViewById(R.id.averageTextView);
        aimMarkEdit = findViewById(R.id.aimMarkEdit);
        remainingTestsEdit = findViewById(R.id.remainingTestsEdit);
        neededMarkView = findViewById(R.id.neededMarkTextView);

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
        marksLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        marksLayout.setAdapter(new ItemArrayAdapter<>(R.layout.item_mark, data.getMarks(), new MarkItemHolder.Factory()));

        setupListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject, menu);

        TextView titleView = (TextView) toolbar.getChildAt(0);
        titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);

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
            case R.id.showMarksAction:
                openMarksActivity();
                return true;
            case R.id.showStatisticsAction:
                openStatisticsActivity();
                return true;
            default:
                return false;
        }
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

    private void openMarksActivity() {
        Intent intent = new Intent(this, MarksActivity.class);
        ArrayList<SubjectData> subjects = new ArrayList<>();
        subjects.add(this.data);
        intent.putExtra(MarksActivity.subjectsIntentKey, subjects);
        startActivity(intent);
    }

    private void openStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        ArrayList<SubjectData> subjects = new ArrayList<>();
        subjects.add(this.data);
        intent.putExtra(StatisticsActivity.subjectsIntentKey, subjects);
        startActivityForResult(intent, 0);
    }



    /////////////
    // AVERAGE //
    /////////////

    private void updateAverage() throws ArithmeticException {
        try {
            float average = data.getAverage(termSpinner.getSelectedItemPosition());
            averageView.setText(MarkFormatting.floatToString(average, 3));
            averageView.setTextColor(MarkFormatting.colorOf(getApplicationContext(), average));
        } catch (Throwable e) {
            averageView.setText("");
        }
    }

    private void updateNeededMark() {
        try {
            float neededMark = data.getNeededMark(Float.valueOf(aimMarkEdit.getText().toString()), Integer.valueOf(remainingTestsEdit.getText().toString()));
            neededMarkView.setText(MarkFormatting.floatToString(neededMark, 3));
        } catch (Throwable e) {
            neededMarkView.setText("");
        }
    }

}
