package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
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
import com.stypox.mastercom_workbook.view.EventsActivity;
import com.stypox.mastercom_workbook.view.MarksActivity;
import com.stypox.mastercom_workbook.view.StatisticsActivity;
import com.stypox.mastercom_workbook.view.SubjectActivity;
import com.stypox.mastercom_workbook.view.TimetableActivity;
import com.stypox.mastercom_workbook.view.TopicsActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SubjectItemHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivity;
import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivityWithAllSubjects;
import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivityWithSubject;

public class MainActivity extends ThemedActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemArrayAdapter.OnItemClickListener<SubjectData> {

    private static final int requestCodeLoginActivity = 0;

    private CompositeDisposable disposables;

    private View welcomeMessageLayout;
    private Button loadMarksButton;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<SubjectData> subjectsArrayAdapter;

    private DrawerLayout drawer;
    private MenuItem marksMenuItem;
    private MenuItem topicsMenuItem;
    private MenuItem statisticsMenuItem;
    private MenuItem timetableMenuItem;

    private TextView fullNameView;
    private TextView fullAPIUrlView;

    private boolean loadMarksDirectly;
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

        welcomeMessageLayout = findViewById(R.id.welcomeMessageLayout);
        loadMarksButton = findViewById(R.id.loadMarksButton);

