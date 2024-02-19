package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    /* Views */
    private EditText emailET;
    private EditText passwordET;
    private Button logInBTN;
    private TextView goToRegisterBTN;
    private ProgressBar progressBar;
    private TextView forgotPasswordTV;

    /* Vars */
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initVars();
        initViews();
    }

    private void initVars() {
        lastClickTime = 0;
    }

    private void initViews() {
        this.emailET = findViewById(R.id.LogInPageEmailET);

        this.passwordET = findViewById(R.id.LogInPagePasswordET);

        this.logInBTN = findViewById(R.id.LogInPageLogInBTN);
        logInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                logIn();
                hideSoftKeyboard(LoginActivity.this);
            }
        });

        this.goToRegisterBTN = findViewById(R.id.LogInPageRegisterBTN);
        goToRegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        this.progressBar = findViewById(R.id.logInPagePB);
        progressBar.setVisibility(View.GONE);

        this.forgotPasswordTV = findViewById(R.id.logInPageForgotPasswordTV);
        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openResetEmailDialog();
            }
        });
    }

    private void logIn() {
    }

    private boolean isDataValid(String email, String password) {
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

        return output;
    }

    private boolean isNetworkAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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