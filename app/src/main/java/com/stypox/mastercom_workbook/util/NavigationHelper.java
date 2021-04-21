package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.Intent;

import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.Extractor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

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

    /**
     * Return whether the intent says to show all subjects
     */
    public static boolean isSelectedSubjectsAll(final Intent intent) {
        return intent.getIntExtra(SELECTED_SUBJECT_KEY, ALL_SUBJECTS) == ALL_SUBJECTS;
    }

    /**
     * Get the selected subjects based on the intent assuming that subjects have already been
     * extracted
     */
    public static List<SubjectData> getSelectedSubjects(final Intent intent) {
        int selectedSubject = intent.getIntExtra(SELECTED_SUBJECT_KEY, ALL_SUBJECTS);
        if (selectedSubject == ALL_SUBJECTS) {
            return Extractor.getExtractedSubjects();
        } else {
            return Collections.singletonList(Extractor.getExtractedSubjects().get(selectedSubject));
        }
    }

    /**
     * Get the selected subjects based on the intent and provide results to the handler after
     * subjects have been extracted
     */
    public static void getSelectedSubjects(final Intent intent,
                                           final CompositeDisposable disposables,
                                           final Extractor.DataHandler<List<SubjectData>> handler) {
        final int selectedSubject = intent.getIntExtra(SELECTED_SUBJECT_KEY, ALL_SUBJECTS);
        if (selectedSubject == ALL_SUBJECTS) {
            Extractor.extractSubjects(false, disposables, handler);
        } else {
            // surely subjects have already been extracted, if we know the id of one of them
            handler.onExtractedData(Collections.singletonList(
                    Extractor.getExtractedSubjects().get(selectedSubject)));
        }
    }
}
