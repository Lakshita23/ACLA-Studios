package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class WaitScreen implements Screen {

	private SpaceConquest game;
	private GameScreenManager gsm;

	//Background
	private SpriteBatch batch;
	private Texture background;
	private Sprite sprite;

	public WaitScreen(SpaceConquest game, GameScreenManager gsm) {
		this.game = game;
		this.gsm = gsm;
		show();
	}

	@Override
	public void show() {
		//initialising of background imae
		batch = new SpriteBatch();
		background = new Texture("darkscreen.png");
		background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite = new Sprite(background);
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//render the background image
		batch.begin();
		sprite.draw(batch);
		batch.end();

		//Check state of the session
		if ((game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_PLAY)) {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
			//stop music of the menu screen in preparation to run new music in playScreen
			MenuScreen.menuMusic.stop();
			gsm.set(new PlayScreen(game, gsm));

		} else if (game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_MENU) {
			//go back to menu screen
			game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_NULL;
			gsm.set(new MenuScreen(game, gsm));
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

}
