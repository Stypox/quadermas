package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;

public class MarkItem extends ConstraintLayout {
    private MarkData data;

    public MarkItem(Context context, MarkData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mark_item, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ((TextView)findViewById(R.id.mark_value)).setText(data.getValueRepresentation());
        ((TextView)findViewById(R.id.mark_date)).setText(data.getDateRepresentation());
        ((TextView)findViewById(R.id.mark_type)).setText(data.getTypeRepresentation(getContext()));
    }
}
