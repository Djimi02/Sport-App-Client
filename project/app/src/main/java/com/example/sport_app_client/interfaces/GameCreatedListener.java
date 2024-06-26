package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface GameCreatedListener {
    /** This method updates the recyclers in the current fragment/activity. */
    default public void onGameCreatedGroupIMPL() {}

    default public void onGameCreatedOrDeletedHomepageIMPL(Member member) {}
}