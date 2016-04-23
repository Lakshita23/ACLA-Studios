package com.aclastudios.spaceconquest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/* Splash Screen will show when app is starting. It will show the Game company Logo - ACLA Studios on startup for 2 seconds.*/
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create a new Thread
        Thread th = new Thread(new Runnable() {
            //Run thread that will start intent after 2 seconds
            @Override
            public void run() {
                try {
                    //Thread sleeps for 2 secs
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Start new intent to redirect to Android Launcher to start the game.
                finally {
                    Intent i = new Intent(SplashActivity.this, AndroidLauncher.class);
                    startActivity(i);
                    //Remove activity
                    finish();
                }
            }
        });
        //Starting thread
        th.start();

    }
}
