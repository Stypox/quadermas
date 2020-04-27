package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.TopicExtractor;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SubjectTopicItemHolder;
import com.stypox.mastercom_workbook.view.holder.TopicItemHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class TopicsActivity extends ThemedActivity {
    public static final String subjectsIntentKey = "subjects";

    private int numSubjectsExtracted;
    private List<SubjectData> subjects;
    private List<TopicData> topics;

    private CompositeDisposable disposables;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<TopicData> topicsArrayAdapter;



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

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_title_topics));


        subjects = (List<SubjectData>) getIntent().getSerializableExtra(subjectsIntentKey);
        assert subjects != null;
        if (subjects.size() == 1) {
            actionBar.setSubtitle(subjects.get(0).getName());
        }


        disposables = new CompositeDisposable();
        topics = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView topicsList = findViewById(R.id.topicsList);

        topicsList.setLayoutManager(new LinearLayoutManager(this));
        topicsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_topic, topics,
                subjects.size() == 1 ? new TopicItemHolder.Factory() : new SubjectTopicItemHolder.Factory());
        topicsList.setAdapter(topicsArrayAdapter);


        refreshLayout.setOnRefreshListener(() -> reloadTopics(true));
        reloadTopics(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadTopics(boolean reload) {
        refreshLayout.setRefreshing(true);

        disposables.clear();
        topics.clear();
        topicsArrayAdapter.notifyDataSetChanged();

        numSubjectsExtracted = 0;
        fetchTopics(reload);
    }

    private void fetchTopics(boolean reload) {
        for (SubjectData subject : subjects) {
            fetchTopicsForSubject(subject, reload);
        }
    }

    private void fetchTopicsForSubject(SubjectData subjectData, boolean reload) {
        if (reload || subjectData.getTopics() == null) {
            disposables.add(TopicExtractor.fetchTopics(subjectData,
                    () -> onTopicExtractionError(subjectData.getName()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTopicsFetched, this::onError));
        } else {
            onTopicsFetched(subjectData);
        }
    }

    private void onError(Throwable throwable) {
        if (!(throwable instanceof ExtractorError)) return;
        ExtractorError error = (ExtractorError) throwable;
        error.printStackTrace();

        Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), v -> reloadTopics(false))
                .show();
        increaseNumSubjectsExtracted();
    }

    private void onTopicExtractionError(String subjectName) {
        new Handler(getMainLooper()).post(() ->
                Toast.makeText(this, getString(R.string.error_could_not_load_a_topic, subjectName), Toast.LENGTH_LONG)
                        .show()
        );
    }

    private void onTopicsFetched(SubjectData subjectData) {
        assert subjectData.getTopics() != null;
        this.topics.addAll(subjectData.getTopics());
        topicsArrayAdapter.notifyDataSetChanged();
        increaseNumSubjectsExtracted();
    }

    private void increaseNumSubjectsExtracted() {
        numSubjectsExtracted++;
        if (numSubjectsExtracted == subjects.size()) {
            refreshLayout.setRefreshing(false);
        }
    }
}
