package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.retrofit.request.AddNewFBGameRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FBGroupAPI {

    @GET("/group/football/get/{id}")
    Call<FootballGroup> getFootballGroup(@Path("id") Long groupID);

    @POST("/group/football/save/{name}/{userid}")
    Call<FootballGroup> createFootballGroup(@Path("name") String name, @Path("userid") Long userID);

    @POST("/group/football/add/member/{groupid}/{name}")
    Call<FootballMember> addFootballMember(@Path("groupid") Long groupID, @Path("name") String memberName);

    @POST("/group/football/add/game")
    Call<FootballGame> addNewFootballGame(@Body AddNewFBGameRequest request);

    @GET("/group/football/get/gamestats/{id}")
    Call<List<FootballMember>> getGameStats(@Path("id") Long gameID);

}