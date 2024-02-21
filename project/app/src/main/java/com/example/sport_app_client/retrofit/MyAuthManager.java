package com.example.sport_app_client.retrofit;

import com.example.sport_app_client.model.User;

public class MyAuthManager {
    private User user;
    private String token;
    private static MyAuthManager instance;

    private MyAuthManager(){}

    public static MyAuthManager getInstance() {
        if (instance == null) {
            instance = new MyAuthManager();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}