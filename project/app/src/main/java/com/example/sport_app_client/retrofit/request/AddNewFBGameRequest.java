package com.example.sport_app_client.retrofit.request;

import com.example.sport_app_client.model.member.FootballMember;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddNewFBGameRequest {
    private List<FootballMember> membersGameStats;
    private Long groupID;
    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    public AddNewFBGameRequest() {}
}