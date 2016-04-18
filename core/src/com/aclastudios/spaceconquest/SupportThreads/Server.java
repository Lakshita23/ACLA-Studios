package com.aclastudios.spaceconquest.SupportThreads;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.SpaceConquest;

public class Server{ //Not a thread for now
    private int RedTeamScore = 0;
    private int BlueTeamScore = 0;
    private SpaceConquest game;
    public Server(SpaceConquest game){
        this.game = game;
    }
    public void addBlueScore(int score){
        BlueTeamScore+=score;
        game.playServices.BroadcastMessage("UpdateScoreAll:" + RedTeamScore + ":" + BlueTeamScore);
        Hud.updatescore(RedTeamScore,BlueTeamScore);
    }
    public void addRedScore(int score){
        RedTeamScore+=score;
        game.playServices.BroadcastMessage("UpdateScoreAll:" + RedTeamScore + ":" + BlueTeamScore);
        Hud.updatescore(RedTeamScore, BlueTeamScore);
    }
}
