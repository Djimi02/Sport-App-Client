package com.example.sport_app_client.retrofit.request;

import com.example.sport_app_client.model.member.FootballMember;

import java.util.List;

public class AddNewFBGameRequest {
    private List<FootballMember> updatedMembers;
    private List<FootballMember> membersGameStats;
    private Long groupID;
    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    public AddNewFBGameRequest() {
    }

    public List<FootballMember> getUpdatedMembers() {
        return updatedMembers;
    }

    public void setUpdatedMembers(List<FootballMember> updatedMembers) {
        this.updatedMembers = updatedMembers;
    }

    public List<FootballMember> getMembersGameStats() {
        return membersGameStats;
    }

    public void setMembersGameStats(List<FootballMember> membersGameStats) {
        this.membersGameStats = membersGameStats;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Integer getVictory() {
        return victory;
    }

    public void setVictory(Integer victory) {
        this.victory = victory;
    }
}