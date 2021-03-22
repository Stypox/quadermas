package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.MarkFormatting;

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
        dateView.setText(DateUtils.formatDate(data.getDate()));
    }


    private static class Factory implements ItemHolderFactory<MarkData> {
        @Override
        public MarkItemHolder buildItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<MarkData> adapter) {
            return new MarkItemHolder(itemView, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
