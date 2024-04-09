package com.example.sport_app_client.retrofit.request;

import com.example.sport_app_client.model.stats.FBStats;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddNewFBGameRequest {
    private Map<Long, FBStats> gameStats; // key is member id and value is member stats
    private Long groupID;
    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    public AddNewFBGameRequest() {}
}