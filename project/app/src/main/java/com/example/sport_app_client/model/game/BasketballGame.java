package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketballGame extends Game {

    private FootballGroup group;

    public BasketballGame() {super.sport = Sports.BASKETBALL;}

}