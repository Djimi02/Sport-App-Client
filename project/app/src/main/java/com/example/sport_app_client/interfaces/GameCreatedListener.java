package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface GameCreatedListener {
    default public void onGameCreatedGroupIMPL() {}

    default public void onGameCreatedOrDeletedHomepageIMPL(Member<?> member) {}
}