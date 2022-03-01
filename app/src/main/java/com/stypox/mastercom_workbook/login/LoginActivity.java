package com.stypox.mastercom_workbook.login;

import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SchoolData;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.ExtractorError;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.settings.SettingsActivity;
import com.stypox.mastercom_workbook.util.StringUtils;
import com.stypox.mastercom_workbook.util.ThemedActivity;
import com.stypox.mastercom_workbook.view.holder.ItemArrayAdapter;
import com.stypox.mastercom_workbook.view.holder.SchoolItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginActivity extends ThemedActivity {

    private EditText APIUrlEdit;
    private View APIUrlLoading;
    private RecyclerView APIUrlList;
    private EditText userEdit;
    private EditText passwordEdit;

    private RecyclerView.LayoutManager APIUrlLayoutManager;
    private ItemArrayAdapter<SchoolData> APIUrlAdapter;
    private List<SchoolData> schools = null;
    private boolean currentlyExtractingSchools = false;
    private final CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        APIUrlEdit = findViewById(R.id.APIUrlEdit);
        APIUrlLoading = findViewById(R.id.APIUrlLoadingIndicator);
        APIUrlList = findViewById(R.id.APIUrlList);
        userEdit = findViewById(R.id.userEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        // show old credentials
        APIUrlEdit.setText(LoginData.getAPIUrl(this));
        userEdit.setText(LoginData.getUser(this));

        setupAPIUrlPopup();
        setupAPIUrlEdit();

        passwordEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                completeLogin();
            }
            return false;
        });

        findViewById(R.id.settingsButton).setOnClickListener(
                v -> openActivity(this, SettingsActivity.class));
        findViewById(R.id.loginButton).setOnClickListener(v -> completeLogin());
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && !touchesAPIUrlPopup(ev)) {
            // motion started outside of APIUrl popup: dismiss it
            hideAPIUrlPopup();
            APIUrlEdit.clearFocus();
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean touchesAPIUrlPopup(final MotionEvent ev) {
        final View currentlyOpenPopup;
        if (APIUrlLoading.getVisibility() == View.VISIBLE) {
            currentlyOpenPopup = APIUrlLoading;
        } else if (APIUrlList.getVisibility() == View.VISIBLE) {
            currentlyOpenPopup = APIUrlList;
        } else {
            return true;
        }

        final Rect currentlyOpenPopupRect = new Rect();
        currentlyOpenPopup.getHitRect(currentlyOpenPopupRect);
        if (currentlyOpenPopupRect.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }

        final Rect APIUrlEditRect = new Rect();
        APIUrlEdit.getHitRect(APIUrlEditRect);
        return APIUrlEditRect.contains((int) ev.getX(), (int) ev.getY());
    }

    private void setupAPIUrlEdit() {
        APIUrlEdit.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                showAPIUrlPopup();
            } else {
                hideAPIUrlPopup();
            }
        });

        APIUrlEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence,
                                          final int i,
                                          final int i1,
                                          final int i2) {}

            @Override
            public void onTextChanged(final CharSequence charSequence,
                                      final int i,
                                      final int i1,
                                      final int i2) {}

            @Override
            public void afterTextChanged(final Editable editable) {
                showAPIUrlPopup();
            }
        });
    }

    private void setupAPIUrlPopup() {
        APIUrlAdapter = new ItemArrayAdapter<>(R.layout.item_login_api_url,
                Collections.emptyList(), SchoolItemHolder::new);
        APIUrlAdapter.setOnItemClickListener(school -> {
            APIUrlEdit.setText(school.getAPIUrl());
            hideAPIUrlPopup();
            userEdit.requestFocus(); // focus next field
        });

        APIUrlLayoutManager = new LinearLayoutManager(this);
        APIUrlList.setLayoutManager(APIUrlLayoutManager);
        APIUrlList.setAdapter(APIUrlAdapter);
    }

    private void hideAPIUrlPopup() {
        APIUrlLoading.setVisibility(View.GONE);
        APIUrlList.setVisibility(View.GONE);
    }

    private void showAPIUrlPopup() {
        if (schools == null) {
            APIUrlLoading.setVisibility(View.VISIBLE);
            APIUrlList.setVisibility(View.GONE);

            if (!currentlyExtractingSchools) {
                currentlyExtractingSchools = true;
                Extractor.extractSchools(disposable, new Extractor.DataHandler<List<SchoolData>>() {
                    @Override
                    public void onExtractedData(final List<SchoolData> data) {
                        currentlyExtractingSchools = false;
                        //noinspection ComparatorCombinators
                        Collections.sort(data, (a, b) -> a.getName().compareTo(b.getName()));
                        schools = data;
                        if (APIUrlLoading.getVisibility() == View.VISIBLE) {
                            // update popup window if it is being shown
                            showAPIUrlPopup();
                        }
                    }

                    @Override
                    public void onItemError(final ExtractorError error) {}

                    @Override
                    public void onError(final ExtractorError error) {
                        // hide the popup and set an empty list as schools
                        currentlyExtractingSchools = false;
                        schools = new ArrayList<>();
                        hideAPIUrlPopup();
                    }
                });
            }

        } else {
            final List<SchoolData> toBeShown = getSchoolsToBeShown();
            if (toBeShown.isEmpty()) {
                // nothing to show: dismiss window
                hideAPIUrlPopup();

            } else {
                APIUrlLoading.setVisibility(View.GONE);
                APIUrlList.setVisibility(View.VISIBLE);
                APIUrlLayoutManager.scrollToPosition(0); // text changed: scroll to top
                APIUrlAdapter.setDataItems(toBeShown);
            }
        }
    }

    // could be greatly simplified with Java streams API
    private List<SchoolData> getSchoolsToBeShown() {
        class SchoolWithScore {
            final int score;
            final SchoolData schoolData;

            public SchoolWithScore(int score, SchoolData schoolData) {
                this.score = score;
                this.schoolData = schoolData;
            }
        }

        final String text = APIUrlEdit.getText().toString().trim();
        if (text.isEmpty()) {
            return schools;
        }

        final List<SchoolWithScore> schoolsWithScore = new ArrayList<>();
        for (final SchoolData school : schools) {
            final int score = StringUtils.customStringDistance(text, school.getAPIUrl())
                    + StringUtils.customStringDistance(text, school.getName())
                    + StringUtils.customStringDistance(text, school.getMunicipality());
            schoolsWithScore.add(new SchoolWithScore(score, school));
        }

        //noinspection ComparatorCombinators
        Collections.sort(schoolsWithScore, (a, b) -> Integer.compare(a.score, b.score));
        final List<SchoolData> result = new ArrayList<>();
        for (final SchoolWithScore schoolWithScore : schoolsWithScore) {
            result.add(schoolWithScore.schoolData);
        }
        return result;
    }

    private void completeLogin() {
        final String APIUrl = APIUrlEdit.getText().toString();
        final String user = userEdit.getText().toString();
        final String password = passwordEdit.getText().toString();

        LoginData.setCredentials(this, APIUrl, user, password);
        final SecondTermStart secondTermStart = SecondTermStart.fromAPIUrl(APIUrl);
        if (secondTermStart != null) {
            secondTermStart.saveToPreferences(this); // change second term start based on API url
        }

        finish();
    }
}
