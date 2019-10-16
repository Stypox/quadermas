package com.stypox.mastercom_workbook.view.holder;

import android.view.View;

public interface ItemHolderFactory<H extends ItemHolder> {
    H buildItemHolder(View view);
}
