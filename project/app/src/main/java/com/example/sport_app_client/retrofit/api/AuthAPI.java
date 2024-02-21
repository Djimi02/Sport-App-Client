package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.retrofit.request.SignInRequest;
import com.example.sport_app_client.retrofit.request.SignUpRequest;
import com.example.sport_app_client.retrofit.response.JwtAuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
    @POST("/auth/signup")
    Call<JwtAuthenticationResponse> signUp(@Body SignUpRequest signUpRequest);

    @POST("/auth/signin")
    Call<JwtAuthenticationResponse> signIp(@Body SignInRequest signInRequest);
}
