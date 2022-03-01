package com.stypox.mastercom_workbook.view.holder;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Using *ItemHolder::new will usually work
 */
public interface ItemHolderFactory<D> {
    ItemHolder<D> buildItemHolder(@NonNull View view, @NonNull ItemArrayAdapter<D> adapter);
}
