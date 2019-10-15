package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.SubjectExtractor;
import com.stypox.mastercom_workbook.extractor.ExtractorData;
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

    private CompositeDisposable disposables;

    private LinearLayout subjectsLayout;
    private SwipeRefreshLayout refreshLayout;

    private MenuItem marksMenuItem;
    private MenuItem statisticsMenuItem;
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
        marksMenuItem = navigationView.getMenu().findItem(R.id.menu_marks);
        statisticsMenuItem = navigationView.getMenu().findItem(R.id.menu_statistics);

        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.nav_fullNameView);
        fullAPIUrlView = headerLayout.findViewById(R.id.nav_fullAPIUrlView);

        refreshLayout.setOnRefreshListener(this::reloadSubjects);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.menu_marks).setEnabled(false);
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
        marksMenuItem.setEnabled(false);
        statisticsMenuItem.setEnabled(false);
        refreshLayout.setRefreshing(true);
        subjectsLayout.removeAllViews();

        disposables.clear();
        subjects = new ArrayList<>();
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

        disposables.add(AuthenticationExtractor.authenticate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onAuthenticationCompleted,
                        throwable -> {
                            if(!(throwable instanceof ExtractorError)) return;
                            ExtractorError error = (ExtractorError) throwable;
                            error.printStackTrace();

                            if (error.isType(Type.invalid_credentials) || error.isType(Type.malformed_url)) {
                                Toast.makeText(this, error.getMessage(this), Toast.LENGTH_LONG)
                                        .show();
                                openLoginDialogThenReload();
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
    private void onMarkExtractionError(String subjectName) {
        new Handler(getMainLooper()).post(() ->
            Toast.makeText(this, getString(R.string.error_could_not_load_a_mark, subjectName), Toast.LENGTH_LONG)
                    .show()
        );
    }
    private void onSubjectFetched(SubjectData subjectData) {
        subjects.add(subjectData);
        subjectsLayout.addView(new SubjectItem(this, subjectData));
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    private void openMarksActivity() {
        Intent intent = new Intent(this, MarksActivity.class);
        intent.putExtra(MarksActivity.subjectsIntentKey, subjects);
        startActivity(intent);
    }

    private void openStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(StatisticsActivity.subjectsIntentKey, subjects);
        startActivityForResult(intent, requestCodeStatisticsActivity);
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
