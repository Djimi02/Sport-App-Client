package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sport_app_client.model.User;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.AuthAPI;
import com.example.sport_app_client.retrofit.request.SignUpRequest;
import com.example.sport_app_client.retrofit.response.JwtAuthenticationResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    /* Views */
    private EditText emailET;
    private EditText passwordET;
    private EditText repeatPasswordET;
    private EditText userNameET;
    private Button registerBTN;

    /* Vars */
    private Retrofit retrofit;
    private AuthAPI authAPI;
    private MyAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.authAPI = retrofit.create(AuthAPI.class);
        this.authManager = MyAuthManager.getInstances();
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
        String email = emailET.getText().toString().trim();
        String pass1 = passwordET.getText().toString().trim();
        String pass2 = repeatPasswordET.getText().toString().trim();
        String userName = userNameET.getText().toString().trim();

        if (!isDataValid(email, pass1, pass2, userName)) {
            return;
        }
        // Data is valid

        SignUpRequest newSignUpRequest = new SignUpRequest(userName, email, pass1);
        this.authAPI.signUp(newSignUpRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                if (response.code() == 200) {
                    authManager.setToken(response.body().getToken());
                    authManager.setUser(new User(userName, email, pass1));
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, HomepageActivity.class));
                } else if (response.code() == 400) {
                    try {
                        Toast.makeText(RegisterActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
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