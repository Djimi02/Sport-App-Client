package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface SelectMemberToJoinGroupListener {
    /**
     * This method should call ConfirmActionDialog.showDialog() and pass as arguments the
     * currently active activity, the text of the dialog and the required action. The required
     * action is API.joinGroupAsExistingMember();. On successful request:
     * 1. the specified member's user attribute should be set the current user;
     * 2. set the associated member to the specified member
     * 3. call MyGlobals.createOrJoinOrLeaveGroupListener.onGroupJoined() and initViews();
     * Also GlobalMethods.showPGAndBlockUI(); and GlobalMethods.hidePGAndEnableUi(); should be
     * called before the request and on response/fail respectively.
     * @param member - specified member
     */
    public void onMemberSelected(Member member);
}