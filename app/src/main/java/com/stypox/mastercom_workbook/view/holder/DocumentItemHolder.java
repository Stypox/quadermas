package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;

import org.jetbrains.annotations.NotNull;

public class DocumentItemHolder extends ItemHolder<DocumentData> {
    private TextView nameView;
    private TextView subjectAndOwnerView;

    private Context context;


    public DocumentItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<DocumentData> adapter) {
        super(itemView, adapter);

        nameView = itemView.findViewById(R.id.name);
        subjectAndOwnerView = itemView.findViewById(R.id.subjectAndOwner);

        context = itemView.getContext();
    }

    @Override
    public void updateItemData(DocumentData data) {
        nameView.setText(data.getName());
        subjectAndOwnerView.setText(context.getResources().getString(R.string.two_strings, data.getSubject(), data.getOwner()));

        if (adapter != null) {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
    }

    public static class Factory implements ItemHolderFactory<DocumentData> {
        @Override
        public DocumentItemHolder buildItemHolder(@NotNull View view, @Nullable ItemArrayAdapter<DocumentData> adapter) {
            return new DocumentItemHolder(view, adapter);
        }
    }
}
