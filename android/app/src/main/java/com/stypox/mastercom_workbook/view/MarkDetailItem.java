package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import java.util.Date;

public class MarkDetailItem extends ConstraintLayout {
    private MarkData data;

    public MarkDetailItem(Context context, MarkData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mark_detail_item, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView mark_value = findViewById(R.id.mark_value);
        mark_value.setText(data.getValueRepresentation());
        mark_value.setTextColor(MarkFormatting.colorOf(getContext(), data.getValue()));

        ((TextView)findViewById(R.id.mark_type)).setText(data.getTypeRepresentation(getContext()));
        ((TextView)findViewById(R.id.mark_subject)).setText(data.getSubject());

        TextView mark_description = findViewById(R.id.mark_description);
        if (data.getDescription().isEmpty()) {
            mark_description.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        }
        else {
            mark_description.setText(data.getDescription());
        }

        ((TextView)findViewById(R.id.mark_teacher_date)).setText(String.format("%s  -  %s", data.getTeacher(), data.getDateRepresentation()));
    }

    public Date getDate() {
        return data.getDate();
    }
    public float getValue() {
        return data.getValue();
    }
}
