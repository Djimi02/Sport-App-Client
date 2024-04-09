package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.stats.FBStats;

public interface OpenFBMemberStatDialog {
    public void openDialog(String memberName, FBStats stats);
}