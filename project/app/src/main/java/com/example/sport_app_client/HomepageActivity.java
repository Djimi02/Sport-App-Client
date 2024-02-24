package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sport_app_client.model.User;
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
        test();
    }

    private void test() {
        User user = authManager.getUser();
        System.out.println(user.getMembers().get(0).numberOfGroups());
        Toast.makeText(this, user.getUserName(), Toast.LENGTH_SHORT).show();
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
        this.retrofit = new RetrofitService().getRetrofit();
    }

    private void initViews() {

    }

}