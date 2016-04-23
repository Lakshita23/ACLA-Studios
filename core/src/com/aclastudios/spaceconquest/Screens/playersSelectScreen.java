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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class playersSelectScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private GameScreenManager gsm;
    private SpaceConquest game;

    //Background
    private SpriteBatch batch;
    private Texture background;
    private Sprite sprite;

    //Buttons and Style
    private TextButtonStyle style;
    private TextButton play1;
    private TextButton play2;
    private TextButton play3;
    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;


    public playersSelectScreen(SpaceConquest game, GameScreenManager gsm){
        this.gsm = gsm;
        this.game = game;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());

        //Create stage to store the buttons
        stage = new Stage(viewport, (game).batch);

        BUTTON_WIDTH = 100;
        BUTTON_HEIGHT = 150;

        //Create font for the button
        style = new TextButtonStyle();
        style.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt"));
        style.font.setColor(Color.BLUE);
        style.font.getData().setScale(0.8f, 0.8f);
        style.up= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-notPressed.png")));
        style.down= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-Pressed.png")));

        //Button Text
        play1 = new TextButton("1 v 1", style);
        play2 = new TextButton("2 v 2", style);
        play3 = new TextButton("3 v 3", style);

        show();
    }

    @Override
    public void show() {

        //Create background image
        batch = new SpriteBatch();
        background = new Texture("darkscreen.png");

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Set Button to stage
        play1.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        play1.setPosition(25, 25);
        stage.addActor(play1);

        play2.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        play2.setPosition(150, 25);
        stage.addActor(play2);

        play3.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        play3.setPosition(275, 25);
        stage.addActor(play3);

        //Add button listener
        play1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.playServices.getSignedInGPGS()) {
                    //StartQuickGame has a build in UI which displays the wait room
                    game.playServices.startQuickGame(1); // Start a 1v1 game, 1 other player
                    game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_WAIT;
                    //When all players are in the room, set screen to WaitScreen while waiting for the GPGS to start the room session
                    gsm.set(new WaitScreen(game, gsm));
                } else {
                    game.playServices.loginGPGS();
                }
            }
        });

        play2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.playServices.getSignedInGPGS()) {
                    //StartQuickGame has a build in UI which displays the wait room
                    game.playServices.startQuickGame(3); // Start a 2v2 game, 3 other players
                    game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_WAIT;
                    //When all players are in the room, set screen to WaitScreen while waiting for the GPGS to start the room session

                    gsm.set(new WaitScreen(game, gsm));
                } else {
                    game.playServices.loginGPGS();
                }
            }
        });

        play3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.playServices.getSignedInGPGS()) {
                    //StartQuickGame has a build in UI which displays the wait room
                    game.playServices.startQuickGame(5); // Start a 3v3 game, 5 other players
                    game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_WAIT;
                    //When all players are in the room, set screen to WaitScreen while waiting for the GPGS to start the room session
                    gsm.set(new WaitScreen(game, gsm));
                } else {
                    game.playServices.loginGPGS();
                }
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render Background
        batch.begin();
        sprite.draw(batch);
        batch.end();

        //Render Buttons
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
