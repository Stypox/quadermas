package com.stypox.mastercom_workbook.login;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.settings.SettingsActivity;
import com.stypox.mastercom_workbook.util.ThemedActivity;

import static com.stypox.mastercom_workbook.util.NavigationHelper.openActivity;

public class LoginActivity extends ThemedActivity {

    private EditText APIUrlEdit;
    private EditText userEdit;
    private EditText passwordEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        APIUrlEdit = findViewById(R.id.APIUrlEdit);
        userEdit = findViewById(R.id.userEdit);
        passwordEdit = findViewById(R.id.passwordEdit);


        // show old credentials
        APIUrlEdit.setText(LoginData.getAPIUrl(this));
        userEdit.setText(LoginData.getUser(this));

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
    public void onBackPressed() {
        moveTaskToBack(true);
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
