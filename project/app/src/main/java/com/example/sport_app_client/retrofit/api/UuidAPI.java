package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.retrofit.response.GroupTypeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UuidAPI {

    @GET("/uuid/{uuid}")
    public Call<GroupTypeResponse> getGroupTypeByUUID(@Path("uuid") String uuid);

}