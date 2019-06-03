package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.MarkData;

import java.util.ArrayList;

public interface FetchMarksCallback extends BaseCallback {
    void onFetchMarksCompleted(ArrayList<MarkData> marks);
}
