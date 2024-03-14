package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.group.Group;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

public abstract class Member {
    protected Long id;
    protected String nickname;
    protected User user;
    protected Sports sport;

    protected Group group;

    protected Integer wins;
    protected Integer draws;
    protected Integer loses;

    public Member() {initVars();}

    public Member(String nickname, Sports sport, Group group) {
        this.nickname = nickname;
        this.sport = sport;
        this.group = group;

        initVars();
    }

    private void initVars() {
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Sports getSport() {
        return sport;
    }

    public void setSport(Sports sport) {
        this.sport = sport;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }
}