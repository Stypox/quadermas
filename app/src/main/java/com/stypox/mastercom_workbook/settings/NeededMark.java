package com.stypox.mastercom_workbook.settings;

import android.content.Context;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.stypox.mastercom_workbook.data.SubjectData;

public class NeededMark {
    public static final String AIM_MARK_KEY = "aim_mark_";
    public static final String REMAINING_TESTS_KEY = "remaining_tests_";

    public static String aimMarkForSubject(final Context context, final SubjectData subject) {
        final String aimMark = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(AIM_MARK_KEY + subject.getId(), null);

        if (TextUtils.isEmpty(aimMark)) {
            try {
                final SecondTermStart secondTermStart = SecondTermStart.fromPreferences(context);
                return String.valueOf(Math.max(6, (int) Math.ceil(
                        subject.getAverage(secondTermStart, secondTermStart.currentTerm()))));
            } catch (final ArithmeticException e) {
                return "6"; // default to 6
            }
        } else {
            return aimMark;
        }
    }

    public static String remainingTestsForSubject(final Context context,
                                                  final SubjectData subject) {
        final String remainingTests = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(REMAINING_TESTS_KEY + subject.getId(), null);

        if (TextUtils.isEmpty(remainingTests)) {
            return "1"; // default to one test
        } else {
            return remainingTests;
        }
    }

    public static void saveAimMarkForSubject(final Context context,
                                             final SubjectData subject,
                                             final String aimMark) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(AIM_MARK_KEY + subject.getId(), aimMark).apply();
    }

    public static void saveRemainingTestsForSubject(final Context context,
                                                    final SubjectData subject,
                                                    final String remainingTests) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(REMAINING_TESTS_KEY + subject.getId(), remainingTests).apply();
    }
}
