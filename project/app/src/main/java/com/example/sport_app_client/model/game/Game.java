package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;

import java.util.Date;

public abstract class Game {

    protected Long id;
    protected Date date;
    protected Sports sport;

    public Game(Date date, Sports sport) {
        this.date = date;
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Sports getSport() {
        return sport;
    }

    public void setSport(Sports sport) {
        this.sport = sport;
    }
}