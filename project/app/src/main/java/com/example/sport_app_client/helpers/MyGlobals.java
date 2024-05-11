package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;

public final class MyGlobals {

    /* Constants */
    public final static String ERROR_MESSAGE_1 = "Unable to execute this action now!";
    public final static String ERROR_MESSAGE_2 = "Check your internet or try again later!";

    /* Pointers to objects */
    private static Group group = null;
    public static Group getGroup() {
        return group;
    }
    private static Member associatedMember = null;
    public static Member getAssociatedMember() {
        return associatedMember;
    }

    // Used group and game activities
    public static GameCreatedListener gameCreatedListenerGroup = null;

    // Used by homepage, group/game activities
    public static GameCreatedListener gameCreatedListenerHomepage = null;

    // Used by homepage and group activities
    public static CreateOrJoinOrLeaveGroupListener createJoinLeaveGroupListenerHomepageActivity = null;

    /* FOOTBALL */
    private static FootballGroup footballGroup = null;
    public static FootballGroup getFootballGroup() {
        return footballGroup;
    }
    public static void setFootballGroup(FootballGroup group1) {
        footballGroup = group1;
        group = group1;
    }

    private static FootballMember associatedFBMember = null;
    public static FootballMember getAssociatedFBMember() {
        return associatedFBMember;
    }
    public static void setAssociatedFBMember(FootballMember member1) {
        associatedFBMember = member1;
        if (member1 != null) {
            // Set abstract values
            associatedFBMember.setGroupAbs(associatedFBMember.getGroup());
            associatedFBMember.setStatsAbs(associatedFBMember.getStats());
        }
        associatedMember = member1;
    }

    private MyGlobals() {}
}