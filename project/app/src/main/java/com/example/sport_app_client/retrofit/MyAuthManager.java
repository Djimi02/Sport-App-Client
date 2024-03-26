package com.example.sport_app_client.retrofit;

import com.example.sport_app_client.model.User;

public class MyAuthManager {
    public static User user = null;
    public static String token = "";

    public static void resetData() {
        user = null;
        token = "";
    }

    private MyAuthManager(){
    }
}