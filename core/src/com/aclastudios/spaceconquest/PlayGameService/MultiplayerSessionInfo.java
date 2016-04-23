package com.aclastudios.spaceconquest.PlayGameService;

import java.util.ArrayList;


// Storage class to store all information needed for google play services

public class MultiplayerSessionInfo {
	
	public String mId;
	public int mId_num;
	public String mIncomingInvitationId;
	public String mRoomId;
	public ArrayList mParticipants;
	public ArrayList<String> mParticipantsId;
	public String mName;
	public int mState=1000;

	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_MENU=1003;
	public final int ROOM_LEADER=1004;
	
	public MultiplayerSessionInfo(){
	}

	
	public void endSession(){
		mId=null;
		mIncomingInvitationId=null;
		mRoomId=null;
		mParticipants=null;
		mState=ROOM_MENU;
		mName = null;
	}


}
