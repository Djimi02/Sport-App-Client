package com.example.sport_app_client.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.sport_app_client.LoginActivity;
import com.example.sport_app_client.retrofit.MyAuthManager;

public class LogOutHandler {

    public static void logout(Activity context) {
        MyAuthManager.deleteInstance();
        Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
        // Flags to clear the back stack of activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void logout(Activity context, String message) {
        MyAuthManager.deleteInstance();
        Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
        // Flags to clear the back stack of activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}