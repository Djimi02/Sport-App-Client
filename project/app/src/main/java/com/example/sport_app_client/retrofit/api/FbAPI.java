package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.retrofit.request.AddNewFBGameRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FbAPI {

    /* GROUP */

    @GET("/football/group/get/{id}")
    Call<FootballGroup> getFootballGroup(@Path("id") Long groupID);

    @POST("/football/group/save/{name}/{userid}")
    Call<FootballGroup> createFootballGroup(@Path("name") String name, @Path("userid") Long userID);

    @DELETE("/football/group/delete/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupID);

    /* Member */

    @POST("/football/member/save/{groupid}/{name}")
    Call<FootballMember> addFootballMember(@Path("groupid") Long groupID, @Path("name") String memberName);

    @DELETE("/football/member/delete/{id}")
    Call<Void> removeMemberFromGroup(@Path("id") Long memberID);

    /* Game */

    @GET("/football/game/get/gamestats/{id}")
    Call<List<FootballMember>> getGameStats(@Path("id") Long gameID);

    @POST("/football/game/save")
    Call<FootballGame> addNewFootballGame(@Body AddNewFBGameRequest request);

    @DELETE("/football/game/delete/{id}")
    Call<Void> deleteGame(@Path("id") Long gameID);

}