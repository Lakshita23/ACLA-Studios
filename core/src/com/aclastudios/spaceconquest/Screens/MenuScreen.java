package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.Helper.AssetLoader;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class MenuScreen implements Screen {
    public static Music menuMusic;
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
    private TextButton play;
    private TextButton leaderboard;
    private TextButton instructions;

    public MenuScreen(SpaceConquest game, GameScreenManager gsm){
        this.gsm = gsm;
        this.game = game;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);
        //Initialising assets
        AssetLoader.loadStyles();
        AssetLoader.loadbackgrounds();

        BUTTON_WIDTH = 120;
        BUTTON_HEIGHT = 20;

        //Button style
        style = AssetLoader.style;

        if (GameOver.gameoverMusic!=null){
            GameOver.gameoverMusic.stop();
            GameOver.gameoverMusic.dispose();
        }

        //Adding the music
        if (menuMusic==null) {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic/!in-game.mp3"));
        }
        if (!menuMusic.isPlaying()) {
            menuMusic.setVolume(1f);
            menuMusic.setLooping(false);
            menuMusic.play();
        }

        //Initialising Buttons- Text and Style
        play = new TextButton("START GAME",style);
        leaderboard = new TextButton("LEADER BOARD", style);
        instructions = new TextButton("HOW TO PLAY", style);

        show();
    }

    @Override
    public void show() {
        // The elements are displayed in the order you add them.
        // The first appear on top, the last at the bottom.

        //Creating new sprite for menu screen
        batch = new SpriteBatch();
        background = AssetLoader.background;
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Adding buttons to stage
        play.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        play.setPosition(30, 30);
        stage.addActor(play);

        leaderboard.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        leaderboard.setPosition(165, 30);
        stage.addActor(leaderboard);

        instructions.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        instructions.setPosition(292, 30);
        stage.addActor(instructions);

        //Creating button click actions
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new ModeSelectScreen(game, gsm));
            }
        });
        leaderboard.addListener(new ClickListener() {
              @Override
            public void clicked(InputEvent event, float x, float y) {
                  game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_LEADER;
                  gsm.set(new LeadersBoardScreen(game, gsm));
            }
        });
        instructions.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetLoader.loadTutorialScreen();
                gsm.set(new TutorialScreen(game, gsm));
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
