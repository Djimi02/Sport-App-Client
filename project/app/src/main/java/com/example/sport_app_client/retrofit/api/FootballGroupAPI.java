package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FootballGroupAPI {

    @GET("/group/football/get/{id}")
    Call<FootballGroup> getFootballGroup(@Path("id") Long groupID);

    @POST("/group/football/save/{name}/{userid}")
    Call<FootballGroup> createFootballGroup(@Path("name") String name, @Path("userid") Long userID);

    @POST("/group/football/add/member/{groupid}/{name}")
    Call<FootballMember> addFootballMember(@Path("groupid") Long groupID, @Path("name") String memberName);

}