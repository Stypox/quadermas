package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;

public class DocumentItemHolder extends ItemHolder<DocumentData> {
    private View view;
    private TextView nameView;
    private TextView subjectAndOwnerView;

    private Context context;


    public DocumentItemHolder(View view) {
        super(view);

        this.view = view;
        nameView = view.findViewById(R.id.name);
        subjectAndOwnerView = view.findViewById(R.id.subjectAndOwner);

        context = view.getContext();
    }

    @Override
    public void updateItemData(DocumentData data) {
        nameView.setText(data.getName());
        subjectAndOwnerView.setText(context.getResources().getString(R.string.two_strings, data.getSubject(), data.getOwner()));
    }

    public static class Factory implements ItemHolderFactory<DocumentItemHolder> {
        @Override
        public DocumentItemHolder buildItemHolder(View view) {
            return new DocumentItemHolder(view);
        }
    }
}
