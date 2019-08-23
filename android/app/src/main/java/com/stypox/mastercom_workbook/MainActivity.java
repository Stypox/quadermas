package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.ExtractorError.Type;
import com.stypox.mastercom_workbook.login.LoginData;
import com.stypox.mastercom_workbook.login.LoginDialog;
import com.stypox.mastercom_workbook.view.MarksActivity;
import com.stypox.mastercom_workbook.view.StatisticsActivity;
import com.stypox.mastercom_workbook.view.SubjectItem;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int requestCodeLoginDialog = 0;
    private static final int requestCodeStatisticsActivity = 1;

    private boolean areSubjectsLoaded = false;

    private CompositeDisposable disposables;

    private LinearLayout subjectsLayout;
    private SwipeRefreshLayout refreshLayout;
    private TextView fullNameView;
    private TextView fullAPIUrlView;

    private ArrayList<SubjectData> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposables = new CompositeDisposable();

        subjectsLayout = findViewById(R.id.subjectsLayout);
        refreshLayout = findViewById(R.id.refreshLayout);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.nav_fullNameView);
        fullAPIUrlView = headerLayout.findViewById(R.id.nav_fullAPIUrlView);

        refreshLayout.setOnRefreshListener(this::reloadSubjects);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        reloadIfLoggedIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodeLoginDialog:
                reloadIfLoggedIn();
                break;
            case requestCodeStatisticsActivity:
                if (resultCode == StatisticsActivity.resultErrorNoMarks) {
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.no_marks_for_statistics), Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    ////////////////////
    // LOGIN AND LOAD //
    ////////////////////

    private void reloadIfLoggedIn() {
        if (LoginData.isLoggedIn(getApplicationContext())) {
            reloadSubjects();
        } else {
            openLoginDialogThenReload();
        }
    }

    private void openLoginDialogThenReload() {
        LoginData.logout(getApplicationContext());
        Intent intent = new Intent(this, LoginDialog.class);
        startActivityForResult(intent, requestCodeLoginDialog); // see onActivityResult
    }

    private void reloadSubjects() {
        refreshLayout.setRefreshing(true);
        subjectsLayout.removeAllViews();

        areSubjectsLoaded = false;
        disposables.clear();
        subjects = new ArrayList<>();
        authenticate();
    }

    private void onReloadSubjectsCompleted() {
        areSubjectsLoaded = true;
        refreshLayout.setRefreshing(false);
    }


    /////////////
    // NETWORK //
    /////////////

    private void authenticate() {
        // show data in drawer header
        Extractor.setAPIUrl(LoginData.getAPIUrl(getApplicationContext()));
        fullNameView.setText("");
        fullAPIUrlView.setText(Extractor.getFullAPIUrlToShow());

        disposables.add(Extractor.authenticate(LoginData.getUser(this), LoginData.getPassword(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onAuthenticationCompleted,
                        throwable -> {
                            if(!(throwable instanceof ExtractorError)) return;
                            ExtractorError error = (ExtractorError) throwable;
                            error.printStackTrace();

                            if (error.isType(Type.invalid_credentials) || error.isType(Type.malformed_url)) {
                                Toast.makeText(getApplicationContext(), error.getMessage(this), Toast.LENGTH_LONG)
                                        .show();
                                openLoginDialogThenReload();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.retry), v -> authenticate())
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
        disposables.add(Extractor.fetchSubjects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SubjectData>() {
                    @Override
                    public void onNext(SubjectData subjectData) {
                        onSubjectFetched(subjectData);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(!(throwable instanceof ExtractorError)) return;
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
    private void onSubjectFetched(SubjectData subjectData) {
        subjects.add(subjectData);
        subjectsLayout.addView(new SubjectItem(this, subjectData));
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    private void openMarksActivity() {
        if (areSubjectsLoaded) {
            Intent intent = new Intent(this, MarksActivity.class);
            intent.putExtra(MarksActivity.subjectsIntentKey, subjects);
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.error_marks_are_still_loading), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), v -> openMarksActivity()).show();
        }
    }

    private void openStatisticsActivity() {
        if (areSubjectsLoaded) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra(StatisticsActivity.subjectsIntentKey, subjects);
            startActivityForResult(intent, requestCodeStatisticsActivity);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.error_marks_are_still_loading), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), v -> openStatisticsActivity()).show();
        }
    }

    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    ////////////////
    // GUI EVENTS //
    ////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_login:
                openLoginDialogThenReload();
                break;
            case R.id.menu_marks:
                openMarksActivity();
                break;
            case R.id.menu_statistics:
                openStatisticsActivity();
                break;
            case R.id.menu_source_code:
                openUrlInBrowser(getResources().getString(R.string.source_code_url));
                break;
            case R.id.menu_report_bug:
                openUrlInBrowser(getResources().getString(R.string.report_bug_url));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
