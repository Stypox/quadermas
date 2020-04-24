package com.stypox.mastercom_workbook.view.holder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class ItemHolder<D> extends RecyclerView.ViewHolder {
    @Nullable protected ItemArrayAdapter<D> adapter;

    public ItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<D> adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public abstract void updateItemData(D data);
}
