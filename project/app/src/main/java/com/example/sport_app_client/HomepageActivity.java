package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;

import retrofit2.Retrofit;

public class HomepageActivity extends AppCompatActivity {

    /* Views */


    /* Vars */
    private MyAuthManager authManager;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
        this.retrofit = new RetrofitService().getRetrofit();
    }

    private void initViews() {

    }

}