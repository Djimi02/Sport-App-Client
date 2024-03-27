package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Game<GroupT extends Group<?,?>, MemberT extends Member<?>> {

    protected Long id;
    protected LocalDate date;
    protected Sports sport;
    protected String results;
    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    private GroupT group;

    private List<MemberT> members;

    public Game() {initVars();}

    public Game(LocalDate date, Sports sport, GroupT group) {
        this.date = date;
        this.sport = sport;
        this.victory = null;
        this.group = group;
        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
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

    public void addMember(MemberT member) {
        this.members.add(member);
    }

    public void removeMember(Long memberID) {
        int memberToBeRemoved = -1;
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).getId() == memberID) {
                memberToBeRemoved = i;
                break;
            }
        }

        if (memberToBeRemoved != -1) {
            this.members.remove(memberToBeRemoved);
        }
    }

    public GroupT getGroup() {
        return group;
    }

    public void setGroup(GroupT group) {
        this.group = group;
    }

    public List<MemberT> getMembers() {
        return members;
    }

    public void setMembers(List<MemberT> members) {
        this.members = members;
    }
}