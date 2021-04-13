package com.stypox.mastercom_workbook.login;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.util.ThemedActivity;

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

        final View loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> completeLogin());
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
        finish();
    }
}
