package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.Helper.AssetLoader;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

        BUTTON_WIDTH = 120;
        BUTTON_HEIGHT = 20;

        style = new TextButtonStyle();  //can customize
        style.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt"));
        style.font.setColor(Color.BLUE);
        style.font.getData().setScale(0.2f, 0.2f);
        style.up= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-notPressed.png")));
        style.down= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-Pressed.png")));

        // adding the music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic/!in-game.mp3"));
        menuMusic.setVolume(1f);
        menuMusic.setLooping(false);
        menuMusic.play();
        play = new TextButton("START GAME",style);
        leaderboard = new TextButton("LEADER BOARD", style);
        instructions = new TextButton("HOW TO PLAY", style);

        System.out.println("constructor");
        show();
    }

    @Override
    public void show() {
        // The elements are displayed in the order you add them.
        // The first appear on top, the last at the bottom.
//        if (AssetLoader.gameMusic != null) {
//            AssetLoader.gameMusic.stop();
//            AssetLoader.disposeSFX();
//        }
        //AssetLoader.menuMusic.play();

        batch = new SpriteBatch();
        background = new Texture("screens/Screen.png");

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        play.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        play.setPosition(30, 30);
        stage.addActor(play);

        leaderboard.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        leaderboard.setPosition(165, 30);
        stage.addActor(leaderboard);

        instructions.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        instructions.setPosition(292, 30);
        stage.addActor(instructions);

        System.out.println("play");
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new playersSelectScreen(game, gsm));
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
               // AssetLoader.clickSound.play(AssetLoader.VOLUME);
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
