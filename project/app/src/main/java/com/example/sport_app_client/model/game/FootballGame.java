package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FootballGame extends Game<FootballGroup, FootballMember> {
    public FootballGame(LocalDate date, FootballGroup group) {
        super(date, Sports.FOOTBALL, group);
    }

}