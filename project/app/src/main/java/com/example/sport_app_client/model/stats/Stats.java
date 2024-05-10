package com.example.sport_app_client.model.stats;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Stats {

    protected Long id;

    protected String memberName;

    protected Integer wins;
    protected Integer draws;
    protected Integer loses;
    protected Boolean isPartOfTeam1; // used for games

    protected Sports sport;

    public Stats() { initVars(); }

    private void initVars() {
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
        this.isPartOfTeam1 = null;
    }

}