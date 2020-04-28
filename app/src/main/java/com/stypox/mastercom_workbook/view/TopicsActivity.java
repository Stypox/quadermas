package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.util.NavigationHelper;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SubjectTopicItemHolder;
import com.stypox.mastercom_workbook.view.holder.TopicItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class TopicsActivity extends ThemedActivity
        implements Toolbar.OnMenuItemClickListener {

    private CompositeDisposable disposables;

    private int numSubjectsExtracted;
    private List<SubjectData> subjects;
    private List<TopicData> filteredTopics;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<TopicData> topicsArrayAdapter;

    private MenuItem showOnlyAssignmentsMenuItem;
    private boolean showOnlyAssignments = false;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_topics));

        subjects = NavigationHelper.getSelectedSubjects(getIntent());
        if (subjects.size() == 1) {
            actionBar.setSubtitle(subjects.get(0).getName());
        }


        disposables = new CompositeDisposable();
        filteredTopics = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView topicsList = findViewById(R.id.topicsList);

        topicsList.setLayoutManager(new LinearLayoutManager(this));
        topicsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_topic, filteredTopics,
                subjects.size() == 1 ? TopicItemHolder.getFactory() : SubjectTopicItemHolder.getFactory());
        topicsList.setAdapter(topicsArrayAdapter);


        refreshLayout.setOnRefreshListener(() -> reloadTopics(true));
        reloadTopics(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topics, menu);
        showOnlyAssignmentsMenuItem = menu.findItem(R.id.showOnlyAssignmentsAction);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.showOnlyAssignmentsAction:
                switchShowOnlyAssignments();
                return true;
            default:
                return false;
        }
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadTopics(boolean reload) {
        refreshLayout.setRefreshing(true);

        disposables.clear();
        filteredTopics.clear();
        topicsArrayAdapter.notifyDataSetChanged();

        numSubjectsExtracted = 0;
        fetchTopics(reload);
    }

    private void increaseNumSubjectsExtracted() {
        numSubjectsExtracted++;
        if (numSubjectsExtracted == subjects.size()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void fetchTopics(boolean reload) {
        for (SubjectData subject : subjects) {
            Extractor.extractTopics(subject, reload, disposables, new Extractor.DataHandler<SubjectData>() {
                @Override
                public void onExtractedData(SubjectData data) {
                    increaseNumSubjectsExtracted();
                    addFilteredTopics(data);
                    sortFilteredTopicsByDateAndShow();
                }

                @Override
                public void onItemError(ExtractorError error) {
                    Toast.makeText(TopicsActivity.this,
                            getString(R.string.error_could_not_load_a_topic, subject.getName()), Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(ExtractorError error) {
                    increaseNumSubjectsExtracted();
                    Snackbar.make(findViewById(android.R.id.content),
                            error.getMessage(TopicsActivity.this), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.retry), v -> reloadTopics(false))
                            .show();
                }
            });
        }
    }


    ///////////////
    // FILTERING //
    ///////////////

    private void addFilteredTopics(SubjectData subject) {
        assert subject.getTopics() != null;
        if (showOnlyAssignments) {
            for (TopicData topic : subject.getTopics()) {
                if (!topic.getAssignment().isEmpty()) {
                    filteredTopics.add(topic);
                }
            }
        } else {
            filteredTopics.addAll(subject.getTopics());
        }
    }

    private void sortFilteredTopicsByDateAndShow() {
        Collections.sort(filteredTopics, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        topicsArrayAdapter.notifyDataSetChanged();
    }

    private void onFilterChanged() {
        filteredTopics.clear();
        for (SubjectData subject : subjects) {
            if (subject.getTopics() == null) {
                continue;
            }

            addFilteredTopics(subject);
        }

        sortFilteredTopicsByDateAndShow();
    }

    private void switchShowOnlyAssignments() {
        showOnlyAssignments = !showOnlyAssignments;

        showOnlyAssignmentsMenuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(),
                showOnlyAssignments ? R.drawable.ic_clear_white_24dp : R.drawable.ic_home_white_24dp));
        showOnlyAssignmentsMenuItem.setTitle(
                showOnlyAssignments ? R.string.action_clear_filter : R.string.action_show_only_assignments);

        onFilterChanged();
    }
}
