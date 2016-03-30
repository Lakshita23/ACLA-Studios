package com.aclastudios.spaceconquest.SupportThreads;


import com.aclastudios.spaceconquest.SpaceConquest;

import java.io.IOException;

public class ClientThread extends Thread {
    private SpaceConquest game;
    public ClientThread(SpaceConquest game){
        this.game = game;
    }
    public void run() {
        String line=null;
        while (true){
            try {
                if (game.playServices.inputBuffer()!=null && (line=game.playServices.inputBuffer().readLine())!=null) {
                    System.out.println("Reading input: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
