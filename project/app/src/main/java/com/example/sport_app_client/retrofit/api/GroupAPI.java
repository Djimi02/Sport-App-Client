package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.group.FootballGroup;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupAPI {

    @GET("/group/football/get/{id}")
    Call<FootballGroup> getFootballGroup(@Path("id") Long groupID);

}