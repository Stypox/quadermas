package com.stypox.mastercom_workbook.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.data.StudentData;
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.DocumentsExtractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.StudentExtractor;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.view.holder.DocumentItemHolder;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class DocumentsActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {
    private final int requestCodePermissionDialog = 0;

    private CompositeDisposable disposables;
    private DocumentData lastDownloadDocument;

    private MenuItem selectYearMenuItem;
    private MenuItem selectSubjectMenuItem;
    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<DocumentData> documentsArrayAdapter;

    private int nrClasses;
    private int nrClassesFetched;
    private List<DocumentData> documents;
    List<DocumentData> filteredDocuments;

    private Integer selectedYear = null;
    private String selectedSubject = null;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.activity_title_documents));


        disposables = new CompositeDisposable();
        documents = new ArrayList<>();
        filteredDocuments = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView documentList = findViewById(R.id.documentList);

        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_document, filteredDocuments, new DocumentItemHolder.Factory());
        documentsArrayAdapter.setOnItemClickListener(this::downloadDocument);
        documentList.setAdapter(documentsArrayAdapter);


        refreshLayout.setOnRefreshListener(this::reloadDocuments);
        reloadDocuments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.documents, menu);

        selectYearMenuItem = menu.findItem(R.id.selectYearAction);
        selectSubjectMenuItem = menu.findItem(R.id.selectSubjectAction);

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
            case R.id.selectYearAction:
                showSelectYearDialog();
                return true;
            case R.id.selectSubjectAction:
                showSelectSubjectDialog();
                return true;
            default:
                return false;
        }
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadDocuments() {
        refreshLayout.setRefreshing(true);
        if (selectYearMenuItem != null) selectYearMenuItem.setEnabled(false);
        if (selectSubjectMenuItem != null) selectSubjectMenuItem.setEnabled(false);

        disposables.clear();
        documents.clear();
        filterAndShowDocuments();
        fetchDocuments();
    }

    private void fetchDocuments() {
        disposables.add(AuthenticationExtractor.authenticateMessenger()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessengerAuthenticated,
                        throwable -> onError(throwable, true)));
    }

    private void onMessengerAuthenticated() {
        disposables.add(StudentExtractor.fetchStudent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onStudentFetched,
                        throwable -> onError(throwable, true)));
    }

    private void onStudentFetched(StudentData studentData) {
        nrClasses = studentData.getClasses().size();
        nrClassesFetched = 0;

        for (ClassData classData : studentData.getClasses()) {
            disposables.add(DocumentsExtractor.fetchDocuments(classData.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (documentData) -> {
                                onDocumentsFetched(documentData);
                                increaseFetchedClasses();
                            },
                            throwable -> {
                                onError(throwable, false);
                                increaseFetchedClasses();
                            }));
        }
    }

    private void increaseFetchedClasses() {
        ++nrClassesFetched;
        if (nrClassesFetched == nrClasses) {
            refreshLayout.setRefreshing(false);
            if (selectYearMenuItem != null) selectYearMenuItem.setEnabled(true);
            if (selectSubjectMenuItem != null) selectSubjectMenuItem.setEnabled(true);
        }
    }

    private void onDocumentsFetched(List<DocumentData> fetchedDocuments) {
        documents.addAll(fetchedDocuments);
        Collections.sort(documents, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        filterAndShowDocuments();
    }

    private void onError(Throwable throwable, boolean fatal) {
        if(!(throwable instanceof ExtractorError)) return;
        ExtractorError error = (ExtractorError) throwable;
        error.printStackTrace();

        if (fatal) {
            Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), v -> reloadDocuments())
                    .show();
            refreshLayout.setRefreshing(false);
        } else {
            Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    //////////////
    // DOWNLOAD //
    //////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodePermissionDialog:
                if (lastDownloadDocument != null) {
                    downloadDocument(lastDownloadDocument);
                    lastDownloadDocument = null;
                }
                break;
        }
    }

    public void downloadDocument(DocumentData documentData) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (lastDownloadDocument != null) {
                // user just denied permission
                return;
            }

            // onActivityResult waits for when we are ready to download
            lastDownloadDocument = documentData;
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCodePermissionDialog);

        } else {
            DocumentsExtractor.downloadDocument(documentData, this);
        }
    }


    ///////////////
    // FILTERING //
    ///////////////

    private void filterAndShowDocuments() {
        filteredDocuments.clear();
        for (DocumentData document : documents) {
            if (isYearInsideFilter(document) && isSubjectInsideFilter(document)) {
                filteredDocuments.add(document);
            }
        }

        documentsArrayAdapter.notifyDataSetChanged();
    }

    private void showSelectYearDialog() {
        TreeSet<Integer> schoolYears = new TreeSet<>();
        for (DocumentData document : documents) {
            if (isSubjectInsideFilter(document)) {
                schoolYears.add(DateUtils.schoolYear(document.getDate()));
            }
        }

        String[] options = new String[schoolYears.size() + 1];
        options[0] = getString(R.string.all_years);

        int i = 1;
        for (Integer schoolYear : schoolYears.descendingSet()) {
            options[i] = schoolYearRepresentation(schoolYear);
            ++i;
        }

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(options, getCheckedItemIndex(options, schoolYearRepresentation(selectedYear)), (dialog, which) -> {
                    if (which == 0) {
                        selectedYear = null;
                    } else {
                        selectedYear = (Integer) schoolYears.toArray()[schoolYears.size() - which];
                    }
                    filterAndShowDocuments();
                    dialog.dismiss();
                }).show();
    }

    private void showSelectSubjectDialog() {
        Set<String> subjects = new TreeSet<>();
        for (DocumentData document : documents) {
            if (isYearInsideFilter(document)) {
                subjects.add(document.getSubject());
            }
        }

        String[] options = new String[subjects.size() + 1];
        options[0] = getString(R.string.all_subjects);

        int i = 1;
        for (String subject : subjects) {
            options[i] = subject;
            ++i;
        }

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(options, getCheckedItemIndex(options, selectedSubject), (dialog, which) -> {
                    if (which == 0) {
                        selectedSubject = null;
                    } else {
                        selectedSubject = options[which];
                    }
                    filterAndShowDocuments();
                    dialog.dismiss();
                }).show();
    }

    private boolean isYearInsideFilter(DocumentData document) {
        return  selectedYear == null ||
                selectedYear == DateUtils.schoolYear(document.getDate());
    }

    private boolean isSubjectInsideFilter(DocumentData document) {
        return  selectedSubject == null ||
                selectedSubject.equals(document.getSubject());
    }

    @Nullable
    private String schoolYearRepresentation(@Nullable Integer schoolYear) {
        if (schoolYear == null) {
            return null;
        }

        return getString(R.string.two_strings,
                String.valueOf(schoolYear), String.valueOf(schoolYear+1));
    }

    private int getCheckedItemIndex(String[] options, @Nullable String selected) {
        if (selected == null) {
            return 0;
        }

        for (int i = 0; i < options.length; ++i) {
            if (options[i].equals(selected)) {
                return i;
            }
        }

        return 0;
    }
}
