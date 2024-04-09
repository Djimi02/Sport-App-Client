package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;

public final class MyGlobals {

    /* Constants */
    public final static String ERROR_MESSAGE_1 = "Unable to execute this action now!";
    public final static String ERROR_MESSAGE_2 = "Check your internet or try again later!";

    /* Pointers to objects */
    public static Group<?,?> group = null;
    public static Member<?,?> associatedMember = null;

    public static GameCreatedListener gameCreatedListenerGroup = null;

    public static GameCreatedListener gameCreatedListenerHomepage = null;

    public static CreateOrJoinOrLeaveGroupListener createOrJoinOrLeaveGroupListener = null;

    /* FOOTBALL */
    public static FootballGroup footballGroup = null;
    public static FootballMember associatedFBMember = null;

    private MyGlobals() {}
}