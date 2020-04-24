package com.stypox.mastercom_workbook.view.holder;

import androidx.annotation.NonNull;
import android.view.View;

public interface ItemHolderFactory<D> {
    ItemHolder<D> buildItemHolder(@NonNull View view, @NonNull ItemArrayAdapter<D> adapter);
}
