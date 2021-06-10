package com.stypox.mastercom_workbook.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.stypox.mastercom_workbook.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.stypox.mastercom_workbook.util.DateUtils.getCalendarField;

public class SecondTermStart {
    public static final int FIRST_TERM = 0;
    public static final int SECOND_TERM = 1;

    private static final String MONTH_KEY = "second_term_start_month";
    private static final String DAY_KEY = "second_term_start_day";

    private static final Map<String, SecondTermStart> SECOND_TERM_START_FOR_API_URL_MAP
            = new HashMap<String, SecondTermStart>() {{
        put("davinci-tn", new SecondTermStart(1, 14));
        put("galilei-tn", new SecondTermStart(1, 1));
        put("marconi-tn", new SecondTermStart(2, 1));
        put("rosmini-tn", new SecondTermStart(1, 1));
    }};

    private static final int END_OF_SECOND_TERM_MONTH = 8; // August, exclusive


    private final int month; // starting from 1, which means January
    private final int day; // starting from 1


    private SecondTermStart(final int month, final int day) {
        this.month = month;
        this.day = day;
    }
    private SecondTermStart() {
        this(1, 1); // January the first
    }


    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getTerm(final Date date) {
        final int dateMonth = date.getMonth() + 1;
        final int dateDay = date.getDate();
        final boolean isSecondTerm;

        if (month >= END_OF_SECOND_TERM_MONTH) {
            // the second term starts before the end of the year (e.g. December)
            isSecondTerm = (dateMonth > month || (dateMonth == month && dateDay >= day))
                    || (dateMonth < END_OF_SECOND_TERM_MONTH);
        } else {
            // the second year starts after the beginning of the new year (e.g. January)
            isSecondTerm = (dateMonth > month || (dateMonth == month && dateDay >= day))
                    && (dateMonth < END_OF_SECOND_TERM_MONTH);
        }

        return isSecondTerm ? SECOND_TERM : FIRST_TERM;
    }

    public int currentTerm() {
        return getTerm(new Date());
    }

    public void saveToPreferences(final Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(MONTH_KEY, month)
                .putInt(DAY_KEY, day)
                .apply();
    }


    /**
     * Calculates the first year of the school year the date is in. For example, if the date is in
     * the school year 2018/2019, the returned value is 2018.
     *
     * @param date the date of which to extract the school year
     * @return the school year as int
     */
    public static int schoolYear(final Date date) {
        final int year = getCalendarField(date, Calendar.YEAR);
        if (date.getMonth() + 1 < END_OF_SECOND_TERM_MONTH) {
            return year - 1;
        } else {
            return year;
        }
    }

    /**
     * Deduces the calendar year a month belongs to, given that it happened in the current school
     * year. For example, if the month is March the returned value is the current year, while if it
     * is September the returned value is the current year {@code - 1}.
     *
     * @param month the month, 1-based (1 is January)
     * @return the current year as int
     */
    public static int yearFromMonth(final int month) {
        return getCalendarField(new Date(), Calendar.YEAR)
                - (month < END_OF_SECOND_TERM_MONTH ? 0 : 1);
    }


    public static SecondTermStart fromPreferences(final Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final int month = sp.getInt(MONTH_KEY, -1);
        final int day = sp.getInt(DAY_KEY, -1);

        if (month == -1 || day == -1) {
            return new SecondTermStart();
        } else {
            return new SecondTermStart(month, day);
        }
    }

    @Nullable
    public static SecondTermStart fromAPIUrl(final String APIUrl) {
        return SECOND_TERM_START_FOR_API_URL_MAP.get(APIUrl);
    }

    public static void openPickerDialog(final Context context, final Runnable onSettingChanged) {
        final View dialogContent = View.inflate(context, R.layout.dialog_second_term_start, null);
        final NumberPicker monthPicker = dialogContent.findViewById(R.id.monthPicker);
        final NumberPicker dayPicker = dialogContent.findViewById(R.id.dayPicker);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        final SecondTermStart valueBefore = fromPreferences(context);
        monthPicker.setValue(valueBefore.month);
        dayPicker.setValue(valueBefore.day);

        new AlertDialog.Builder(context)
                .setView(dialogContent)
                .setTitle(R.string.settings_second_term_start)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    new SecondTermStart(monthPicker.getValue(), dayPicker.getValue())
                            .saveToPreferences(context);
                    onSettingChanged.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
}
