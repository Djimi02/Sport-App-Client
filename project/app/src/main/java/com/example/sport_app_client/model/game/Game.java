package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;

import java.time.LocalDate;
import java.util.Date;

public abstract class Game {

    protected Long id;
    protected LocalDate date;
    protected Sports sport;
    protected String results;

    public Game(LocalDate date, Sports sport) {
        this.date = date;
        this.sport = sport;
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
}