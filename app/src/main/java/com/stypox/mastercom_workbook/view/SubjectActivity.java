package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.stypox.mastercom_workbook.settings.NeededMark;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.util.MarkFormatting;
import com.stypox.mastercom_workbook.util.NavigationHelper;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.MarkItemHolder;

import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivityWithSubject;

public class SubjectActivity extends ThemedActivity {

    private SubjectData subject;

    private SecondTermStart secondTermStart;

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


        subject = NavigationHelper.getSelectedSubjects(getIntent()).get(0);
        if (subject.getMarks() == null || subject.getMarks().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SubjectActivity with 0 marks");
        }
        actionBar.setTitle(subject.getName());
        actionBar.setSubtitle(subject.getTeacher());

        secondTermStart = SecondTermStart.fromPreferences(this);


        termSpinner = findViewById(R.id.termSpinner);
        averageView = findViewById(R.id.averageTextView);
        aimMarkEdit = findViewById(R.id.aimMarkEdit);
        remainingTestsEdit = findViewById(R.id.remainingTestsEdit);
        neededMarkView = findViewById(R.id.neededMarkTextView);

        findViewById(R.id.marksButton).setOnClickListener(
                (v) -> openActivityWithSubject(this, MarksActivity.class, subject));
        findViewById(R.id.statisticsButton).setOnClickListener(
                (v) -> openActivityWithSubject(this, StatisticsActivity.class, subject));
        findViewById(R.id.topicsButton).setOnClickListener(
                (v) -> openActivityWithSubject(this, TopicsActivity.class, subject));

        termSpinner.setSelection(secondTermStart.currentTerm());
        aimMarkEdit.setText(NeededMark.aimMarkForSubject(this, subject));
        remainingTestsEdit.setText(NeededMark.remainingTestsForSubject(this, subject));

        updateAverage();
        updateNeededMark();


        RecyclerView marksLayout = findViewById(R.id.marksList);
        marksLayout.setAdapter(new ItemArrayAdapter<>(R.layout.item_mark, subject.getMarks(), MarkItemHolder.getFactory()));
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

                if (!TextUtils.isEmpty(aimMarkEdit.getText())) {
                    NeededMark.saveAimMarkForSubject(SubjectActivity.this, subject,
                            aimMarkEdit.getText().toString());
                }
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

                if (!TextUtils.isEmpty(remainingTestsEdit.getText())) {
                    NeededMark.saveRemainingTestsForSubject(SubjectActivity.this, subject,
                            remainingTestsEdit.getText().toString());
                }
            }
        });
    }



    /////////////
    // AVERAGE //
    /////////////

    private void updateAverage() throws ArithmeticException {
        try {
            final float average = subject.getAverage(secondTermStart,
                    termSpinner.getSelectedItemPosition());
            averageView.setText(MarkFormatting.floatToString(average, 3));
            averageView.setTextColor(MarkFormatting.colorOf(this, average));
        } catch (Throwable e) {
            averageView.setText("");
        }
    }

    private void updateNeededMark() {
        try {
            final float neededMark = subject.getNeededMark(secondTermStart,
                    Float.parseFloat(aimMarkEdit.getText().toString()),
                    Integer.parseInt(remainingTestsEdit.getText().toString()));
            neededMarkView.setText(MarkFormatting.floatToString(neededMark, 3));
        } catch (Throwable e) {
            neededMarkView.setText("");
        }
    }

}
