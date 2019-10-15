package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.MarkFormatting;
import com.stypox.mastercom_workbook.view.SubjectActivity;

public class SubjectItemHolder implements ItemHolder<SubjectData> {
    private View view;
    private TextView nameView;
    private TextView teacherTextView;
    private TextView averageTextView;

    private Context context;


    public SubjectItemHolder(View view) {
        this.view = view;
        nameView = view.findViewById(R.id.name);
        teacherTextView = view.findViewById(R.id.teacher);
        averageTextView = view.findViewById(R.id.average);

        context = view.getContext();
    }

    @Override
    public void updateItemData(SubjectData data) {
        nameView.setText(data.getName());
        if (data.getMarks() == null) {
            teacherTextView.setText(data.getError().getMessage(context));
            averageTextView.setText("X");
            averageTextView.setTextColor(Color.BLACK);
            view.setOnClickListener(null);

        } else if (data.getMarks().isEmpty()) {
            teacherTextView.setText(context.getString(R.string.error_no_marks));
            averageTextView.setText("?");
            averageTextView.setTextColor(Color.BLACK);
            view.setOnClickListener(null);

        } else {
            teacherTextView.setText(data.getTeacher());

            float average = data.getAverage(data.getMarks().get(0).getTerm()); // current average
            averageTextView.setText(MarkFormatting.floatToString(average, 2));
            averageTextView.setTextColor(MarkFormatting.colorOf(context, average));

            view.setOnClickListener(v -> {
                Intent openSubjectActivity = new Intent(context, SubjectActivity.class);
                openSubjectActivity.putExtra(SubjectActivity.subjectDataIntentKey, data);
                context.startActivity(openSubjectActivity);
            });
        }
    }

    public static class Factory implements ItemHolderFactory<SubjectItemHolder> {
        @Override
        public SubjectItemHolder buildItemHolder(View view) {
            return new SubjectItemHolder(view);
        }
    }
}
