package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.Helper.AssetLoader;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class GameOver implements Screen {
    private Viewport viewport;
    private Stage stage;
    private GameScreenManager gsm;
    private SpaceConquest game;

    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;

    private SpriteBatch batch;
    private Texture background;
    private Sprite sprite;

    private TextButtonStyle style;
    private TextButton mainBtn;
    private TextButton leaderBtn;

    private Label.LabelStyle style1;
    private Label winLosetext;
    private Label playerScore;
    private Label myScore;
    private Label oppScore;


    public GameOver(SpaceConquest game, GameScreenManager gsm, int len, int myId, int redScore, int blueScore, int mykillScore){
        this.gsm = gsm;
        this.game = game;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);

        BUTTON_WIDTH = 120;
        BUTTON_HEIGHT = 20;

        style1 = new Label.LabelStyle();
        style1.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt.fnt"));
        style1.font.setColor(Color.BLUE);
        style1.font.getData().setScale(0.3f, 0.3f);

        style = new TextButtonStyle();  //can customize
        style.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt.fnt"));
        style.font.setColor(Color.BLUE);
        style.font.getData().setScale(0.2f, 0.2f);
        style.up= new TextureRegionDrawable(new TextureRegion(new Texture("basic/button_up.png")));
        style.down= new TextureRegionDrawable(new TextureRegion(new Texture("basic/button_down.png")));

        String myTeam;
        String winLose = "DRAW!";
        if (myId<len/2)
            myTeam = "RED";
        else
            myTeam = "BLUE";
        if (myTeam.equals("RED")){
            if (redScore>blueScore)
                winLose = "Congrats Red Team, you WON!!!";
            else if (redScore<blueScore)
                winLose = "Sorry, you LOST";
            else
                winLose = "DRAW, Nice match";
        }
        if (myTeam.equals("BLUE")){
            if (redScore<blueScore)
                winLose = "Congrats Red Team, you WON!!!";
            else if (redScore>blueScore)
                winLose = "Sorry, you LOST";
            else
                winLose = "DRAW, Nice match";
        }

        winLosetext = new Label(winLose, style1);
        playerScore = new Label("MY KILL SCORE: "+mykillScore, style1);
        if (myTeam.equals("RED")){
            myScore = new Label("MY TEAM: RED: "+redScore, style1);
            oppScore = new Label("ENEMY TEAM: BLUE: "+blueScore, style1);
        }
        else{
            myScore = new Label("MY TEAM: BLUE: "+blueScore, style1);
            oppScore = new Label("ENEMY TEAM: RED: "+redScore, style1);
        }


        mainBtn = new TextButton("Main Menu", style);
        leaderBtn = new TextButton("Leader Board", style);

        System.out.println("constructor");
        show();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture("gameover.jpg");

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        //
        System.out.println("add actors");
        winLosetext.setSize(this.BUTTON_WIDTH * 2, this.BUTTON_HEIGHT);
        winLosetext.setPosition(SpaceConquest.V_WIDTH/2-this.BUTTON_WIDTH/2-10, 150);
        stage.addActor(winLosetext);
        playerScore.setSize(this.BUTTON_WIDTH * 2, this.BUTTON_HEIGHT);
        playerScore.setPosition(SpaceConquest.V_WIDTH/2-this.BUTTON_WIDTH/2-10, 120);
        stage.addActor(playerScore);
        myScore.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        myScore.setPosition(SpaceConquest.V_WIDTH/2-this.BUTTON_WIDTH/2-10, 90);
        stage.addActor(myScore);
        oppScore.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        oppScore.setPosition(SpaceConquest.V_WIDTH/2-this.BUTTON_WIDTH/2-10, 60);
        stage.addActor(oppScore);
//



        mainBtn.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        mainBtn.setPosition(60, 20);
        stage.addActor(mainBtn);

        leaderBtn.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        leaderBtn.setPosition(SpaceConquest.V_WIDTH-this.BUTTON_WIDTH-60, 20);
        stage.addActor(leaderBtn);

        mainBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_MENU;
                gsm.set(new MenuScreen(game, gsm));
            }
        });

        leaderBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_LEADER;
                gsm.set(new LeadersBoardScreen(game, gsm));
            }
        });


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.end();

        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
