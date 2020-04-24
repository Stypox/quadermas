package com.stypox.mastercom_workbook.login;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.stypox.mastercom_workbook.R;

public class LoginActivity extends AppCompatActivity {

    private EditText APIUrlEdit;
    private EditText userEdit;
    private EditText passwordEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.activity_title_login));


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

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> completeLogin());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private void completeLogin() {
        String APIUrl = APIUrlEdit.getText().toString();
        String user = userEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        LoginData.setCredentials(this, APIUrl, user, password);
        finish();
    }
}
