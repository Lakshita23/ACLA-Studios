package com.aclastudios.spaceconquest;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.aclastudios.spaceconquest.PlayGameService.MultiplayerSessionInfo;
import com.aclastudios.spaceconquest.PlayGameService.PlayServices;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;



public class AndroidLauncher extends AndroidApplication implements GameHelperListener, PlayServices,RealTimeMessageReceivedListener {

	private final String TAG = "SpaceConquest Andriod Launcher";
	// Request codes for the UIs that we show with startActivityForResult:
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;
	final static int RC_LEADER = 10003;
	private static final int RC_SIGN_IN = 9001;
	private String leaderboardID = "CgkI8bDhycAZEAIQAQ";

	//GooglePlayFunctions
	public GameHelper gameHelper;
	public GoogleApiClient mGoogleApiClient;
	private GPSListeners mGooglePlayListeners;
	//Core Functions
	public MultiplayerSessionInfo MultiplayerSession;
	private PlayScreen screen;



	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Initialize gameHelper
		if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		}
		gameHelper.setMaxAutoSignInAttempts(0);
		gameHelper.setup(this);

		//Get and store api client for multi-player services
		mGoogleApiClient=gameHelper.getApiClient();

		//Initalize helper class that stores all additional needed information for multiplayer games
		MultiplayerSession = new MultiplayerSessionInfo();

		//Initialize listener helper class
		if (mGooglePlayListeners == null) {
			mGooglePlayListeners = new GPSListeners(mGoogleApiClient,this);
		}

		//Initialize the core functions
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new SpaceConquest(this, MultiplayerSession), config);

	}

	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);

		switch (requestCode) {
			case RC_SELECT_PLAYERS:
				// we got the result from the "select players" UI -- ready to create the room
				handleSelectPlayersResult(responseCode, intent);
				break;
			case RC_INVITATION_INBOX:
				// we got the result from the "select invitation" UI (invitation inbox). We're
				// ready to accept the selected invitation:
				//NOT USED
				handleInvitationInboxResult(responseCode, intent);
				break;
			case RC_WAITING_ROOM:
				// we got the result from the "waiting room" UI.
				if (responseCode == Activity.RESULT_OK) {
					System.out.println("GPS room returned OK");
					//Change screen to game screen
					MultiplayerSession.mState= MultiplayerSession.ROOM_PLAY;
				} else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
					// player indicated that they want to leave the room
					MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
					leaveRoom();
				} else if (responseCode == Activity.RESULT_CANCELED) {
					MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
					// Dialog was cancelled (user pressed back key, for instance). In our game,
					// this means leaving the room too. In more elaborate games, this could mean
					// something else (like minimizing the waiting room UI).
					leaveRoom();
				}
				break;
			case RC_SIGN_IN:
				gameHelper.onActivityResult(requestCode, responseCode, intent);
				break;
			case RC_LEADER:
				// we got the result from the "leader room" UI.
				if (responseCode == Activity.RESULT_CANCELED){
					MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
				}
		}
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable(){
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}
	}
	@Override
	public void logoutGPGS(){
		if(getSignedInGPGS()){
			try {
				runOnUiThread(new Runnable(){
					public void run() {
						gameHelper.signOut();
					}
				});
			} catch (final Exception ex) {
			}
		}

	}

	@Override
	public void submitScoreGPGS(int score) {
		//Sends the score to the googleplay server for it to be stored
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), leaderboardID, score);
	}

	@Override
	public void getLeaderboardGPGS() {
		//Call GPGS leaderboard function (It has a build in UI)
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), leaderboardID), RC_LEADER);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
	}

	@Override
	public void startQuickGame(int num) {
		// quick-start a game with "num" randomly selected opponent
		if (gameHelper.isSignedIn()) {
			//Set multiplayer flag to be true so that game screen will choose to create multiplayer world instead
			final int MIN_OPPONENTS = num, MAX_OPPONENTS = num;
			//Create the GPGS room
			Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,MAX_OPPONENTS, 0);
			RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
			rtmConfigBuilder.setMessageReceivedListener(this);
			rtmConfigBuilder.setRoomStatusUpdateListener(mGooglePlayListeners);
			rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	// Leave the room.
	public void leaveRoom() {
		if (MultiplayerSession.mRoomId != null) {
			Games.RealTimeMultiplayer.leave(this.mGoogleApiClient, this.mGooglePlayListeners, MultiplayerSession.mRoomId);
			//Reset all the values upon leaving room
			MultiplayerSession.mRoomId=null;
			MultiplayerSession.mName=null;
			MultiplayerSession.mParticipants=null;
			MultiplayerSession.mId=null;
		} else {
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
		}
	}

	//Sends ReliableMessage to all players
	public void BroadcastMessage(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		for (Object o : MultiplayerSession.mParticipants) {
			Participant p = (Participant) o;
			Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
					MultiplayerSession.mRoomId, p.getParticipantId());

		}
	}

	//Sends UnreliableMessage to all players
	public void BroadcastUnreliableMessage(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(mGoogleApiClient, bytes,
				MultiplayerSession.mRoomId);

	}

	//Sends ReliableMessage to the client that is acting as the server
	public void MessagetoServer(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		Participant p = (Participant) MultiplayerSession.mParticipants.get(screen.getServerID());
		Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
				MultiplayerSession.mRoomId, p.getParticipantId());
	}

	//Sends ReliableMessage to a particular client
	public void MessagetoParticipant(int id, String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		Participant p = (Participant) MultiplayerSession.mParticipants.get(id);
		Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
				MultiplayerSession.mRoomId, p.getParticipantId());
	}

	//Link the playScreen to this class so that screen functions can be called
	@Override
	public void setScreen(PlayScreen screen) {
		this.screen = screen;
	}

	//Listens to incoming messages and calls the playScreen.MessageListener function (core),
	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] buf = rtm.getMessageData();
		if (screen!=null) {
			screen.MessageListener(buf);
		}

	}

	//Check if the client is still in the room
	@Override
	public boolean checkhost(int serverID) {
		Participant p = (Participant) MultiplayerSession.mParticipants.get(serverID);
		return p.isConnectedToRoom();
	}


	//***************************Future Implementation (Phase 2) ***********************************


	//Future Implementation (No invitation function yet) Phase 2
	@Override
	public void seeInvitations(){
		if (gameHelper.isSignedIn()) {
			Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
			startActivityForResult(intent, RC_INVITATION_INBOX);
			// show list of pending invitations

		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}
	//Future Implementation (No invitation function) Phase 2
	@Override
	public void sendInvitations(){
		if (gameHelper.isSignedIn()) {
			//Assign device as server and setup a socket to accept connections
			// show list of inevitable players
			//Choose from between 1 to 3 other opponents (APIclient,minOpponents, maxOpponents, boolean Automatch)
			Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 5);
			startActivityForResult(intent, RC_SELECT_PLAYERS);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}
	//Future Implementation (No achievement function) Phase 2
	@Override
	public void unlockAchievementGPGS(String achievementId) {
		Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}
	//Future Implementation (No achievement function) Phase 2
	@Override
	public void getAchievementsGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), RC_LEADER);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	private void handleSelectPlayersResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
			return;
		}
		// get the invitee list
		final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

		// get the automatch criteria
		Bundle autoMatchCriteria = null;
		int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
			autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
					minAutoMatchPlayers, maxAutoMatchPlayers, 0);
		}

		// create the room
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
		rtmConfigBuilder.addPlayersToInvite(invitees);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(mGooglePlayListeners);
		if (autoMatchCriteria != null) {
			rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		}

		Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
	}
	private void handleInvitationInboxResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
			return;
		}
		Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
		// accept invitation
		acceptInviteToRoom(inv.getInvitationId());
	}

	// Accept the given invitation.
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(mGooglePlayListeners);
		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}
}
