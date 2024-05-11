package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.helpers.UserLoadConfig;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.AuthAPI;
import com.example.sport_app_client.retrofit.request.SignUpRequest;
import com.example.sport_app_client.retrofit.response.JwtAuthenticationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    /* Views */
    private ProgressBar progressBar;
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;
    private EditText userNameET;
    private Button registerBTN;

    /* Vars */
    private Retrofit retrofit;
    private AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getSupportActionBar().hide();
        Toast.makeText(this, "On create!", Toast.LENGTH_SHORT).show();

        initVars();
        initViews();
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.authAPI = retrofit.create(AuthAPI.class);
    }

    private void initViews() {
        this.progressBar = findViewById(R.id.registerPagePB);
        this.emailET = findViewById(R.id.RegistrationPageEmailET);
        this.passwordET = findViewById(R.id.RegistrationPagePasswordET);
        this.repeatPasswordET = findViewById(R.id.RegistrationPageRepPassET);
        this.userNameET = findViewById(R.id.RegistrationPageUsernameET);
        this.registerBTN = findViewById(R.id.RegistrationPageRegisterBTN);
        registerBTN.setOnClickListener(view -> {
            register();
            GlobalMethods.hideSoftKeyboard(RegisterActivity.this);
        });
    }

    private void register() {
        String email = emailET.getText().toString().trim();
        String pass1 = passwordET.getText().toString().trim();
        String pass2 = repeatPasswordET.getText().toString().trim();
        String userName = userNameET.getText().toString().trim();

        if (!isDataValid(email, pass1, pass2, userName)) {
            return;
        }
        // Data is valid

        // Show progress bar and disable UI interactions
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        SignUpRequest newSignUpRequest = new SignUpRequest(userName, email, pass1);
        this.authAPI.signUp(newSignUpRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                if (response.code() == 200) {
//                    MyAuthManager.token = response.body().getToken();
//                    MyAuthManager.user = response.body().getUser();
                    UserLoadConfig.configureUserData(response.body());

                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, HomepageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (response.code() == 400) {
                    try {
                        Toast.makeText(RegisterActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
                // Hide progress bar and allow UI interactions
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(RegisterActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                System.out.println(t.toString());
                // Hide progress bar and allow UI interactions
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
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
        } else if (nickName.length() > 12) {
            userNameET.setError("Nickname should be at most 12 characters long!");
            output = false;
        }

        return output;
    }
}