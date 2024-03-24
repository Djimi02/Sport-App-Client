package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.LeaveGroupListener;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.group.FootballGroup;

import java.util.List;

public final class MyGlobals {

    public static User currentUser = null;

    public static LeaveGroupListener leaveGroupListener = null;

    public static FootballGroup footballGroup = null;

    public static GameCreatedListener gameCreatedListener = null;

    private MyGlobals() {}
}