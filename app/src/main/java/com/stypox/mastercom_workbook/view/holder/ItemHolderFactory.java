package com.stypox.mastercom_workbook.view.holder;

import android.view.View;

public interface ItemHolderFactory<H> {
    H buildItemHolder(View view);
}
