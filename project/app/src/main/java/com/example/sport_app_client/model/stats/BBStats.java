package com.example.sport_app_client.model.stats;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.BasketballGame;
import com.example.sport_app_client.model.member.BasketballMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BBStats extends Stats {

    private int points;
    private int numberOfThreePoints;
    private int numOfDunks;
    private int blocks;
    private int fouls;

    private BasketballGame game;
    private BasketballMember member;

    public BBStats() {
        super.sport = Sports.BASKETBALL;
        initVars();
    }

    private void initVars() {
        this.points = 0;
        this.numberOfThreePoints = 0;
        this.blocks = 0;
        this.fouls = 0;
        this.numOfDunks = 0;
    }
}
