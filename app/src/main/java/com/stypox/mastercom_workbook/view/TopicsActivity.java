package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.TopicsExtractor;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.TopicItemHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class TopicsActivity extends AppCompatActivity {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData subjectData;
    private List<TopicData> topics;

    private CompositeDisposable disposables;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<TopicData> topicsArrayAdapter;



    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_title_topics));


        subjectData = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        actionBar.setSubtitle(subjectData.getName());


        disposables = new CompositeDisposable();
        topics = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView topicsList = findViewById(R.id.topicsList);

        topicsList.setLayoutManager(new LinearLayoutManager(this));
        topicsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_topic, topics, new TopicItemHolder.Factory());
        topicsList.setAdapter(topicsArrayAdapter);


        refreshLayout.setOnRefreshListener(this::reloadTopics);
        reloadTopics();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadTopics() {
        refreshLayout.setRefreshing(true);

        disposables.clear();
        topics.clear();
        topicsArrayAdapter.notifyDataSetChanged();
        fetchTopics();
    }

    private void fetchTopics() {
        disposables.add(TopicsExtractor.fetchTopics(subjectData.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTopicsFetched, this::onError));
    }

    private void onError(Throwable throwable) {
        if (!(throwable instanceof ExtractorError)) return;
        ExtractorError error = (ExtractorError) throwable;
        error.printStackTrace();

        Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), v -> reloadTopics())
                .show();
        refreshLayout.setRefreshing(false);
    }

    private void onTopicsFetched(List<TopicData> topicData) {
        this.topics.addAll(topicData);
        topicsArrayAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
