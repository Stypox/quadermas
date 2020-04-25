package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.stypox.mastercom_workbook.extractor.ExtractorData;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.ExtractorError.Type;
import com.stypox.mastercom_workbook.extractor.SubjectExtractor;
import com.stypox.mastercom_workbook.login.LoginActivity;
import com.stypox.mastercom_workbook.login.LoginData;
import com.stypox.mastercom_workbook.settings.SettingsActivity;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.DocumentsActivity;
import com.stypox.mastercom_workbook.view.MarksActivity;
import com.stypox.mastercom_workbook.view.StatisticsActivity;
import com.stypox.mastercom_workbook.view.SubjectActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SubjectItemHolder;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends ThemedActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int requestCodeLoginActivity = 0;

    private CompositeDisposable disposables;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<SubjectData> subjectsArrayAdapter;

    private DrawerLayout drawer;
    private MenuItem marksMenuItem;
    private MenuItem statisticsMenuItem;
    private TextView fullNameView;
    private TextView fullAPIUrlView;

    private ArrayList<SubjectData> subjects;


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

        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.navigationFullName);
        fullAPIUrlView = headerLayout.findViewById(R.id.navigationAPIUrl);

        subjects = new ArrayList<>();
        RecyclerView subjectList = findViewById(R.id.subjectList);
        subjectList.setLayoutManager(new LinearLayoutManager(this));
        subjectsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_subject, subjects, new SubjectItemHolder.Factory());
        subjectsArrayAdapter.setOnItemClickListener(this::openSubjectActivity);
        subjectList.setAdapter(subjectsArrayAdapter);


        refreshLayout.setOnRefreshListener(this::reloadSubjects);

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
            reloadSubjects();
        } else {
            openLoginActivityThenReload();
        }
    }

    private void openLoginActivityThenReload() {
        LoginData.logout(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestCodeLoginActivity); // see onActivityResult
    }

    private void reloadSubjects() {
        marksMenuItem.setEnabled(false);
        statisticsMenuItem.setEnabled(false);
        refreshLayout.setRefreshing(true);

        disposables.clear();
        subjects.clear();
        subjectsArrayAdapter.notifyDataSetChanged();
        authenticate();
    }

    private void onReloadSubjectsCompleted() {
        marksMenuItem.setEnabled(true);
        statisticsMenuItem.setEnabled(true);
        refreshLayout.setRefreshing(false);
    }


    /////////////
    // NETWORK //
    /////////////

    private void authenticate() {
        // show data in drawer header
        ExtractorData.setAPIUrl(LoginData.getAPIUrl(this));
        ExtractorData.setUser(LoginData.getUser(this));
        ExtractorData.setPassword(LoginData.getPassword(this));

        fullNameView.setText("");
        fullAPIUrlView.setText(ExtractorData.getFullAPIUrlToShow());

        disposables.add(AuthenticationExtractor.authenticateMain()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onAuthenticationCompleted,
                        throwable -> {
                            if (!(throwable instanceof ExtractorError)) return;
                            ExtractorError error = (ExtractorError) throwable;
                            error.printStackTrace();

                            if (error.isType(Type.invalid_credentials) || error.isType(Type.malformed_url)) {
                                Toast.makeText(this, error.getMessage(this), Toast.LENGTH_LONG)
                                        .show();
                                openLoginActivityThenReload();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.retry), v -> reloadSubjects())
                                        .show();
                            }
                            refreshLayout.setRefreshing(false);
                        }));
    }

    private void onAuthenticationCompleted(String fullName) {
        fullNameView.setText(fullName);

        fetchSubjects();
    }


    private void fetchSubjects() {
        disposables.add(SubjectExtractor.fetchSubjects(this::onMarkExtractionError)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SubjectData>() {
                    @Override
                    public void onNext(SubjectData subjectData) {
                        onSubjectFetched(subjectData);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (!(throwable instanceof ExtractorError)) return;
                        ExtractorError error = (ExtractorError) throwable;
                        error.printStackTrace();

                        Snackbar.make(findViewById(android.R.id.content), error.getMessage(MainActivity.this), Snackbar.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onComplete() {
                        onReloadSubjectsCompleted();
                    }
                }));
    }

    private void onMarkExtractionError(String subjectName) {
        new Handler(getMainLooper()).post(() ->
                Toast.makeText(this, getString(R.string.error_could_not_load_a_mark, subjectName), Toast.LENGTH_LONG)
                        .show()
        );
    }

    private void onSubjectFetched(SubjectData subjectData) {
        subjects.add(subjectData);
        subjectsArrayAdapter.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodeLoginActivity:
                reloadIfLoggedIn();
                break;
        }
    }

    private void openSubjectActivity(SubjectData subjectData) {
        Intent intent = new Intent(this, SubjectActivity.class);
        intent.putExtra(SubjectActivity.subjectDataIntentKey, subjectData);
        startActivity(intent);
    }

    private void openMarksActivity() {
        Intent intent = new Intent(this, MarksActivity.class);
        intent.putExtra(MarksActivity.subjectsIntentKey, subjects);
        startActivity(intent);
    }

    private void openStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(StatisticsActivity.subjectsIntentKey, subjects);
        startActivity(intent);
    }

    private void openDocumentsActivity() {
        Intent intent = new Intent(this, DocumentsActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
                openMarksActivity();
                break;
            case R.id.statisticsAction:
                openStatisticsActivity();
                break;
            case R.id.documentsAction:
                openDocumentsActivity();
                break;
            case R.id.settingsAction:
                openSettingsActivity();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
