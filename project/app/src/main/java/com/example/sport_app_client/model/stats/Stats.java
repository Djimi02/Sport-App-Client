package com.example.sport_app_client.model.stats;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Stats<MemberT extends Member<?,?>, GameT extends Game<?>> {

    protected Long id;

    protected Integer wins;
    protected Integer draws;
    protected Integer loses;

    protected GameT game;

    protected MemberT member;

    protected Sports sport;

    public Stats() { initVars(); }

    public Stats(Sports sport) {
        this.sport = sport;
        initVars();
    }

    private void initVars() {
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
    }

}