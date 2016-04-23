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
Short tutorial of game to help players understand the concept.
 Tutorial shows game map, resource collection area, weapons and abilities of player via image swipes.
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

        //Initialise next and prev button image styles
        imgstylenext = AssetLoader.imgStyleNext;
        imgstyleprevious = AssetLoader.imgStylePrev;

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

        //Add imageButtons to Stage
        next.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        next.setPosition(370, 0);
        stage.addActor(next);

        previous.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        previous.setPosition(5, 0);
        stage.addActor(previous);

        //Creating button click actions
        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (count<5) {  //show upto 6 images explaining the game
                    //count keeps record of image number
                    count++;
                    //update background image based on button click
                    background = new Texture(AssetLoader.images[count]);
                    background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    sprite = new Sprite(background);
                    sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }
                else{   //goto Menu Screen once player has viewed all 6 images
                    gsm.set(new MenuScreen(game, gsm));
                }
            }
        });

        previous.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (count>0) {  //disable back button if viewing first image
                    count--;
                    //update background to previous image
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
