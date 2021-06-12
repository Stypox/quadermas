package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;

public class MarkDetailItemHolder extends ItemHolder<MarkData> {

    private final TextView valueView;
    private final TextView typeView;
    private final TextView subjectView;
    private final TextView descriptionView;
    private final TextView teacherDateView;

    public MarkDetailItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
        super(itemView, adapter);

        valueView = itemView.findViewById(R.id.value);
        typeView = itemView.findViewById(R.id.type);
        subjectView = itemView.findViewById(R.id.subject);
        descriptionView = itemView.findViewById(R.id.description);
        teacherDateView = itemView.findViewById(R.id.subtitle);
    }

    @Override
    public void updateItemData(MarkData data) {
        valueView.setText(MarkFormatting.valueRepresentation(data.getValue()));
        valueView.setTextColor(MarkFormatting.colorOf(context, data.getValue()));
        typeView.setText(MarkFormatting.typeRepresentation(context, data.getType()));
        subjectView.setText(data.getSubject());

        if (data.getDescription().isEmpty()) {
            descriptionView.setText("");
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setText(data.getDescription());
            descriptionView.setVisibility(View.VISIBLE);
        }

        teacherDateView.setText(context.getResources().getString(R.string.two_strings,
                data.getTeacher(), SHORT_DATE_FORMAT.format(data.getDate())));
    }


    private static class Factory implements ItemHolderFactory<MarkData> {
        @Override
        public MarkDetailItemHolder buildItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
            return new MarkDetailItemHolder(itemView, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
