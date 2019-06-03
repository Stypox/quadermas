package com.stypox.mastercom_workbook;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.view.MarkItem;

import java.util.Calendar;

public class SubjectActivity extends AppCompatActivity {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData data;

    private LinearLayout marksLayout;
    private Spinner termSpinner;
    private TextView averageTextView;
    private EditText aimMarkEdit;
    private EditText remainingTestsEdit;
    private TextView neededMarkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        marksLayout = findViewById(R.id.marksLayout);
        termSpinner = findViewById(R.id.termSpinner);
        averageTextView = findViewById(R.id.averageTextView);
        aimMarkEdit = findViewById(R.id.aimMarkEdit);
        remainingTestsEdit = findViewById(R.id.remainingTestsEdit);
        neededMarkTextView = findViewById(R.id.neededMarkTextView);

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    updateAverage();
                } catch (ArithmeticException e) {
                    e.printStackTrace();

                    // change selection
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
        termSpinner.setSelection(data.getMarks().get(0).getTerm(), false);

        aimMarkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    updateNeededMark();
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
                try {
                    if (Integer.valueOf(s.toString()) <= 0) {
                        s.clear();
                    } else {
                        updateNeededMark();
                    }
                } catch (NumberFormatException e) {}
            }
        });
        updateNeededMark();

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

    private float getAverage(int termToConsider) throws ArithmeticException {
        float marksSum = 0;
        int numberOfMarks = 0;

        for (MarkData mark : data.getMarks()) {
            if (mark.getTerm() == termToConsider) {
                marksSum += mark.getValue();
                ++numberOfMarks;
            }
        }

        if (numberOfMarks == 0)
            throw new ArithmeticException();

        return marksSum / numberOfMarks;
    }
    private void updateAverage() throws ArithmeticException {
        float average = getAverage(termSpinner.getSelectedItemPosition());
        String averageString = String.valueOf(average); // TODO fix strings
        averageTextView.setText(averageString.substring(0, Math.min(4, averageString.length())));
    }

    private float getNeededMark(float aimMark, int remainingTests) {
        float marksSum = 0;
        int numberOfMarks = 0;

        int currentTerm = MarkData.currentTerm();
        for (MarkData mark : data.getMarks()) {
            if (mark.getTerm() == currentTerm) {
                marksSum += mark.getValue();
                ++numberOfMarks;
            }
        }

        return (aimMark*(numberOfMarks + remainingTests) - marksSum) / remainingTests;
    }
    private void updateNeededMark() {
        float neededMark = getNeededMark(Float.valueOf(aimMarkEdit.getText().toString()), Integer.valueOf(remainingTestsEdit.getText().toString()));
        String neededMarkString = String.valueOf(neededMark); // TODO fix strings
        neededMarkTextView.setText(neededMarkString.substring(0, Math.min(4, neededMarkString.length())));
    }
}
