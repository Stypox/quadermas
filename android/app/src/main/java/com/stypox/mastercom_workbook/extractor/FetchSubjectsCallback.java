package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.SubjectData;

import java.util.ArrayList;

public interface FetchSubjectsCallback extends BaseCallback {
    void onFetchSubjectsCompleted(ArrayList<SubjectData> subjects);
}
