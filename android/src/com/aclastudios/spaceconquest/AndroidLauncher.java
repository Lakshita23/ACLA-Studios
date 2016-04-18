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


	public GameHelper gameHelper;
	public GoogleApiClient mGoogleApiClient;
	private GPSListeners mGooglePlayListeners;
	public MultiplayerSessionInfo MultiplayerSession;

	private PlayScreen screen;



	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Set log level to debug to let all gdx messages through
		this.setLogLevel(LOG_DEBUG);

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
//		gameHelper.onStop();
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
			Gdx.app.log("MainActivity", "Log in failed: " + ex.getMessage() + ".");

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
				Gdx.app.log("MainActivity", "Log out failed: " + ex.getMessage() + ".");

			}
		}

	}

	@Override
	public void submitScoreGPGS(int score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), "CgkI8bDhycAZEAIQAQ", score);

	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
		Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), "CgkI8bDhycAZEAIQAQ"), RC_LEADER);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void getAchievementsGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), RC_LEADER);
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
		// quick-start a game with 1 randomly selected opponent
		if (gameHelper.isSignedIn()) {
			//Set multiplayer flag to be true so that game screen will choose to create multiplayer world instead
			final int MIN_OPPONENTS = num, MAX_OPPONENTS = num;
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
	public void seeInvitations(){
		if (gameHelper.isSignedIn()) {
			MultiplayerSession.isServer=false;
			Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
			startActivityForResult(intent, RC_INVITATION_INBOX);
			// show list of pending invitations

		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}


	@Override
	public void sendInvitations(){
		if (gameHelper.isSignedIn()) {
			//Assign device as server and setup a socket to accept connections
			MultiplayerSession.isServer=true;
			// show list of inevitable players
			//Choose from between 1 to 3 other opponents (APIclient,minOpponents, maxOpponents, boolean Automatch)
			Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 5);
			startActivityForResult(intent, RC_SELECT_PLAYERS);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	// Leave the room.
	public void leaveRoom() {
		Log.d(TAG, "Leaving room.");
		if (MultiplayerSession.mRoomId != null) {
			Games.RealTimeMultiplayer.leave(this.mGoogleApiClient, this.mGooglePlayListeners, MultiplayerSession.mRoomId);
			MultiplayerSession.mRoomId=null;
			MultiplayerSession.isServer=false;
			MultiplayerSession.mName=null;
			MultiplayerSession.mParticipants=null;
			MultiplayerSession.mId=null;
		} else {
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
		}
	}

	// Handle the result of the "Select players UI" we launched when the user clicked the
	// "Invite friends" button. We react by creating a room with those players.
	private void handleSelectPlayersResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** select players UI cancelled, " + response);
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
			return;
		}

		Log.d(TAG, "Select players UI succeeded.");

		// get the invitee list
		final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
		Log.d(TAG, "Invitee count: " + invitees.size());

		// get the automatch criteria
		Bundle autoMatchCriteria = null;
		int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
			autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
					minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
		}

		// create the room
		Log.d(TAG, "Creating room...");
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
		rtmConfigBuilder.addPlayersToInvite(invitees);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(mGooglePlayListeners);
		if (autoMatchCriteria != null) {
			rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		}

		Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
		Log.d(TAG, "Room created, waiting for it to be ready...");
	}


	// Handle the result of the invitation inbox UI, where the player can pick an invitation
	// to accept. We react by accepting the selected invitation, if any.
	private void handleInvitationInboxResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
			MultiplayerSession.mState= MultiplayerSession.ROOM_MENU;
			return;
		}

		Log.d(TAG, "Invitation inbox UI succeeded.");
		Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

		// accept invitation
		acceptInviteToRoom(inv.getInvitationId());
	}

	// Accept the given invitation.
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Log.d(TAG, "Accepting invitation: " + invId);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(mGooglePlayListeners);
		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}

	//Sending messages
	public void BroadcastMessage(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		for (Object o : MultiplayerSession.mParticipants) {
			Participant p = (Participant) o;
//			if (!p.getParticipantId().equals(MultiplayerSession.mId)) {
				Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
						MultiplayerSession.mRoomId, p.getParticipantId());
//			}
		}
	}
	public void BroadcastUnreliableMessage(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
//		byte[] bytes = message.getBytes();
		Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(mGoogleApiClient, bytes,
				MultiplayerSession.mRoomId);

	}
	public void MessagetoServer(String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		Participant p = (Participant) MultiplayerSession.mParticipants.get(0);
		Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
				MultiplayerSession.mRoomId, p.getParticipantId());
	}
	public void MessagetoParticipant(int id, String message){
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		Participant p = (Participant) MultiplayerSession.mParticipants.get(id);
		Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, bytes,
				MultiplayerSession.mRoomId, p.getParticipantId());
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] buf = rtm.getMessageData();
		if (screen!=null) {
			screen.MessageListener(buf);
		}

	}
	@Override
	public void setScreen(PlayScreen screen) {
		this.screen = screen;
	}

	@Override
	public boolean checkhost() {
		Participant p = (Participant) MultiplayerSession.mParticipants.get(0);
		if (!p.isConnectedToRoom()){
			return false;
		} else {
			return true;
		}
	}
}
