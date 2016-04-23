package com.aclastudios.spaceconquest.Scores;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.SpaceConquest;

// Only the client assigned as the server(host) will have access to this function
// This class is unique to the server-client as this is the only place where scores are updated.
// (Ensure consistent score throughout all the players)
public class Server{
    private int RedTeamScore = 0;
    private int BlueTeamScore = 0;
    private SpaceConquest game;

    public Server(SpaceConquest game){
        this.game = game;
    }

    //Add blue score and broadcast to other "non-server" clients
    public void addBlueScore(int score){
        BlueTeamScore+=score;
        game.playServices.BroadcastMessage("UpdateScoreAll:" + RedTeamScore + ":" + BlueTeamScore);
        Hud.updatescore(RedTeamScore,BlueTeamScore);
    }

    //Add red score and broadcast to other "non-server" clients
    public void addRedScore(int score){
        RedTeamScore+=score;
        game.playServices.BroadcastMessage("UpdateScoreAll:" + RedTeamScore + ":" + BlueTeamScore);
        Hud.updatescore(RedTeamScore, BlueTeamScore);
    }

    //setter method
    public void setRnBteamScore(int red, int blue){
        RedTeamScore = red;
        BlueTeamScore = blue;
    }
}
