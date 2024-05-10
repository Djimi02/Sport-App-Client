package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.stats.FBStats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FootballMember extends Member {

    protected FBStats stats;

    protected FootballGroup group;

    public FootballMember() {
        super.sport = Sports.FOOTBALL;
        this.stats = new FBStats();
    }


}