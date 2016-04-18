package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class LeadersBoardScreen implements Screen {

	private Stage stage;
	private SpriteBatch batcher;
	private Sprite sprite;
	private SpaceConquest game;
	private GameScreenManager gsm;

	private float gameWidth;
	private float gameHeight;

	public LeadersBoardScreen(SpaceConquest game, GameScreenManager gsm) {
		this.game = game;
		this.gsm = gsm;
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_LEADER) {
			game.playServices.getLeaderboardGPGS();
		}
		else if (game.multiplayerSessionInfo.mState == game.multiplayerSessionInfo.ROOM_MENU) {
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
