package com.stypox.mastercom_workbook.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stypox.mastercom_workbook.R;

public class LoginDialog extends AppCompatActivity {
    private EditText APIUrlEdit;
    private EditText userEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        APIUrlEdit = findViewById(R.id.APIUrlEdit);
        userEdit = findViewById(R.id.userEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        APIUrlEdit.setText(LoginData.getAPIUrl(getApplicationContext()));
        userEdit.setText(LoginData.getUser(getApplicationContext()));

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String APIUrl = APIUrlEdit.getText().toString();
                String user = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                LoginData.setCredentials(getApplicationContext(), APIUrl, user, password);
                finish();
            }
        });
    }
}
