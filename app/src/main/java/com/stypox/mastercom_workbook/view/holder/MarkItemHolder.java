package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;

public class MarkItemHolder extends ItemHolder<MarkData> {

    private final TextView valueView;
    private final TextView typeView;
    private final TextView dateView;

    public MarkItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
        super(itemView, adapter);

        valueView = itemView.findViewById(R.id.value);
        typeView = itemView.findViewById(R.id.type);
        dateView = itemView.findViewById(R.id.date);
    }

    @Override
    public void updateItemData(MarkData data) {
        valueView.setText(MarkFormatting.valueRepresentation(data.getValue()));
        valueView.setTextColor(MarkFormatting.colorOf(context, data.getValue()));
        typeView.setText(MarkFormatting.typeRepresentation(context, data.getType()));
        dateView.setText(SHORT_DATE_FORMAT.format(data.getDate()));
    }
}
