package com.aclastudios.spaceconquest.PlayGameService;

import com.aclastudios.spaceconquest.Screens.PlayScreen;

//Interface to link android and core
public interface PlayServices {
    public boolean getSignedInGPGS();

    public void loginGPGS();

    public void logoutGPGS();

    public void submitScoreGPGS(int score);

    public void unlockAchievementGPGS(String achievementId);

    public void getLeaderboardGPGS();

    public void getAchievementsGPGS();

    public void startQuickGame(int num);

    public void seeInvitations();

    public void sendInvitations();

    public void leaveRoom();

    public void BroadcastMessage(String message);

    public void BroadcastUnreliableMessage(String message);

    public void MessagetoServer(String message);

    public void MessagetoParticipant(int id, String message);

    public void setScreen(PlayScreen screen);

    public boolean checkhost(int serverID);

}
