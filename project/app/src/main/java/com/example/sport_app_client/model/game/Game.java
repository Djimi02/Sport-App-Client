package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;

import java.time.LocalDate;
import java.util.Date;

public abstract class Game {

    protected Long id;
    protected LocalDate date;
    protected Sports sport;
    protected String results;
    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    public Game(LocalDate date, Sports sport) {
        this.date = date;
        this.sport = sport;
        this.victory = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Sports getSport() {
        return sport;
    }

    public void setSport(Sports sport) {
        this.sport = sport;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public Integer getVictory() {
        return victory;
    }

    public void setVictory(Integer victory) {
        this.victory = victory;
    }
}