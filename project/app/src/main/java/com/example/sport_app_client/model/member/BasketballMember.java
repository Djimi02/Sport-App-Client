package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.stats.BBStats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketballMember extends Member {

    protected BBStats stats;

    protected BasketballGroup group;

    public BasketballMember() {
        super.sport = Sports.BASKETBALL;
        this.stats = new BBStats();
    }

}