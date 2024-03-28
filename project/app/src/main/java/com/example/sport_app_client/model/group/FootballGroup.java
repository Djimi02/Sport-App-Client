package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;

public class FootballGroup extends Group<FootballMember, FootballGame> {

    public FootballGroup(String name) {
        super(name, Sports.FOOTBALL);
    }

}