package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.stats.FBStats;
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
    Call<FootballGroup> getGroupByID(@Path("id") Long groupID);

    @GET("/football/group/get/uuid/{uuid}")
    Call<FootballGroup> getGroupByUUID(@Path("uuid") String groupUUID);

    @POST("/football/group/save/{name}/{userid}")
    Call<FootballGroup> createGroup(@Path("name") String name, @Path("userid") Long userID);

    @POST("/football/group/join/notnew/{userid}/{memberid}")
    Call<Void> joinGroupAsExistingMember(@Path("userid") long userID, @Path("memberid") long memberID);

    @POST("/football/group/join/new/{userid}/{groupid}")
    Call<FootballMember> joinGroupAsNewMember(@Path("userid") long userID, @Path("groupid") long groupID);

    @DELETE("/football/group/delete/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupID);

    /* Member */

    @POST("/football/member/save/{groupid}/{name}")
    Call<FootballMember> addMember(@Path("groupid") Long groupID, @Path("name") String memberName);

    @POST("/football/member/role/admin/{id}")
    Call<Void> setRoleToAdmin(@Path("id") Long memberID);

    @POST("/football/member/role/gamemaker/{id}")
    Call<Void> setRoleToGameMaker(@Path("id") Long memberID);

    @POST("/football/member/role/member/{id}")
    Call<Void> setRoleToMember(@Path("id") Long memberID);

    @DELETE("/football/member/delete/{id}")
    Call<Void> removeMemberFromGroup(@Path("id") Long memberID);

    /* Game */

    @GET("/football/game/get/gamestats/{id}")
    Call<List<FBStats>> getGameStats(@Path("id") Long gameID);

    @POST("/football/game/save")
    Call<FootballGame> addNewGame(@Body AddNewFBGameRequest request);

    @DELETE("/football/game/delete/{id}")
    Call<Void> deleteGame(@Path("id") Long gameID);

}