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
		batch = new SpriteBatch();
		background = new Texture("waitscreen.jpg");
		background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite = new Sprite(background);
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		sprite.draw(batch);
		batch.end();

		if ((game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_PLAY))
			//	&& (game.multiplayerSessionInfo.serverAddress != null)
			//	&& (game.multiplayerSessionInfo.serverPort != 0))
		{
			// Create MMClient and connect to server
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
			gsm.set(new PlayScreen(game, gsm));

		} else if (game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_MENU) {
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
