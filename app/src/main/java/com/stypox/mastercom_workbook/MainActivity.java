package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.ExtractorData;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.ExtractorError.Type;
import com.stypox.mastercom_workbook.extractor.SubjectExtractor;
import com.stypox.mastercom_workbook.login.LoginData;
import com.stypox.mastercom_workbook.login.LoginDialog;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int requestCodeLoginDialog = 0;

    private CompositeDisposable disposables;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<SubjectData> subjectsArrayAdapter;

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        marksMenuItem = navigationView.getMenu().findItem(R.id.menu_marks);
        statisticsMenuItem = navigationView.getMenu().findItem(R.id.menu_statistics);

        View headerLayout = navigationView.getHeaderView(0);
        fullNameView = headerLayout.findViewById(R.id.nav_fullNameView);
        fullAPIUrlView = headerLayout.findViewById(R.id.nav_fullAPIUrlView);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        subjectsArrayAdapter.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodeLoginDialog:
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
        switch (item.getItemId()) {
            case R.id.menu_login:
                openLoginDialogThenReload();
                break;
            case R.id.menu_marks:
                openMarksActivity();
                break;
            case R.id.menu_statistics:
                openStatisticsActivity();
                break;
            case R.id.menu_documents:
                openDocumentsActivity();
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
