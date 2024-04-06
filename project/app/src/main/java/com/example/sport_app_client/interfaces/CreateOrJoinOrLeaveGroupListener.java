package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.Member;

public interface CreateOrJoinOrLeaveGroupListener {
    public void onGroupRemoved(long memberID);

    public void onGroupCreated(Member<?> member);

    public void onGroupJoined(Member<?> member, Group<?,?> group);
}
