package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.Intent;

import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.Extractor;

import java.util.Collections;
import java.util.List;

public class NavigationHelper {
    public static final String SELECTED_SUBJECT_KEY = "selected_subject_key";
    public static int ALL_SUBJECTS = -1;

    public static void openActivity(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    public static void openActivityWithSubject(Context context, Class<?> activity, SubjectData subject) {
        Intent intent = new Intent(context, activity);
        intent.putExtra(SELECTED_SUBJECT_KEY, Extractor.getExtractedSubjects().indexOf(subject));
        context.startActivity(intent);
    }

    public static void openActivityWithAllSubjects(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.putExtra(SELECTED_SUBJECT_KEY, ALL_SUBJECTS);
        context.startActivity(intent);
    }

    public static List<SubjectData> getSelectedSubjects(Intent intent) {
        int selectedSubject = intent.getIntExtra(SELECTED_SUBJECT_KEY, ALL_SUBJECTS);
        if (selectedSubject == ALL_SUBJECTS) {
            return Extractor.getExtractedSubjects();
        } else {
            return Collections.singletonList(Extractor.getExtractedSubjects().get(selectedSubject));
        }
    }
}
