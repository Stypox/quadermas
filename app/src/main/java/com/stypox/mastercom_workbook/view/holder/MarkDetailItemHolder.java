package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class MarkDetailItemHolder extends ItemHolder<MarkData> {
    private final TextView valueView;
    private final TextView typeView;
    private final TextView subjectView;
    private final TextView descriptionView;
    private final TextView teacherDateView;

    private final Context context;


    public MarkDetailItemHolder(View view) {
        super(view);
        context = view.getContext();

        valueView = view.findViewById(R.id.mark_value);
        typeView = view.findViewById(R.id.mark_type);
        subjectView = view.findViewById(R.id.mark_subject);
        descriptionView = view.findViewById(R.id.mark_description);
        teacherDateView = view.findViewById(R.id.mark_teacher_date);
    }

    @NonNull
    @Override
    public void updateItemData(MarkData data) {
        valueView.setText(data.getValueRepresentation());
        valueView.setTextColor(MarkFormatting.colorOf(context, data.getValue()));
        typeView.setText(data.getTypeRepresentation(context));
        subjectView.setText(data.getSubject());

        if (data.getDescription().isEmpty()) {
            descriptionView.setText("");
            descriptionView.setVisibility(View.GONE);
        }
        else {
            descriptionView.setText(data.getDescription());
            descriptionView.setVisibility(View.VISIBLE);
        }

        teacherDateView.setText(context.getResources().getString(R.string.two_strings, data.getTeacher(), data.getDateRepresentation()));
    }

    public static class Factory implements ItemHolderFactory<MarkDetailItemHolder> {
        @Override
        public MarkDetailItemHolder buildItemHolder(View view) {
            return new MarkDetailItemHolder(view);
        }
    }
}
