package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    /* Views */
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;
    private EditText userNameET;
    private Button registerBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {

    }

    private void initViews() {
        this.emailET = findViewById(R.id.RegistrationPageEmailET);
        this.passwordET = findViewById(R.id.RegistrationPagePasswordET);
        this.repeatPasswordET = findViewById(R.id.RegistrationPageRepPassET);
        this.userNameET = findViewById(R.id.RegistrationPageUsernameET);
        this.registerBTN = findViewById(R.id.RegistrationPageRegisterBTN);
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
                hideSoftKeyboard(RegisterActivity.this);
            }
        });
    }

    private void register() {
    }

    private boolean isDataValid(String email, String password, String repPassword, String nickName) {
        boolean output = true;

        if (email.isEmpty()) {
            emailET.setError("Input your email!");
            output = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Input correct email!");
            output = false;
        }

        if (password.isEmpty()) {
            passwordET.setError("Input your password!");
            output = false;
        } else if (password.length() < 6) {
            passwordET.setError("Password should be at least 6 characters long!");
            output = false;
        }

        if (repPassword.isEmpty()) {
            repeatPasswordET.setError("Repeat your password!");
            output = false;
        } else if (!repPassword.equals(password)) {
            repeatPasswordET.setError("The passwords should match!");
            output = false;
        }

        if (nickName.isEmpty()) {
            userNameET.setError("Input your nickname!");
            output = false;
        } else if (nickName.length() < 4) {
            userNameET.setError("Nickname should be at least 4 characters long!");
            output = false;
        }

        return output;
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}