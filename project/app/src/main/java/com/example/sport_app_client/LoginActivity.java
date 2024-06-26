package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.helpers.UserLoadConfig;
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
    }

    private void initViews() {
        this.emailET = findViewById(R.id.LogInPageEmailET);

        this.passwordET = findViewById(R.id.LogInPagePasswordET);

        this.logInBTN = findViewById(R.id.LogInPageLogInBTN);
        logInBTN.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();
            logIn();
            GlobalMethods.hideSoftKeyboard(LoginActivity.this);
        });

        this.goToRegisterBTN = findViewById(R.id.LogInPageRegisterBTN);
        goToRegisterBTN.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        this.progressBar = findViewById(R.id.logInPagePB);

        this.forgotPasswordTV = findViewById(R.id.logInPageForgotPasswordTV);
        forgotPasswordTV.setOnClickListener(view -> {
            Toast.makeText(LoginActivity.this, "reset email dialog xD", Toast.LENGTH_SHORT).show();
//                openResetEmailDialog();
        });
    }

    private void logIn() {
        String email = emailET.getText().toString().trim();
        String pass = passwordET.getText().toString().trim();

        if (!isDataValid(email, pass)) {
            return;
        }
        // Data is valid

        // Show progress bar and disable UI interactions
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        SignInRequest newSignInRequest = new SignInRequest(email, pass);
        authAPI.signIn(newSignInRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                if (response.code() == 200) {
//                    MyAuthManager.token = response.body().getToken();
//                    MyAuthManager.user = response.body().getUser();
                    UserLoadConfig.configureUserData(response.body());

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    try {
                        Toast.makeText(LoginActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                // Hide progress bar and allow UI interactions
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                System.out.println(t.toString());
                // Hide progress bar and allow UI interactions
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
}