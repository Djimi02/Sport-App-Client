package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;

public abstract class Group {

    protected Long id;

    protected String name;

    protected Sports sport;

    public Group(String name, Sports sport) {
        this.name = name;
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sports getSport() {
        return sport;
    }

    public void setSport(Sports sport) {
        this.sport = sport;
    }
}