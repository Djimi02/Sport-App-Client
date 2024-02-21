package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sport_app_client.retrofit.MyAuthManager;

public class HomepageActivity extends AppCompatActivity {

    /* Views */


    /* Vars */
    private MyAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
        test();
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
    }

    private void initViews() {

    }

    private void test() {
        TextView tv = findViewById(R.id.testTV);
        tv.setText(authManager.getUser().getUserName() + " " + authManager.getUser().getRole().toString());
        Button btn = findViewById(R.id.testBTN);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}