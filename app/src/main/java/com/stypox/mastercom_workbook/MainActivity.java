package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.ExtractorError.Type;
import com.stypox.mastercom_workbook.login.LoginActivity;
import com.stypox.mastercom_workbook.login.LoginData;
import com.stypox.mastercom_workbook.settings.SettingsActivity;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.DocumentsActivity;
import com.stypox.mastercom_workbook.view.MarksActivity;
import com.stypox.mastercom_workbook.view.StatisticsActivity;
import com.stypox.mastercom_workbook.view.SubjectActivity;
import com.stypox.mastercom_workbook.view.TopicsActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SubjectItemHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivity;
import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivityWithAllSubjects;
import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivityWithSubject;

public class MainActivity extends ThemedActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemArrayAdapter.OnItemClickListener<SubjectData> {

    private static final int requestCodeLoginActivity = 0;

    private CompositeDisposable disposables;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<SubjectData> subjectsArrayAdapter;

    private DrawerLayout drawer;
    private MenuItem marksMenuItem;
    private MenuItem topicsMenuItem;
    private MenuItem statisticsMenuItem;
    private TextView fullNameView;
    private TextView fullAPIUrlView;

    private int numSubjectsExtracted;
    private List<SubjectData> subjects;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposables = new CompositeDisposable();

        refreshLayout = findViewById(R.id.refreshLayout);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        marksMenuItem = navigationView.getMenu().findItem(R.id.marksAction);
        statisticsMenuItem = navigationView.getMenu().findItem(R.id.statisticsAction);
        topicsMenuItem = navigationView.getMenu().findItem(R.id.topicsAction);

        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.navigationFullName);
        fullAPIUrlView = headerLayout.findViewById(R.id.navigationAPIUrl);

        subjects = new ArrayList<>();
        RecyclerView subjectList = findViewById(R.id.subjectList);
        subjectList.setLayoutManager(new LinearLayoutManager(this));
        subjectsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_subject, subjects, new SubjectItemHolder.Factory());
        subjectsArrayAdapter.setOnItemClickListener(this);
        subjectList.setAdapter(subjectsArrayAdapter);


        refreshLayout.setOnRefreshListener(() -> reloadSubjects(true));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        reloadIfLoggedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }


    ////////////////////
    // LOGIN AND LOAD //
    ////////////////////

    private void reloadIfLoggedIn() {
        if (LoginData.isLoggedIn(this)) {
            reloadSubjects(false);
        } else {
            openLoginActivityThenReload();
        }
    }

    private void openLoginActivityThenReload() {
        AuthenticationExtractor.removeAllData();
        Extractor.removeAllData();
        LoginData.logout(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestCodeLoginActivity); // see onActivityResult
    }

    private void reloadSubjects(boolean reload) {
        marksMenuItem.setEnabled(false);
        topicsMenuItem.setEnabled(false);
        statisticsMenuItem.setEnabled(false);
        refreshLayout.setRefreshing(true);

        disposables.clear();
        subjects.clear();
        subjectsArrayAdapter.notifyDataSetChanged();
        authenticate(reload);
    }

    private void onReloadSubjectsCompleted() {
        marksMenuItem.setEnabled(true);

        // do not allow opening statistics if there is no mark
        for (SubjectData subject : subjects) {
            if (subject.getMarks() != null && subject.getMarks().size() != 0) {
                statisticsMenuItem.setEnabled(true);
            }
        }

        refreshLayout.setRefreshing(false);
    }


    /////////////
    // NETWORK //
    /////////////

    private void authenticate(boolean reload) {
        // show data in drawer header
        Extractor.setAPIUrl(LoginData.getAPIUrl(this));
        Extractor.setUser(LoginData.getUser(this));
        Extractor.setPassword(LoginData.getPassword(this));

        fullNameView.setText("");
        fullAPIUrlView.setText(Extractor.getFullAPIUrlToShow());

        disposables.add(AuthenticationExtractor.authenticateMain()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (fullName) -> onAuthenticationCompleted(fullName, reload),
                        throwable -> {
                            throwable.printStackTrace();
                            if (!(throwable instanceof ExtractorError)) return;
                            ExtractorError error = (ExtractorError) throwable;

                            if (error.isType(Type.invalid_credentials) || error.isType(Type.malformed_url)) {
                                Toast.makeText(this, error.getMessage(this), Toast.LENGTH_LONG)
                                        .show();
                                openLoginActivityThenReload();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.retry), v -> reloadSubjects(false))
                                        .show();
                            }
                            refreshLayout.setRefreshing(false);
                        }));
    }

    private void onAuthenticationCompleted(String fullName, boolean reload) {
        fullNameView.setText(fullName);

        fetchSubjects(reload);
    }


    private void fetchSubjects(boolean reload) {
        numSubjectsExtracted = 0;
        // never force reload subjects, as they usually do not change
        Extractor.extractSubjects(false, disposables, new Extractor.DataHandler<List<SubjectData>>() {
            @Override
            public void onExtractedData(List<SubjectData> data) {
                onSubjectsFetched(data, reload);
            }

            @Override
            public void onItemError(ExtractorError error) {
                Toast.makeText(MainActivity.this,
                        getString(R.string.error_could_not_load_a_subject), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(ExtractorError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(MainActivity.this), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void increaseNumSubjectsExtracted() {
        ++numSubjectsExtracted;
        if (numSubjectsExtracted == subjects.size()) {
            onReloadSubjectsCompleted();
        }
    }

    private void onSubjectsFetched(List<SubjectData> data, boolean reload) {
        subjects.addAll(data);
        topicsMenuItem.setEnabled(true);
        subjectsArrayAdapter.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

        for (SubjectData subject : subjects) {
            Extractor.extractMarks(subject, reload, disposables, new Extractor.DataHandler<SubjectData>() {
                @Override
                public void onExtractedData(SubjectData data) {
                    increaseNumSubjectsExtracted();
                    subjectsArrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemError(ExtractorError error) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.error_could_not_load_a_mark, subject.getName()), Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(ExtractorError error) {
                    increaseNumSubjectsExtracted();
                    Toast.makeText(MainActivity.this,
                            getString(R.string.error_could_not_load_marks, subject.getName()), Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestCodeLoginActivity:
                reloadIfLoggedIn();
                break;
        }
    }


    ////////////////
    // GUI EVENTS //
    ////////////////

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loginAction:
                openLoginActivityThenReload();
                break;
            case R.id.marksAction:
                openActivityWithAllSubjects(this, MarksActivity.class);
                break;
            case R.id.topicsAction:
                openActivityWithAllSubjects(this, TopicsActivity.class);
                break;
            case R.id.statisticsAction:
                openActivityWithAllSubjects(this, StatisticsActivity.class);
                break;
            case R.id.documentsAction:
                openActivity(this, DocumentsActivity.class);
                break;
            case R.id.settingsAction:
                openActivity(this, SettingsActivity.class);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(SubjectData subject) {
        if (subject.getMarks() == null || subject.getMarks().isEmpty()) {
            openActivityWithSubject(this, TopicsActivity.class, subject);
        } else {
            openActivityWithSubject(this, SubjectActivity.class, subject);
        }
    }
}
