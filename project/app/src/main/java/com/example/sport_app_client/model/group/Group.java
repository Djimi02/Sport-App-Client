package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Group {

    protected long id;

    protected UUID uuid;

    protected String name;

    protected Sports sport;

    public Group() {
        this.uuid = UUID.randomUUID();
    }

}