package com.aclastudios.spaceconquest.Scenes;

import com.aclastudios.spaceconquest.SpaceConquest;
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

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport; //With the new viewport, when game world moves the hud stays the same

    private Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    private float timeCount;
    private static Integer score;

    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label worldLabel;
    private Label levelLabel;
    private Label KnapsackLabel;

    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(SpaceConquest.V_WIDTH,SpaceConquest.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table(); //To organise the label
        table.top(); //Top-Align table
        table.setBounds(0, SpaceConquest.V_HEIGHT * (float) 3 / 4, SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT / 4);
        //table.setFillParent(true); //Table is the size of the stage

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("Dooms Planet", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("SPACE CONQUEST", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        KnapsackLabel = new Label("KNAPSACK", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(KnapsackLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row(); //new row
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }
    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }
    public static void addScore(int value){
        score+=value;
        scoreLabel.setText(String.format("%06d", score));
    }
    public static int getScore(){
        return score;
    }


    @Override
    public void dispose() {
        stage.dispose();
    }

    public boolean isTimeUp() { return timeUp; }
}
