package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.stats.FBStats;
import com.example.sport_app_client.model.stats.Stats;

public interface OpenFBMemberStatDialog {
    public void openDialog(Stats<?,?> stats);
}