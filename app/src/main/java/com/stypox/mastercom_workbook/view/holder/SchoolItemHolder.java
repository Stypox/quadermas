package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SchoolData;

public class SchoolItemHolder extends ItemHolder<SchoolData> {

    private final TextView schoolDescriptionView;
    private final TextView APIUrlView;

    public SchoolItemHolder(@NonNull final View itemView,
                            @Nullable final ItemArrayAdapter<SchoolData> adapter) {
        super(itemView, adapter);

        schoolDescriptionView = itemView.findViewById(R.id.schoolDescription);
        APIUrlView = itemView.findViewById(R.id.APIUrl);
    }

    @Override
    public void updateItemData(final SchoolData data) {
        if (data.getMunicipality().equals(data.getProvince())) {
            schoolDescriptionView.setText(context.getString(
                    R.string.label_school_municipality, data.getName(), data.getMunicipality()));
        } else {
            schoolDescriptionView.setText(context.getString(
                    R.string.label_school_municipality_province, data.getName(),
                    data.getMunicipality(), data.getProvince()));
        }

        APIUrlView.setText(data.getAPIUrl());

        if (adapter != null) {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
    }
}
