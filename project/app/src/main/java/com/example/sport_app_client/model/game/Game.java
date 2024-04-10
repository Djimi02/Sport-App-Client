package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Game<GroupT extends Group<?,?>> {

    protected Long id;
    protected LocalDate date;
    protected Sports sport;
    protected String results;
//    protected Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    protected GroupT group;

    public Game() {initVars();}

    public Game(LocalDate date, Sports sport, GroupT group) {
        this.date = date;
        this.sport = sport;
        this.group = group;
        initVars();
    }

    private void initVars() {
//        this.victory = null;
    }

}