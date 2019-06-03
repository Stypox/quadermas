package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.AuthenticationCallback;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.FetchMarksCallback;
import com.stypox.mastercom_workbook.extractor.FetchSubjectsCallback;
import com.stypox.mastercom_workbook.view.SubjectItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout subjectsLayout;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subjectsLayout = findViewById(R.id.subjectsLayout);
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                authenticate();
            }
        });

        refreshLayout.setRefreshing(true);
        authenticate();

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSubjectActivity = new Intent(MainActivity.this, SubjectActivity.class);
                //toolbar.collapseActionView();
                MainActivity.this.startActivity(openSubjectActivity);
            }
        });*/

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(v, "Ciao", Snackbar.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    /////////////
    // NETWORK //
    /////////////

    private void authenticate() {
        Extractor.authenticate("", "", new AuthenticationCallback() {
            @Override
            public void onAuthenticationCompleted(String fullName) {
                MainActivity.this.onAuthenticationCompleted(fullName);
            }

            @Override
            public void onError(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                authenticate();
                            }
                        }).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
    private void onAuthenticationCompleted(String fullName) {
        Snackbar.make(findViewById(android.R.id.content), "Authenticated " + fullName, Snackbar.LENGTH_LONG).show();
        refreshLayout.setRefreshing(false);

        fetchSubjects();
    }

    private void fetchSubjects() {
        Extractor.fetchSubjects(new FetchSubjectsCallback() {
            @Override
            public void onFetchSubjectsCompleted(ArrayList<SubjectData> subjects) {
                MainActivity.this.onFetchSubjectsCompleted(subjects);
            }

            @Override
            public void onError(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void onFetchSubjectsCompleted(ArrayList<SubjectData> subjects) {
        for(SubjectData subjectData : subjects) {
            Log.i("fetchSubjects", subjectData.getName());
            final SubjectItem subjectItem = new SubjectItem(getApplicationContext(), subjectData);

            subjectData.fetchMarks(new FetchMarksCallback() {
                @Override
                public void onFetchMarksCompleted(ArrayList<MarkData> marks) {
                    subjectItem.onMarksLoaded(marks);
                }

                @Override
                public void onError(String error) {
                    subjectItem.onMarksLoadingError(error);
                }
            });

            subjectsLayout.addView(subjectItem);
        }

        Snackbar.make(findViewById(android.R.id.content), "Subjects", Snackbar.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_subjects) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
