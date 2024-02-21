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
import android.widget.Toast;

import com.example.sport_app_client.model.User;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.AuthAPI;
import com.example.sport_app_client.retrofit.request.SignInRequest;
import com.example.sport_app_client.retrofit.response.JwtAuthenticationResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private Retrofit retrofit;
    private AuthAPI authAPI;
    private MyAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {
        lastClickTime = 0;

        this.retrofit = new RetrofitService().getRetrofit();
        this.authAPI = retrofit.create(AuthAPI.class);
        this.authManager = MyAuthManager.getInstance();
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
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
        String email = emailET.getText().toString().trim();
        String pass = passwordET.getText().toString().trim();

        if (!isDataValid(email, pass)) {
            return;
        }
        // Data is valid

        SignInRequest newSignInRequest = new SignInRequest(email, pass);
        authAPI.signIn(newSignInRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                if (response.code() == 200) {
                    authManager.setToken(response.body().getToken());
                    authManager.setUser(response.body().getUser());
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {

            }
        });

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