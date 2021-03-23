package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

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
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.MarkFormatting;
import com.stypox.mastercom_workbook.util.NavigationHelper;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.MarkDetailItemHolder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsActivity extends ThemedActivity {
    private List<SubjectData> subjects;
    private List<MarkData> marks;

    private Spinner overallAverageTermSpinner;
    private Spinner overallAverageModeSpinner;
    private TextView overallAverageTextView;
    private LineChart marksChart;
    private LinearLayout markLayout;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_title_statistics));


        subjects = NavigationHelper.getSelectedSubjects(getIntent());
        buildMarksArray();


        overallAverageTermSpinner = findViewById(R.id.overallAverageTermSpinner);
        overallAverageModeSpinner = findViewById(R.id.overallAverageModeSpinner);
        overallAverageTextView = findViewById(R.id.overallAverageTextView);
        marksChart = findViewById(R.id.marksChart);
        markLayout = findViewById(R.id.markLayout);

        if (marks.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a StatisticsActivity with 0 marks");
        } else if (subjects.size() == 1) {
            ((Group) findViewById(R.id.overallAverageGroup)).setVisibility(View.GONE);
            actionBar.setSubtitle(subjects.get(0).getName());
        } else {
            overallAverageTermSpinner.setSelection(DateUtils.getTerm(marks.get(0).getDate()), false);
        }

        setupListeners();

        fillMarksChart();
        formatMarksChart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    ////////
    // UI //
    ////////

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
                showMark((MarkData) e.getData());
            }

            @Override
            public void onNothingSelected() {
                hideMark();
            }
        });
    }

    private void buildMarksArray() {
        marks = new ArrayList<>();
        for (SubjectData subject : subjects) {
            assert subject.getMarks() != null;
            marks.addAll(subject.getMarks());
        }

        // sort by date, so that marks[0].getTerm() returns the current term
        Collections.sort(marks, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
    }

    private void formatMarksChart() {
        marksChart.getDescription().setEnabled(false);
        marksChart.getLegend().setEnabled(false);
        int labelColor = resolveColor(marksChart.getContext(), android.R.attr.textColorPrimary);

        XAxis xAxis = marksChart.getXAxis();
        xAxis.setLabelCount(4);
        xAxis.setTextColor(labelColor);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DateFormat.getDateFormat(getBaseContext()).format(new Date((long) value));
            }
        });

        int initialTerm = DateUtils.getTerm(marks.get(0).getDate());
        int index = 0;
        for (MarkData mark : marks) {
            if (DateUtils.getTerm(mark.getDate()) != initialTerm) {
                xAxis.setAxisMinimum(marks.get(index).getDate().getTime());
            }
            ++index;
        }
        xAxis.setAxisMaximum(marks.get(0).getDate().getTime());

        ValueFormatter markFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return MarkFormatting.valueRepresentation(value);
            }
        };

        YAxis axisLeft = marksChart.getAxisLeft();
        axisLeft.setGranularity(0.5f);
        axisLeft.setLabelCount(15);
        axisLeft.setTextColor(labelColor);
        axisLeft.setDrawGridLines(false);
        axisLeft.setValueFormatter(markFormatter);

        YAxis axisRight = marksChart.getAxisRight();
        axisRight.setGranularity(0.5f);
        axisRight.setLabelCount(15);
        axisRight.setTextColor(labelColor);
        axisRight.setDrawGridLines(false);
        axisRight.setValueFormatter(markFormatter);
    }

    private void fillMarksChart() {
        ArrayList<Entry> chartEntries = new ArrayList<>();
        for (MarkData mark : marks) {
            if (mark.getValue().isNumber()) {
                chartEntries.add(new Entry(mark.getDate().getTime(), mark.getValue().getNumber(), mark));
            }
        }
        Collections.sort(chartEntries, new EntryXComparator());

        LineDataSet chartDataSet = new LineDataSet(chartEntries, getResources().getString(R.string.label_marks_chart));
        chartDataSet.setColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleHoleColor(getResources().getColor(R.color.chartLine));
        chartDataSet.setCircleRadius(getResources().getDimension(R.dimen.radius_chart_circle));
        chartDataSet.setDrawValues(false);

        LineData chartData = new LineData(chartDataSet);
        marksChart.setData(chartData);
        marksChart.invalidate();
    }

    private void showMark(MarkData markData) {
        if (markLayout.getChildCount() == 0) {
            markLayout.addView(LayoutInflater.from(this).inflate(R.layout.item_mark_detail, markLayout, false));
        }

        // TODO
        new MarkDetailItemHolder(markLayout.getChildAt(0), null).updateItemData(markData);
    }

    private void hideMark() {
        markLayout.removeAllViews();
    }


    /////////////
    // AVERAGE //
    /////////////

    private float getOverallAverageOfRoundedAverages(int term) {
        float sum = 0;
        int numberOfSubjects = 0;

        for (SubjectData subject : subjects) {
            try {
                sum += Math.round(subject.getAverage(term));
                ++numberOfSubjects;
            } catch (ArithmeticException ignored) {}
        }

        if (numberOfSubjects == 0) {
            throw new ArithmeticException();
        }
        return sum / numberOfSubjects;
    }

    private float getOverallAverageOfAverages(int term) {
        float sum = 0;
        int numberOfSubjects = 0;

        for (SubjectData subject : subjects) {
            try {
                sum += subject.getAverage(term);
                ++numberOfSubjects;
            } catch (ArithmeticException ignored) {}
        }

        if (numberOfSubjects == 0) {
            throw new ArithmeticException();
        }
        return sum / numberOfSubjects;
    }

    private float getOverallAverageOfMarks(int term) {
        float sum = 0;
        int numberOfMarks = 0;

        for (MarkData mark : marks) {
            if (DateUtils.getTerm(mark.getDate()) == term && mark.getValue().isNumber()) {
                sum += mark.getValue().getNumber();
                ++numberOfMarks;
            }
        }

        if (numberOfMarks == 0) {
            throw new ArithmeticException();
        }
        return sum / numberOfMarks;
    }

    private void updateOverallAverage() {
        try {
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
            overallAverageTextView.setTextColor(MarkFormatting.colorOf(this, overallAverage));
        } catch (ArithmeticException e) {
            overallAverageTextView.setText("");
        }
    }
}
