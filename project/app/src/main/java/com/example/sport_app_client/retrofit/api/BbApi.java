package com.example.sport_app_client.retrofit.api;

import com.example.sport_app_client.model.game.BasketballGame;
import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.member.BasketballMember;
import com.example.sport_app_client.model.stats.BBStats;
import com.example.sport_app_client.retrofit.request.AddNewBBGameRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BbApi {

    /* GROUP */

    @GET("/basketball/group/get/{id}")
    Call<BasketballGroup> getGroupByID(@Path("id") Long groupID);

    @GET("/basketball/group/get/uuid/{uuid}")
    Call<BasketballGroup> getGroupByUUID(@Path("uuid") String groupUUID);

    @POST("/basketball/group/save/{name}/{userid}")
    Call<BasketballGroup> createGroup(@Path("name") String name, @Path("userid") Long userID);

    @POST("/basketball/group/join/notnew/{userid}/{memberid}")
    Call<Void> joinGroupAsExistingMember(@Path("userid") long userID, @Path("memberid") long memberID);

    @POST("/basketball/group/join/new/{userid}/{groupid}")
    Call<BasketballMember> joinGroupAsNewMember(@Path("userid") long userID, @Path("groupid") long groupID);

    @DELETE("/basketball/group/delete/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupID);

    /* Member */

    @POST("/basketball/member/save/{groupid}/{name}")
    Call<BasketballMember> addMember(@Path("groupid") Long groupID, @Path("name") String memberName);

    @POST("/basketball/member/role/admin/{id}")
    Call<Void> setRoleToAdmin(@Path("id") Long memberID);

    @POST("/basketball/member/role/gamemaker/{id}")
    Call<Void> setRoleToGameMaker(@Path("id") Long memberID);

    @POST("/basketball/member/role/member/{id}")
    Call<Void> setRoleToMember(@Path("id") Long memberID);

    @DELETE("/basketball/member/delete/{id}")
    Call<Void> removeMemberFromGroup(@Path("id") Long memberID);

    /* Game */

    @GET("/basketball/game/get/gamestats/{id}")
    Call<List<BBStats>> getGameStats(@Path("id") Long gameID);

    @POST("/basketball/game/save")
    Call<BasketballGame> addNewGame(@Body AddNewBBGameRequest request);

    @DELETE("/basketball/game/delete/{id}")
    Call<Void> deleteGame(@Path("id") Long gameID);

}