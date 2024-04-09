package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.stats.FBStats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FootballMember extends Member<FootballGroup, FBStats> {

    public FootballMember() {
        setSport(Sports.FOOTBALL);
    }

    public FootballMember(String nickname, FootballGroup group) {
        super(nickname, Sports.FOOTBALL, group, new FBStats());
    }
}