        refreshLayout = findViewById(R.id.refreshLayout);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        marksMenuItem = navigationView.getMenu().findItem(R.id.marksAction);
        statisticsMenuItem = navigationView.getMenu().findItem(R.id.statisticsAction);
        topicsMenuItem = navigationView.getMenu().findItem(R.id.topicsAction);
        timetableMenuItem = navigationView.getMenu().findItem(R.id.timetableAction);

        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.navigationFullName);
        fullAPIUrlView = headerLayout.findViewById(R.id.navigationAPIUrl);

        subjects = new ArrayList<>();
        RecyclerView subjectList = findViewById(R.id.subjectList);
        subjectList.setLayoutManager(new LinearLayoutManager(this));
        subjectsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_subject, subjects, SubjectItemHolder::new);
        subjectsArrayAdapter.setOnItemClickListener(this);
        subjectList.setAdapter(subjectsArrayAdapter);


        refreshLayout.setOnRefreshListener(() -> reloadSubjects(true));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // loading is started in onResume
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadIfLoggedIn(); // softly reload, the user could have changed some settings
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
        disposables.dispose();
        disposables = new CompositeDisposable();
        AuthenticationExtractor.removeAllData();
        Extractor.resetUserCachedData();
        LoginData.logout(this);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestCodeLoginActivity); // see onActivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeLoginActivity) {
            reloadIfLoggedIn();
        }
    }

    private void reloadSubjects(boolean reload) {
        if (!reload && Extractor.areSomeMarksExtracted()) {
            refreshLayout.setVisibility(View.VISIBLE);
            welcomeMessageLayout.setVisibility(View.GONE);
            refreshLayout.setRefreshing(!Extractor.areAllMarksExtracted());

            topicsMenuItem.setEnabled(true);
            timetableMenuItem.setEnabled(true);
            fullNameView.setText(AuthenticationExtractor.getFullName());
            fullAPIUrlView.setText(Extractor.getFullAPIUrlToShow());

            onSubjectsFetched(Extractor.getExtractedSubjects(), false);
            return;
        }

        loadMarksDirectly = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.key_load_marks_directly), true);
        if (reload || loadMarksDirectly) {
            refreshLayout.setVisibility(View.VISIBLE);
            welcomeMessageLayout.setVisibility(View.GONE);
        } else {
            refreshLayout.setVisibility(View.GONE);
            welcomeMessageLayout.setVisibility(View.VISIBLE);
            loadMarksButton.setVisibility(View.INVISIBLE);
            loadMarksButton.setOnClickListener(null);
        }

        marksMenuItem.setEnabled(false);
        statisticsMenuItem.setEnabled(false);
        topicsMenuItem.setEnabled(false);
        timetableMenuItem.setEnabled(false);
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


    //////////////
    // FETCHING //
    //////////////

    private void authenticate(boolean reload) {
        // show data in drawer header
        Extractor.setAPIUrl(LoginData.getAPIUrl(this));
        Extractor.setUser(LoginData.getUser(this));
        Extractor.setPassword(LoginData.getPassword(this));

        fullNameView.setText("");
        fullAPIUrlView.setText(Extractor.getFullAPIUrlToShow());

        disposables.add(AuthenticationExtractor.authenticateMain(reload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (fullName) -> onAuthenticationCompleted(fullName, reload),
                        throwable -> {
                            throwable.printStackTrace();
                            if (!(throwable instanceof ExtractorError)) return;
                            ExtractorError error = (ExtractorError) throwable;

                            if (error.isType(Type.invalid_credentials)
                                    || error.isType(Type.invalid_api_url)
                                    || error.isType(Type.malformed_url)) {
                                Toast.makeText(this, error.getMessage(this), Toast.LENGTH_LONG)
                                        .show();
                                openLoginActivityThenReload();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.retry), v -> reloadSubjects(true))
                                        .show();
                            }
                            refreshLayout.setRefreshing(false);
                        }));
    }

    private void onAuthenticationCompleted(String fullName, boolean reload) {
        timetableMenuItem.setEnabled(true);
        topicsMenuItem.setEnabled(true);
        fullNameView.setText(fullName);

        if (reload || loadMarksDirectly) {
            // start loading marks directly / show marks directly if they have already been loaded
            fetchSubjects(reload);
        } else {
            refreshLayout.setVisibility(View.GONE);
            welcomeMessageLayout.setVisibility(View.VISIBLE);
            loadMarksButton.setVisibility(View.VISIBLE);
            loadMarksButton.setOnClickListener(v -> fetchSubjects(reload));
        }
    }


    private void fetchSubjects(boolean reload) {
        refreshLayout.setVisibility(View.VISIBLE);
        welcomeMessageLayout.setVisibility(View.GONE);

        // never force reload subjects, as they usually do not change
        Extractor.extractSubjects(false, disposables, new Extractor.DataHandler<List<SubjectData>>() {
            @Override
            public void onExtractedData(List<SubjectData> data) {
                if (data.isEmpty()) {
                    onNoSubjectFetched();
                } else {
                    onSubjectsFetched(data, reload);
                }
            }

            @Override
            public void onItemError(ExtractorError error) {
                Toast.makeText(MainActivity.this,
                        getString(R.string.error_could_not_load_a_subject), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(ExtractorError error) {
                onNoSubjectFetched();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(MainActivity.this), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void onNoSubjectFetched() {
        refreshLayout.setRefreshing(false);
        topicsMenuItem.setEnabled(false);
    }

    private void increaseNumSubjectsExtracted() {
        ++numSubjectsExtracted;
        if (numSubjectsExtracted == subjects.size()) {
            onReloadSubjectsCompleted();
        }
    }

    private void onSubjectsFetched(List<SubjectData> data, boolean reload) {
        subjects.clear();
        subjects.addAll(data);
        subjectsArrayAdapter.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

        numSubjectsExtracted = 0;
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
        final int itemId = item.getItemId();
        if (itemId == R.id.marksAction) {
            openActivityWithAllSubjects(this, MarksActivity.class);
        } else if (itemId == R.id.statisticsAction) {
            openActivityWithAllSubjects(this, StatisticsActivity.class);
        } else if (itemId == R.id.topicsAction) {
            openActivityWithAllSubjects(this, TopicsActivity.class);
        } else if (itemId == R.id.timetableAction) {
            openActivity(this, TimetableActivity.class);
        } else if (itemId == R.id.eventsAction) {
            openActivity(this, EventsActivity.class);
        } else if (itemId == R.id.documentsAction) {
            openActivity(this, DocumentsActivity.class);
        } else if (itemId == R.id.loginAction) {
            openLoginActivityThenReload();
        } else if (itemId == R.id.settingsAction) {
            openActivity(this, SettingsActivity.class);
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
