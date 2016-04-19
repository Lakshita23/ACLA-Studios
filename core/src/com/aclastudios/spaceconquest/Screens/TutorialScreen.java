package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.Helper.AssetLoader;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Lakshita on 4/19/2016.
 */
public class TutorialScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private GameScreenManager gsm;
    private SpaceConquest game;

    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;

    private SpriteBatch batch;
    private Texture background;
    private Sprite sprite;

    private ImageButton.ImageButtonStyle imgstylenext, imgstyleprevious;
    private ImageButton next;
    private ImageButton previous;

    private int count;
    public TutorialScreen(SpaceConquest game, GameScreenManager gsm){
        this.gsm = gsm;
        this.game = game;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);

        BUTTON_WIDTH = 25;
        BUTTON_HEIGHT = 20;
        count = 0;

        imgstylenext = new ImageButton.ImageButtonStyle();
        imgstylenext.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/next-notpressed.png")));
        imgstylenext.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/next-pressed.png")));
        imgstyleprevious = new ImageButton.ImageButtonStyle();
        imgstyleprevious.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/back-notpressed.png")));
        imgstyleprevious.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/back-pressed.png")));

        next = new ImageButton(imgstylenext);
        previous = new ImageButton(imgstyleprevious);
        show();
    }
    @Override
    public void show() {

        batch = new SpriteBatch();
        background = new Texture(AssetLoader.images[0]);

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        next.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        next.setPosition(370, 0);
        stage.addActor(next);

        previous.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        previous.setPosition(5, 0);
        stage.addActor(previous);

        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (count<5) {
                    count++;
                    background = new Texture(AssetLoader.images[count]);
                    background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    sprite = new Sprite(background);
                    sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }
                else{
                    gsm.set(new MenuScreen(game, gsm));
                }
            }
        });

        previous.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (count>0) {
                    count--;
                    background = new Texture(AssetLoader.images[count]);
                    background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    sprite = new Sprite(background);
                    sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }
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
