package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.group.FootballGroup;

public final class MyGlobals {

    public static User currentUser = null;

    public static CreateOrJoinOrLeaveGroupListener createOrJoinOrLeaveGroupListener = null;

    public static FootballGroup footballGroup = null;

    public static GameCreatedListener gameCreatedListener = null;

    private MyGlobals() {}
}