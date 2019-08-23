package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.DateFormatting;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StatisticsActivity extends AppCompatActivity {
    public static final String subjectsIntentKey = "subjects";

    private ArrayList<SubjectData> subjects;
    private ArrayList<MarkData> marks;

    private Spinner overallAverageTermSpinner;
    private Spinner overallAverageModeSpinner;
    private TextView overallAverageTextView;
    private LineChart marksChart;
    private LinearLayout markLayout;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        subjects = (ArrayList<SubjectData>) getIntent().getSerializableExtra(subjectsIntentKey);
        buildMarksArray();

        overallAverageTermSpinner = findViewById(R.id.overallAverageTermSpinner);
        overallAverageModeSpinner = findViewById(R.id.overallAverageModeSpinner);
        overallAverageTextView = findViewById(R.id.overallAverageTextView);
        marksChart = findViewById(R.id.marksChart);
        markLayout = findViewById(R.id.markLayout);

        if (marks.isEmpty()) {
            finish();
        }
        if (subjects.size() == 1) {
            ConstraintLayout overallAverageLayout = findViewById(R.id.overallAverageLayout);
            TextView overallAverageModeTextView = findViewById(R.id.overallAverageModeTextView);
            View overallAverageDivider = findViewById(R.id.overallAverageDivider);

            overallAverageLayout.setVisibility(View.GONE);
            overallAverageModeTextView.setVisibility(View.GONE);
            overallAverageModeSpinner.setVisibility(View.GONE);
            overallAverageDivider.setVisibility(View.GONE);
        } else {
            overallAverageTermSpinner.setSelection(marks.isEmpty() ? 0 : marks.get(0).getTerm(), false);
        }

        setupListeners();

        fillMarksChart();
        formatMarksChart();
    }

    private void setupListeners() {
        overallAverageTermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    updateOverallAverage();
                } catch (ArithmeticException e) {
                    // change selection; should be safe since data is guaranteed to have at least one mark
                    if (position == 0) {
                        overallAverageTermSpinner.setSelection(1, true);
                    } else /* position == 1 */ {
                        overallAverageTermSpinner.setSelection(0, true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                overallAverageTermSpinner.setSelection(0, true);
            }
        });

        overallAverageModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateOverallAverage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                overallAverageModeSpinner.setSelection(0, true);
            }
        });

        marksChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                showMark((MarkData)e.getData());
            }

            @Override
            public void onNothingSelected() {
                hideMark();
            }
        });
    }


    private void buildMarksArray() {
        marks = new ArrayList<>();
        for(SubjectData subject : subjects) {
            marks.addAll(subject.getMarks());
        }

        // sort by date, so that marks[0].getTerm() returns the current term
        Collections.sort(marks, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
    }

    private void formatMarksChart() {
        marksChart.getDescription().setEnabled(false);
        marksChart.getLegend().setEnabled(false);

        XAxis xAxis = marksChart.getXAxis();
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DateFormatting.formatDate(new Date((long)value));
            }
        });

        if (!marks.isEmpty()) {
            int initialTerm = marks.get(0).getTerm();
            int index = 0;
            for(MarkData mark : marks) {
                if (mark.getTerm() != initialTerm) {
                    xAxis.setAxisMinimum(marks.get(index).getDate().getTime());
                }
                ++index;
            }
            xAxis.setAxisMaximum(marks.get(0).getDate().getTime());
        }

        ValueFormatter markFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return MarkFormatting.valueRepresentation(value);
            }
        };

        YAxis axisLeft = marksChart.getAxisLeft();
        axisLeft.setGranularity(0.5f);
        axisLeft.setLabelCount(15);
        axisLeft.setDrawGridLines(false);
        axisLeft.setValueFormatter(markFormatter);

        YAxis axisRight = marksChart.getAxisRight();
        axisRight.setGranularity(0.5f);
        axisRight.setLabelCount(15);
        axisRight.setDrawGridLines(false);
        axisRight.setValueFormatter(markFormatter);
    }
    private void fillMarksChart() {
        ArrayList<Entry> chartEntries = new ArrayList<>();
        for(MarkData mark : marks) {
            chartEntries.add(new Entry(mark.getDate().getTime(), mark.getValue(), mark));
        }
        Collections.sort(chartEntries, new EntryXComparator());

        LineDataSet chartDataSet = new LineDataSet(chartEntries, "Marks"); // TODO hardcoded string
        chartDataSet.setColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleHoleColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleRadius(getResources().getDimension(R.dimen.chartCircleRadius));
        chartDataSet.setDrawValues(false);

        LineData chartData = new LineData(chartDataSet);
        marksChart.setData(chartData);
        marksChart.invalidate();
    }

    private void showMark(MarkData markData) {
        markLayout.removeAllViews();
        markLayout.addView(new MarkDetailItem(getApplicationContext(), markData));
    }
    private void hideMark() {
        markLayout.removeAllViews();
    }


    private float getOverallAverageOfRoundedAverages(int term) {
        float sum = 0;
        int numberOfSubjects = 0;

        for(SubjectData subject : subjects) {
            if (!subject.getMarks().isEmpty()) {
                sum += Math.round(subject.getAverage(term));
                ++numberOfSubjects;
            }
        }

        return sum / numberOfSubjects;
    }
    private float getOverallAverageOfAverages(int term) {
        float sum = 0;
        int numberOfSubjects = 0;

        for(SubjectData subject : subjects) {
            if (!subject.getMarks().isEmpty()) {
                sum += subject.getAverage(term);
                ++numberOfSubjects;
            }
        }

        return sum / numberOfSubjects;
    }
    private float getOverallAverageOfMarks(int term) {
        float sum = 0;
        int numberOfMarks = 0;

        for(MarkData mark : marks) {
            if (mark.getTerm() == term) {
                sum += mark.getValue();
                ++numberOfMarks;
            }
        }

        if (numberOfMarks == 0)
            throw new ArithmeticException();
        return sum / numberOfMarks;
    }

    private void updateOverallAverage() throws ArithmeticException {
        float overallAverage;
        int term = overallAverageTermSpinner.getSelectedItemPosition();

        switch (overallAverageModeSpinner.getSelectedItemPosition()) {
            case 0:
                overallAverage = getOverallAverageOfRoundedAverages(term);
                break;
            case 1:
                overallAverage = getOverallAverageOfAverages(term);
                break;
            case 2:
                overallAverage = getOverallAverageOfMarks(term);
                break;
            default:
                throw new ArithmeticException(); // just in case
        }

        overallAverageTextView.setText(MarkFormatting.floatToString(overallAverage, 3));
        overallAverageTextView.setTextColor(MarkFormatting.colorOf(getApplicationContext(), overallAverage));
    }
}
