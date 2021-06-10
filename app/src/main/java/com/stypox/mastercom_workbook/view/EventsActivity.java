package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.EventItemHolder;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class EventsActivity extends ThemedActivity {

    private CompositeDisposable disposables;
    private SwipeRefreshLayout refreshLayout;

    private List<EventData> events;
    private ItemArrayAdapter<EventData> eventsArrayAdapter;

    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        setSupportActionBar(findViewById(R.id.toolbar));

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_events));

        disposables = new CompositeDisposable();
        events = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        final RecyclerView marksView = findViewById(R.id.eventsList);

        marksView.setLayoutManager(new LinearLayoutManager(this));
        eventsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_event, events,
                EventItemHolder.getFactory());
        marksView.setAdapter(eventsArrayAdapter);

        refreshLayout.setOnRefreshListener(() -> reloadEvents(true));
        reloadEvents(false);
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
        events.clear();
        events.addAll(data);
        eventsArrayAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
