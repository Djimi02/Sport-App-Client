package com.example.sport_app_client.model.stats;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FBStats extends Stats {

    private int goals;
    private int assists;
    private int saves;
    private int fouls;

    protected FootballGame game;
    protected FootballMember member;

    public FBStats() {
        super.sport = Sports.FOOTBALL;
        initVars();
    }

    private void initVars() {
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.fouls = 0;
        this.isPartOfTeam1 = null;
    }
}