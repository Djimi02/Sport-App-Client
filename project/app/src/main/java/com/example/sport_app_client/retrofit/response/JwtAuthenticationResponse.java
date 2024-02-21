package com.example.sport_app_client.retrofit.response;

import com.example.sport_app_client.model.User;

public class JwtAuthenticationResponse {
    private String token;
    private User user;

    public JwtAuthenticationResponse() {}

    public JwtAuthenticationResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}