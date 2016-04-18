package com.aclastudios.spaceconquest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Lakshita on 4/4/2016.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(this, AndroidLauncher.class);
//        startActivity(intent);
//        finish();


        Thread th = new Thread(new Runnable() {            /*create a new thread */
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent i = new Intent(SplashActivity.this, AndroidLauncher.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        th.start(); // start the thread

    }
}
