package com.example.sport_app_client.helpers;

import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.BasketballMember;
import com.example.sport_app_client.model.member.FootballMember;

public class GroupLoadConfig {

    public static void configureFBGroupData(FootballGroup group) {
        group.setGamesAbs(group.getGames());
        group.setMembersAbs(group.getMembers());
        for (FootballMember member : group.getMembers()){
            member.setGroupAbs(member.getGroup());
            member.setStatsAbs(member.getStats());
        }
        MyGlobals.setFootballGroup(group);

    }

    public static void configureBBGroupData(BasketballGroup group) {
        group.setGamesAbs(group.getGames());
        group.setMembersAbs(group.getMembers());
        for (BasketballMember member : group.getMembers()){
            member.setGroupAbs(member.getGroup());
            member.setStatsAbs(member.getStats());
        }
        MyGlobals.setBasketballGroup(group);

    }

}