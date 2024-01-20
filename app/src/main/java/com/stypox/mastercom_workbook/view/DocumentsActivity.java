package com.stypox.mastercom_workbook.view;

import static com.stypox.mastercom_workbook.util.StringUtils.isBlank;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.DocumentExtractor;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.DocumentItemHolder;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class DocumentsActivity extends ThemedActivity
        implements Toolbar.OnMenuItemClickListener {
    private final int requestCodePermissionDialog = 432364;

    private CompositeDisposable disposables;
    private DocumentData lastDownloadDocument;

    private MenuItem selectYearMenuItem;
    private MenuItem selectSubjectMenuItem;
    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<DocumentData> documentsArrayAdapter;

    private int numClasses;
    private int numClassesExtracted;
    private List<DocumentData> documents;
    private List<DocumentData> filteredDocuments;

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
        actionBar.setTitle(getString(R.string.menu_documents));


        disposables = new CompositeDisposable();
        documents = new ArrayList<>();
        filteredDocuments = new ArrayList<>();

        refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView documentList = findViewById(R.id.documentList);

        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_document, filteredDocuments, DocumentItemHolder::new);
        documentsArrayAdapter.setOnItemClickListener(this::downloadDocument);
        documentList.setAdapter(documentsArrayAdapter);


        refreshLayout.setOnRefreshListener(() -> reloadDocuments(true));
        reloadDocuments(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.documents, menu);

        selectYearMenuItem = menu.findItem(R.id.selectYearAction);
        selectSubjectMenuItem = menu.findItem(R.id.selectSubjectAction);

        return true;
    }

    @Override
    public boolean onMenuItemClick(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId == R.id.selectYearAction) {
            showSelectYearDialog();
        } else if (itemId == R.id.selectSubjectAction) {
            showSelectSubjectDialog();
        } else {
            return false;
        }
        return true;
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadDocuments(boolean forceReload) {
        refreshLayout.setRefreshing(true);
        if (selectYearMenuItem != null) selectYearMenuItem.setEnabled(false);
        if (selectSubjectMenuItem != null) selectSubjectMenuItem.setEnabled(false);

        disposables.clear();
        documents.clear();
        filterAndShowDocuments();

        authenticateMessengerAndFetchDocuments(forceReload);
    }

    private void authenticateMessengerAndFetchDocuments(boolean reload) {
        disposables.add(AuthenticationExtractor.authenticateMessenger(reload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> fetchStudentThenDocuments(reload),
                        throwable -> onFatalError((ExtractorError) throwable)));
    }

    private void fetchStudentThenDocuments(boolean reload) {
        // never force reload student, as classes usually do not change
        Extractor.extractClasses(reload, disposables, new Extractor.DataHandler<List<ClassData>>() {
            @Override
            public void onExtractedData(List<ClassData> data) {
                onClassesFetched(data, reload);
            }

            @Override
            public void onItemError(ExtractorError error) {
                Toast.makeText(DocumentsActivity.this,
                        getString(R.string.error_could_not_load_class), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(ExtractorError error) {
                onFatalError(error);
            }
        });
    }

    private void increaseNumClassesExtracted() {
        ++numClassesExtracted;
        if (numClassesExtracted == numClasses) {
            refreshLayout.setRefreshing(false);
            if (selectYearMenuItem != null) selectYearMenuItem.setEnabled(true);
            if (selectSubjectMenuItem != null) selectSubjectMenuItem.setEnabled(true);
        }
    }

    private void onClassesFetched(List<ClassData> classes, boolean reload) {
        numClasses = classes.size();
        numClassesExtracted = 0;

        for (ClassData classData : classes) {
            Extractor.extractDocuments(classData, reload, disposables, new Extractor.DataHandler<ClassData>() {
                @Override
                public void onExtractedData(ClassData data) {
                    assert data.getDocuments() != null;
                    increaseNumClassesExtracted();
                    documents.addAll(data.getDocuments());
                    Collections.sort(documents, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                    filterAndShowDocuments();
                }

                @Override
                public void onItemError(ExtractorError error) {
                    Toast.makeText(DocumentsActivity.this,
                            getString(R.string.error_could_not_load_a_document, classData.getId()), Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(ExtractorError error) {
                    Toast.makeText(DocumentsActivity.this,
                            getString(R.string.error_could_not_load_documents, classData.getId()), Toast.LENGTH_LONG)
                            .show();
                    increaseNumClassesExtracted();
                }
            });
        }
    }

    private void onFatalError(ExtractorError error) {
        Snackbar.make(findViewById(android.R.id.content), error.getMessage(this), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), v -> reloadDocuments(true))
                .show();
        refreshLayout.setRefreshing(false);
    }


    //////////////
    // DOWNLOAD //
    //////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodePermissionDialog) {
            if (lastDownloadDocument != null) {
                downloadDocument(lastDownloadDocument);
                lastDownloadDocument = null;
            }
        }
    }

    public void downloadDocument(DocumentData documentData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
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
            // on Android 11+
            DocumentExtractor.downloadDocument(documentData, this);
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
                schoolYears.add(SecondTermStart.schoolYear(document.getDate()));
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
                })
                .show();
    }

    private void showSelectSubjectDialog() {
        Set<String> subjects = new TreeSet<>();
        for (DocumentData document : documents) {
            if (!isBlank(document.getSubject()) && isYearInsideFilter(document)) {
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
                })
                .show();
    }

    private boolean isYearInsideFilter(DocumentData document) {
        return  selectedYear == null ||
                selectedYear == SecondTermStart.schoolYear(document.getDate());
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
