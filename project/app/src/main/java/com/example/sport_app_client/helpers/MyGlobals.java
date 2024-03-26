package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.group.FootballGroup;

public final class MyGlobals {

    /* Constants */
    public final static String ERROR_MESSAGE_1 = "Unable to execute this action now!";
    public final static String ERROR_MESSAGE_2 = "Check your internet or try again later!";

    /* Pointers to objects */
    public static User currentUser = null;

    public static CreateOrJoinOrLeaveGroupListener createOrJoinOrLeaveGroupListener = null;

    public static FootballGroup footballGroup = null;

    public static GameCreatedListener gameCreatedListener = null;

    private MyGlobals() {}
}