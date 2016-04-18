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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class MenuScreen implements Screen {
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
    private TextButton login;
    private TextButton instructions;

    private Image mute;
    private Image unmute;

    public MenuScreen(SpaceConquest game, GameScreenManager gsm){
        this.gsm = gsm;
        this.game = game;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);

        BUTTON_WIDTH = 120;
        BUTTON_HEIGHT = 20;

        style = new TextButtonStyle();  //can customize
        style.font = new BitmapFont(Gdx.files.internal("fonts/fontin.fnt"));
        style.font.setColor(Color.BLUE);
        style.font.getData().setScale(0.2f, 0.2f);
        style.up= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-notPressed2.png")));
        style.down= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-Pressed2.png")));

//        style.unpressedOffsetX = 5f;
//        style.pressedOffsetX = style.unpressedOffsetX + 1f;
//        style.pressedOffsetY = -1f;
        play = new TextButton("START GAME",style);
        login = new TextButton("LEADER BOARD", style);
        instructions = new TextButton("HOW TO PLAY", style);

        mute = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("button/sound-on.png"))));
        unmute = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("button/sound-off.png"))));

        System.out.println("constructor");
        show();
    }

    @Override
    public void show() {
        // The elements are displayed in the order you add them.
        // The first appear on top, the last at the bottom.
        if (AssetLoader.gameMusic != null) {
            AssetLoader.gameMusic.stop();
            AssetLoader.disposeSFX();
        }
        //AssetLoader.menuMusic.play();

        batch = new SpriteBatch();
        background = new Texture("screens/Screen2.png");

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        play.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        play.setPosition(300, 140);
        stage.addActor(play);

        login.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        login.setPosition(300, 100);
        stage.addActor(login);

        instructions.setSize(this.BUTTON_WIDTH / 3 * 2, this.BUTTON_HEIGHT);
        instructions.setPosition(300, 60);
        stage.addActor(instructions);

        mute.setScale(0.4f, 0.4f);
        mute.setPosition(3, 195);
        unmute.setScale(0.4f,0.4f);
        unmute.setPosition(3, 195);
        if (AssetLoader.VOLUME == 1) {
            stage.addActor(mute);
        } else {
            stage.addActor(unmute);
        }


        System.out.println("play");
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // AssetLoader.clickSound.play(AssetLoader.VOLUME);
                // Host multiplayer game
                if (game.playServices.getSignedInGPGS()) {
                    game.playServices.startQuickGame();
                    game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_WAIT;
                    gsm.set(new WaitScreen(game, gsm));
                } else {
                    game.playServices.loginGPGS();
                }
               // gsm.set(new PlayScreen(game, gsm));
            }
        });

        login.addListener(new ClickListener() {
              @Override
            public void clicked(InputEvent event, float x, float y) {
                  game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_LEADER;
              gsm.set(new LeadersBoardScreen(game, gsm));
            }
        });


        instructions.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetLoader.clickSound.play(AssetLoader.VOLUME);
                // TODO Set to tutorial screen
                //  gsm.set(new StoryScreen(game, gsm));
            }
        });

        mute.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetLoader.muteSFX();
                mute.remove();
                stage.addActor(unmute);
            }

        });

        unmute.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AssetLoader.unmuteSFX();
                AssetLoader.clickSound.play(AssetLoader.VOLUME);
                unmute.remove();
                stage.addActor(mute);
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
