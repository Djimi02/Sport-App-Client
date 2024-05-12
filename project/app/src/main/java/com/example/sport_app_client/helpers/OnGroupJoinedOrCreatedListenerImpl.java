package com.example.sport_app_client.helpers;

import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;

public class OnGroupJoinedOrCreatedListenerImpl {

    // ================== On Group Created ============================
    public static void onFBGroupCreated(Group group) {
        // Create temporary group so that they don't have cyclic references to each
        // other which will throw exception during request sending
        FootballGroup tempGroup = new FootballGroup();
        tempGroup.setName(group.getName());
        tempGroup.setId(group.getId());

        // Create temp member
        FootballMember initialMember = new FootballMember();
        initialMember.setNickname(MyGlobals.getFootballGroup().getMembers().get(0).getNickname());
        initialMember.setId(MyGlobals.getFootballGroup().getMembers().get(0).getId());
        initialMember.setGroupAbs(tempGroup); // only the abs value is used in homepage
        initialMember.setStatsAbs(initialMember.getStats()); // only the abs value is used in homepage

        MyAuthManager.user.getMembers().add(initialMember);
    }


    // ================== On Group Joined ============================

    public static void onFBGroupJoined(Member member, Group group) {
        // Create temporary group so that they don't have cyclic references to each
        // other which will throw exception during request sending
        FootballGroup tempGroup = new FootballGroup();
        tempGroup.setName(group.getName());
        tempGroup.setId(group.getId());
        tempGroup.setName(group.getName());
        tempGroup.setUuid(group.getUuid());

        // Create temp member
        FootballMember tempMember = new FootballMember();
        tempMember.setNickname(member.getNickname());
        tempMember.setGroupAbs(tempGroup);
        tempMember.setStatsAbs(tempMember.getStats());
        tempMember.getStatsAbs().setWins(member.getStatsAbs().getWins());
        tempMember.getStatsAbs().setDraws(member.getStatsAbs().getDraws());
        tempMember.getStatsAbs().setLoses(member.getStatsAbs().getLoses());
        tempMember.setId(member.getId());

        MyAuthManager.user.getMembers().add(tempMember);
    }
}