package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class SubjectItem extends ConstraintLayout implements View.OnClickListener {
    private SubjectData data;

    public SubjectItem(Context context, SubjectData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_subject, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView nameView = findViewById(R.id.name);
        TextView teacherTextView = findViewById(R.id.teacher);
        TextView averageTextView = findViewById(R.id.average);

        nameView.setText(data.getName());
        if (data.getMarks() == null) {
            teacherTextView.setText(data.getError().getMessage(getContext()));
            averageTextView.setText("X");

        } else if (data.getMarks().isEmpty()) {
            teacherTextView.setText(getContext().getString(R.string.error_no_marks));
            averageTextView.setText("?");

        } else {
            teacherTextView.setText(data.getTeacher());
            setOnClickListener(this);

            float average = data.getAverage(data.getMarks().get(0).getTerm()); // current average
            averageTextView.setText(MarkFormatting.floatToString(average, 2));
            averageTextView.setTextColor(MarkFormatting.colorOf(getContext(), average));
        }
    }

    @Override
    public void onClick(View v) {
        Intent openSubjectActivity = new Intent(getContext(), SubjectActivity.class);
        openSubjectActivity.putExtra(SubjectActivity.subjectDataIntentKey, data);
        getContext().startActivity(openSubjectActivity);
    }
}
