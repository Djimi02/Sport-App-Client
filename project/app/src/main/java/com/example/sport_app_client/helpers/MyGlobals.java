package com.example.sport_app_client.helpers;

import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.List;

public final class MyGlobals {

    public static FootballGroup footballGroup = null;

    public static GameCreatedListener gameCreatedListener = null;

    private MyGlobals() {}
}