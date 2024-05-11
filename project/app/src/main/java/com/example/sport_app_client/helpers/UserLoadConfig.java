package com.example.sport_app_client.helpers;

import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.response.JwtAuthenticationResponse;

public class UserLoadConfig {

    /**
     * Sets values to global variables MyAuthManager.token and MyAuthManager.user and
     * gives value to transient fields Member.groupAbs and Member.statsAbs, which are
     * used for abstraction purposes.
     * @param response - the response returned by the backend API
     */
    public static void configureUserData(JwtAuthenticationResponse response) {
        MyAuthManager.token = response.getToken();
        MyAuthManager.user = response.getUser();

        for (Member member : MyAuthManager.user.getMembers()) {
            switch (member.getSport()){
                case FOOTBALL:
                    FootballMember parsedMember = (FootballMember) member;
                    member.setGroupAbs(parsedMember.getGroup());
                    member.setStatsAbs(parsedMember.getStats());
                    break;
                case BASKETBALL:

                    break;
                default:
                    break;
            }
        }
    }

}