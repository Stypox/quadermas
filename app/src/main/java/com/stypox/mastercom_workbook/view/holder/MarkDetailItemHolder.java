package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.DateFormatting;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class MarkDetailItemHolder extends ItemHolder<MarkData> {
    private final TextView valueView;
    private final TextView typeView;
    private final TextView subjectView;
    private final TextView descriptionView;
    private final TextView teacherDateView;

    private final Context context;


    public MarkDetailItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        valueView = itemView.findViewById(R.id.mark_value);
        typeView = itemView.findViewById(R.id.mark_type);
        subjectView = itemView.findViewById(R.id.mark_subject);
        descriptionView = itemView.findViewById(R.id.mark_description);
        teacherDateView = itemView.findViewById(R.id.mark_teacher_date);
    }

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

        teacherDateView.setText(context.getResources().getString(R.string.two_strings,
                data.getTeacher(), DateFormatting.formatDate(data.getDate())));
    }

    public static class Factory implements ItemHolderFactory<MarkData> {
        @Override
        public MarkDetailItemHolder buildItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
            return new MarkDetailItemHolder(itemView, adapter);
        }
    }
}
