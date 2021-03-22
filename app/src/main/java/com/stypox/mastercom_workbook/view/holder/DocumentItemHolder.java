package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;

public class DocumentItemHolder extends ItemHolder<DocumentData> {

    private final Context context;

    private final TextView nameView;
    private final TextView dateSubjectOwnerView;

    @SuppressLint("ClickableViewAccessibility")
    public DocumentItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<DocumentData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        nameView = itemView.findViewById(R.id.name);
        dateSubjectOwnerView = itemView.findViewById(R.id.dateSubjectOwner);

        HorizontalScrollView nameScrollView = itemView.findViewById(R.id.nameScrollView);
        HorizontalScrollView dateSubjectOwnerScrollView = itemView.findViewById(R.id.dateSubjectOwnerScrollView);
        nameScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
        dateSubjectOwnerScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(DocumentData data) {
        nameView.setText(data.getName());
        dateSubjectOwnerView.setText(context.getResources().getString(R.string.three_strings,
                DateUtils.formatDate(data.getDate()), data.getSubject(), data.getOwner()));

        if (adapter == null) {
            itemView.setOnClickListener(null);
        } else {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
    }


    private static class Factory implements ItemHolderFactory<DocumentData> {
        @Override
        public DocumentItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<DocumentData> adapter) {
            return new DocumentItemHolder(view, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
