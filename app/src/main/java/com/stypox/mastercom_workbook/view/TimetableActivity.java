package com.stypox.mastercom_workbook.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TimetableEventData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.TimetableEventItemHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.stypox.mastercom_workbook.util.DateUtils.FULL_DATE_FORMAT;
import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;
import static com.stypox.mastercom_workbook.util.DateUtils.TODAY;

public class TimetableActivity extends ThemedActivity {

    private TextView dateTextView;
    private TextView emptyTextView;
    private ProgressBar loadingIndicator;
    private TextView errorTextView;
    private View selectDayButton;
    private View previousDayButton;
    private View nextDayButton;

    private ItemArrayAdapter<TimetableEventData> eventArrayAdapter;
    @NonNull private final List<TimetableEventData> events = new ArrayList<>();

    @NonNull private final CompositeDisposable disposables = new CompositeDisposable();
    private Date currentDay;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        fixInsets();
        setSupportActionBar(findViewById(R.id.toolbar));

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_timetable));
        actionBar.setSubtitle(getString(R.string.today_is, SHORT_DATE_FORMAT.format(TODAY)));

        dateTextView = findViewById(R.id.dateTextView);
        emptyTextView = findViewById(R.id.timetableEmptyTextView);
        loadingIndicator = findViewById(R.id.timetableLoadingIndicator);
        errorTextView = findViewById(R.id.timetableErrorTextView);
        selectDayButton = findViewById(R.id.selectDayButton);
        previousDayButton = findViewById(R.id.previousDayButton);
        nextDayButton = findViewById(R.id.nextDayButton);

        RecyclerView timetableEventList = findViewById(R.id.timetableEventList);
        timetableEventList.setLayoutManager(new LinearLayoutManager(this));
        eventArrayAdapter = new ItemArrayAdapter<>(R.layout.item_timetable_event,
                events, TimetableEventItemHolder::new);
        timetableEventList.setAdapter(eventArrayAdapter);

        currentDay = new Date(); // start with today
        setupListeners();
        loadDay(currentDay);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    ///////////
    // SETUP //
    ///////////

    private void setupListeners() {
        selectDayButton.setOnClickListener(v -> openDatePickerDialog());
        previousDayButton.setOnClickListener(
                v -> loadDay(DateUtils.addDaysToDate(currentDay, -1)));
        nextDayButton.setOnClickListener(
                v -> loadDay(DateUtils.addDaysToDate(currentDay, 1)));
    }

    private void openDatePickerDialog() {
        new DatePickerDialog(this,
                (v, year, month, dayOfMonth)
                        -> loadDay(DateUtils.buildDate(year, month, dayOfMonth)),
                DateUtils.getCalendarField(currentDay, Calendar.YEAR),
                DateUtils.getCalendarField(currentDay, Calendar.MONTH),
                DateUtils.getCalendarField(currentDay, Calendar.DAY_OF_MONTH)).show();
    }


    /////////////
    // LOADING //
    /////////////

    void loadDay(final Date date) {
        currentDay = date;
        disposables.clear(); // first clear any current operation

        dateTextView.setText(FULL_DATE_FORMAT.format(date));
        emptyTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);

        events.clear();
        eventArrayAdapter.notifyDataSetChanged();

        Extractor.extractTimetable(date, disposables,
                new Extractor.DataHandler<List<TimetableEventData>>() {
                    @Override
                    public void onExtractedData(final List<TimetableEventData> data) {
                        events.clear(); // clear again just in case
                        events.addAll(data);
                        eventArrayAdapter.notifyDataSetChanged();

                        emptyTextView.setVisibility(
                                data.isEmpty() ? View.VISIBLE : View.GONE);
                        loadingIndicator.setVisibility(View.GONE);
                    }

                    @Override
                    public void onItemError(final ExtractorError error) {
                        errorTextView.setText(R.string.error_could_not_load_timetable_events);
                        errorTextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(final ExtractorError error) {
                        events.clear(); // clear again just in case
                        eventArrayAdapter.notifyDataSetChanged();
                        emptyTextView.setVisibility(View.GONE);
                        loadingIndicator.setVisibility(View.GONE);

                        errorTextView.setText(R.string.error_could_not_load_timetable);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
