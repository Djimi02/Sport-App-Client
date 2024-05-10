package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Game {

    protected Long id;
    protected LocalDate date;
    protected Sports sport;
    protected String results;

    public Game() {
        Calendar calendar = Calendar.getInstance();
        this.date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }


}