package com.aclastudios.spaceconquest.Scenes;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


// This class stores data relating to resources, time, and score and displays it at the top of the playscreen
public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport; // With the new viewport, when game world moves the hud stays the same

    // Resource values
    private static Integer oilScore;
    private static Integer gunpowderScore;
    private static Integer ironScore;
    private static Integer teamKnapsack;
    // Gadget values
    private static Integer ammunition;
    private static float jetpackTime;
    // Personal kills value
    private static Integer kills;
    //Score Values
    private static Integer RedScore;
    private static Integer BlueScore;
    // Time values
    private Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    private float timeCount;

    //Labels to display values
    private Label GameLabel;
    private Label BlueLabel;
    private Label resourcesLabel;
    private Label RedLabel;
    private Label time;
    private Label countdownLabel;
    private static Label BlueScoreLabel;
    private static Label RedScoreLabel;

    //Screen
    private PlayScreen screen;
    //others
    private Integer width;
    float smallScale = (float) 0.75;
    float largeScale = (float) 1.25;
    private boolean backuphud = false;

    public Hud(SpriteBatch sb,PlayScreen screen){
        worldTimer = 300;
        timeCount = 0;
        RedScore = 0;
        BlueScore = 0;
        teamKnapsack = 0;
        oilScore = 0;
        gunpowderScore = 0;
        ironScore = 0;
        ammunition =0;
        jetpackTime=0;
        width = 30;
        kills = 0;
        this.screen = screen;

        //set camera view
        viewport = new FitViewport(SpaceConquest.V_WIDTH,SpaceConquest.V_HEIGHT,new OrthographicCamera());

        //Initialise the stage and table to organise the display of labels
        stage = new Stage(viewport, sb);
        Table table = new Table(); //To organise the label
        table.top(); //Top-Align table
        table.setBounds(0, SpaceConquest.V_HEIGHT * (float) 3 / 4, SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT / 4);
        //table.setFillParent(true); //Table is the size of the stage

        // backup HUD
        if (backuphud){
            countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));
            GameLabel = new Label("SPACE CONQUEST", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));
            RedLabel = new Label("RED", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));
            RedScoreLabel = new Label(String.format("%06d", RedScore), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));
            BlueLabel = new Label("BLUE", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));
            BlueScoreLabel = new Label(String.format("%06d", BlueScore), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));

            BlueLabel.setFontScale(smallScale);
            RedLabel.setFontScale(smallScale);
            GameLabel.setFontScale(smallScale);
            countdownLabel.setFontScale(smallScale);
            RedScoreLabel.setFontScale(smallScale);
            BlueScoreLabel.setFontScale(smallScale);

            table.add(RedLabel).expandX().padTop(10);
            table.add(GameLabel).expandX().padTop(10);
            table.add(BlueLabel).expandX().padTop(10);
            table.row(); //new row
            table.add(RedScoreLabel).expandX();
            table.add(countdownLabel).expandX();
            table.add(BlueScoreLabel).expandX();
        } else {
            // Personal Knapsack
            resourcesLabel = new Label(String.format("oil: %2d gp: %2d iron: %2d\nAmmo: %03d Boost Time: %.1f\nkills: %2d",
                    oilScore, gunpowderScore, ironScore, ammunition, jetpackTime, kills), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));

            // Team Scores
            BlueScoreLabel = new Label(String.format("%03d | %03d", BlueScore, RedScore), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));

            // Global data
            time  =new Label(String.format("time: %3d \nKnapsack: %3d/10", worldTimer, teamKnapsack), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"), false), Color.WHITE));

            // scaling of font size
            resourcesLabel.setFontScale(smallScale / 2);
            BlueScoreLabel.setFontScale(largeScale);
            time.setFontScale(smallScale/2);

            // adding labels to table
            table.add(resourcesLabel).width(width / 3).left().pad(0,10,0,60).expandX();
            table.add(BlueScoreLabel).expandX();
            table.add(time).width(width).right().padRight(50).expandX();

        }
        //add table to stage
        stage.addActor(table);
    }
    public void update(float dt,int ammo,float jpTime){
        ammunition=ammo;
        jetpackTime = jpTime;
        timeCount += dt;

        //if the client is the server-client, it performs time reduction. Game time is controlled by the server
        if (screen.getUserID()==screen.getServerID()) {
            //If WorldTimer more than 0, reduce WorldTimer every second
            if (timeCount >= 1) { //check if dt adds up to one, then reduce one timer
                if (worldTimer > 0) {
                    worldTimer--;
                } else {
                    timeUp = true;
                }
                if (backuphud) {
                    countdownLabel.setText(String.format("%03d", worldTimer));
                } else {
                    time.setText(String.format("time: %3d \nKnapsack: %3d/10", worldTimer, teamKnapsack));
                    resourcesLabel.setText(String.format("oil: %2d gp: %2d iron: %2d\nAmmo: %03d Jet Pack: %.1f\nKills: %3d", oilScore, gunpowderScore, ironScore, ammunition, jetpackTime,kills));
                }
                timeCount = 0; //reset counter once WorldTimer has been reduced
            }
        }
        else {
            if(worldTimer<=0){
                timeUp = true;
            }
            if (backuphud) {
                countdownLabel.setText(String.format("%03d", worldTimer));
            } else {
                time.setText(String.format("time: %3d \nKnapsack: %3d/10", worldTimer, teamKnapsack));
                resourcesLabel.setText(String.format("oil: %2d gp: %2d iron: %2d\nAmmo: %03d Jet Pack: %.1f\nKills: %3d", oilScore, gunpowderScore, ironScore, ammunition, jetpackTime,kills));
            }
        }
    }

    //Set the BlueScore and RedScore
    public static void updatescore(int redScore, int blueScore){
        BlueScore=blueScore;
        RedScore=redScore;
        BlueScoreLabel.setText(String.format("%03d | %03d", RedScore, BlueScore));
    }

    //set resources values
    public static void updateknapscore(int inp, int oil, int iron, int gunpowder) {
        teamKnapsack = inp;
        oilScore =oil;
        ironScore = iron;
        gunpowderScore = gunpowder;
    }

    //set gadget values
    public static void updateGadget(int ammo,float jpTime){
        ammunition = ammo;
        jetpackTime = jpTime;
    }

    //Check if WorldTime is 0
    public boolean isTimeUp() { return timeUp; }

    //Set WorldTimer. Used by non-server clients when they get the time value from the server-client.
    public void setTime(int time){
        worldTimer=time;
    }

    //get and add functions
    public int getTime(){
        return worldTimer;
    }
    public void addkill(){
        kills++;
    }
    public int getkills(){
        return kills;
    }
    public int getRedScore(){
        return RedScore;
    }
    public int getBlueScore(){
        return BlueScore;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
