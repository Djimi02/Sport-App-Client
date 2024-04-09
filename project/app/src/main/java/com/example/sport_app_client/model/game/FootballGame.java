package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;

import java.time.LocalDate;

public class FootballGame extends Game<FootballGroup> {
    public FootballGame(LocalDate date, FootballGroup group) {
        super(date, Sports.FOOTBALL, group);
    }

}