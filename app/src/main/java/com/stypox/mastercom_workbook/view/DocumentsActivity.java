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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.data.StudentData;
import com.stypox.mastercom_workbook.extractor.AuthenticationExtractor;
import com.stypox.mastercom_workbook.extractor.DocumentsExtractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.extractor.StudentExtractor;
import com.stypox.mastercom_workbook.view.holder.DocumentItemHolder;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class DocumentsActivity extends AppCompatActivity {
    private final int requestCodePermissionDialog = 0;

    private CompositeDisposable disposables;
    private DocumentData lastDownloadDocument;

    private SwipeRefreshLayout refreshLayout;
    private ItemArrayAdapter<DocumentData> documentsArrayAdapter;

    private int nrClasses;
    private int nrClassesFetched;
    private List<DocumentData> documents;


    ////////////////////////
    // ACTIVITY LIFECYCLE //
    ////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposables = new CompositeDisposable();

        refreshLayout = findViewById(R.id.refreshLayout);

        documents = new ArrayList<>();
        RecyclerView documentList = findViewById(R.id.documentList);
        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentsArrayAdapter = new ItemArrayAdapter<>(R.layout.item_document, documents, new DocumentItemHolder.Factory());
        documentsArrayAdapter.setOnItemClickListener(this::downloadDocument);
        documentList.setAdapter(documentsArrayAdapter);


        refreshLayout.setOnRefreshListener(this::reloadDocuments);
        reloadDocuments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    /////////////
    // LOADING //
    /////////////

    private void reloadDocuments() {
        refreshLayout.setRefreshing(true);

        disposables.clear();
        documents.clear();
        documentsArrayAdapter.notifyDataSetChanged();
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
                            this::onDocumentsFetched,
                            throwable -> onError(throwable, false)));
        }
    }

    private void onDocumentsFetched(List<DocumentData> fetchedDocuments) {
        documents.addAll(fetchedDocuments);
        Collections.sort(documents, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        documentsArrayAdapter.notifyDataSetChanged();

        ++nrClassesFetched;
        if (nrClassesFetched == nrClasses) {
            refreshLayout.setRefreshing(false);
        }
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
}
