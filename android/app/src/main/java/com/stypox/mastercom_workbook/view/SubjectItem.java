package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import java.util.ArrayList;

public class SubjectItem extends ConstraintLayout implements View.OnClickListener {
    private SubjectData data;

    private TextView teacherTextView;
    private TextView averageTextView;

    public SubjectItem(Context context, SubjectData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.subject_item, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView nameView = findViewById(R.id.name);
        teacherTextView = findViewById(R.id.teacher);
        averageTextView = findViewById(R.id.average);

        nameView.setText(data.getName());
    }

    public void onMarksLoaded(ArrayList<MarkData> marks) {
        if (marks.isEmpty()) {
            this.teacherTextView.setText(getContext().getString(R.string.error_no_marks));
        } else {
            this.teacherTextView.setText(data.getTeacher());
            setOnClickListener(this);
        }
        showAverage();
    }
    public void onMarksLoadingError(Extractor.Error error) {
        this.teacherTextView.setText(error.toString(getContext()));
        showAverage();
    }

    public void showAverage() {
        try {
            float average = data.getAverage(data.getMarks().get(0).getTerm()); // current average
            averageTextView.setText(MarkFormatting.floatToString(average, 3));

            if (average < 6) {
                averageTextView.setTextColor(getResources().getColor(R.color.failingMark));
            } else if (average < 8) {
                averageTextView.setTextColor(getResources().getColor(R.color.halfwayMark));
            } else {
                averageTextView.setTextColor(getResources().getColor(R.color.excellentMark));
            }
        } catch (Throwable e) {
            averageTextView.setText("?");
            averageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        }
    }

    @Override
    public void onClick(View v) {
        Intent openSubjectActivity = new Intent(getContext(), SubjectActivity.class);
        openSubjectActivity.putExtra(SubjectActivity.subjectDataIntentKey, data);
        getContext().startActivity(openSubjectActivity);
    }
}
