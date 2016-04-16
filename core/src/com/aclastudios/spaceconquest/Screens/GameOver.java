package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameOver implements Screen {
    private Viewport viewport;
    private Stage stage;
    private GameScreenManager gsm;
    private Game game;

    public GameOver(Game game, GameScreenManager gsm){
        this.game = game;
        this.gsm = gsm;
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((SpaceConquest) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        //Table1
        


        //Table2
        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        Label playAgainLabel = new Label("Click to Play Again", font);
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            //dispose();
            gsm.set(new MenuScreen((SpaceConquest) game, gsm));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
