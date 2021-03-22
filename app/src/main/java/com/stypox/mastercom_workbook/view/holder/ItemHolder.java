package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ItemHolder<D> extends RecyclerView.ViewHolder {
    @Nullable protected final ItemArrayAdapter<D> adapter;
    @NonNull protected final Context context;

    public ItemHolder(@NonNull final View itemView, @Nullable final ItemArrayAdapter<D> adapter) {
        super(itemView);
        this.adapter = adapter;
        this.context = itemView.getContext();
    }

    public abstract void updateItemData(D data);
}
