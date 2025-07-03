package com.stypox.mastercom_workbook.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.Group;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.EventItemHolder;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;
import static com.stypox.mastercom_workbook.util.DateUtils.TODAY;

public class EventsActivity extends ThemedActivity {

    private CompositeDisposable disposables;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView eventsView;

    private List<EventData> events;
    private ItemArrayAdapter<EventData> eventsArrayAdapter;

    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        fixInsets();
        setSupportActionBar(findViewById(R.id.toolbar));

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_events));
        actionBar.setSubtitle(getString(R.string.today_is, SHORT_DATE_FORMAT.format(TODAY)));

        disposables = new CompositeDisposable();
        events = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        eventsView = findViewById(R.id.eventsList);
        setupLegend();

        eventsView.setLayoutManager(new LinearLayoutManager(this));
        eventsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_event, events,
                EventItemHolder::new);
        eventsView.setAdapter(eventsArrayAdapter);

        refreshLayout.setOnRefreshListener(() -> reloadEvents(true));
        reloadEvents(false);
    }

    private void setupLegend() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String showEventsLegendKey = getString(R.string.key_show_events_legend);
        if (prefs.getBoolean(showEventsLegendKey, true)) {
            final Group legendGroup = findViewById(R.id.legendGroup);
            final View dismissLegendButton = findViewById(R.id.dismissLegendButton);

            legendGroup.setVisibility(View.VISIBLE);
            dismissLegendButton.setOnClickListener(v -> {
                legendGroup.setVisibility(View.GONE);
                prefs.edit().putBoolean(showEventsLegendKey, false).apply();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadEvents(final boolean reload) {
        refreshLayout.setRefreshing(true);

        disposables.clear();
        events.clear();
        eventsArrayAdapter.notifyDataSetChanged();

        fetchEvents(reload);
    }

    private void fetchEvents(final boolean reload) {
        Extractor.extractEvents(reload, disposables, new Extractor.DataHandler<List<EventData>>() {
            @Override
            public void onExtractedData(final List<EventData> data) {
                onEventsFetched(data);
            }

            @Override
            public void onItemError(final ExtractorError error) {
                Toast.makeText(EventsActivity.this,
                        getString(R.string.error_could_not_load_an_event), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(final ExtractorError error) {
                Snackbar.make(findViewById(android.R.id.content),
                        error.getMessage(EventsActivity.this), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), v -> reloadEvents(false))
                        .show();
            }
        });
    }

    private void onEventsFetched(final List<EventData> data) {
        refreshLayout.setRefreshing(false);

        events.clear();
        events.addAll(data);

        eventsArrayAdapter.notifyDataSetChanged();
        scrollToFirstEventInTheFuture();
    }

    private void scrollToFirstEventInTheFuture() {
        int i = 0;
        for(; i < events.size(); ++i) {
            if (!DateUtils.inTheFuture(events.get(i).getEnd())) {
                break;
            }
        }

        if (i > 0) {
            // nested post() calls to process things in the correct order
            final int scrollPosition = i - 1;
            eventsView.post(() -> {
                // first move the item on screen
                eventsView.scrollToPosition(scrollPosition);

                eventsView.post(() -> {
                    // then scroll to move the item in the middle
                    int scrollBy = 0; // default to not scrolling if view not found
                    for (int c = 0; c < eventsView.getChildCount(); ++c) {
                        final View child = eventsView.getChildAt(c);
                        if (child == null) {
                            break; // should be unreachable, but let's be sure
                        }

                        if (eventsView.getChildAdapterPosition(child) == scrollPosition) {
                            scrollBy = (int)((child.getY() - eventsView.getY() + child.getHeight())
                                    - eventsView.getHeight() / 2);
                            break;
                        }
                    }

                    eventsView.scrollBy(0, scrollBy);
                });
            });
        }
    }
}
