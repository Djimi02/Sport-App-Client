package com.example.sport_app_client.retrofit.request;

import com.example.sport_app_client.model.stats.Stats;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AddNewGameRequest<StatsT extends Stats> {
    private Map<Long, StatsT> gameStats; // key is member id and value is member stats
    private Long groupID;
}