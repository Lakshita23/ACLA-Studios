package com.aclastudios.spaceconquest;
//main class
import com.aclastudios.spaceconquest.PlayGameService.MultiplayerSessionInfo;
import com.aclastudios.spaceconquest.PlayGameService.PlayServices;
import com.aclastudios.spaceconquest.Screens.GameScreenManager;
import com.aclastudios.spaceconquest.Screens.MenuScreen;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class SpaceConquest extends Game {
	public static final int V_WIDTH = 400; //Virtual screen width of the game
	public static final int V_HEIGHT =208; //Virtual screen height of the game
	public static final float PPM = 2; //scaling the game screen
	public static final float MAP_SCALE = (float) 0.7; //scaling the map
	public static SpriteBatch batch;

	private GameScreenManager gsm;

	//Collision bit are 2^n, because they will be put through or operation to check for collision
	public static final short OBSTACLE_BIT = 1;
	public static final short MAIN_CHARACTER_BIT = 2;
	public static final short IRON_BIT = 4;
	public static final short OBJECTIVE_BIT = 8;
	public static final short STATION_BIT = 16;
	public static final short CHARACTER_BIT = 32;
	public static final short GUNPOWDER_BIT = 64;
	public static final short OIL_BIT = 128;
	public static final short ENEMY_STATION_BIT = 256;
	public static final short FIREBALL_BIT = 1024;
	public static final short FRIENDLY_FIREBALL_BIT = 2048;
	public static final short IMBA_FIREBALL_BIT = 4096;

	public PlayServices playServices;
	public MultiplayerSessionInfo multiplayerSessionInfo;

	public SpaceConquest() {

	}
	public SpaceConquest(PlayServices playServices, MultiplayerSessionInfo multiplayerSessionInfo)
	{
		this.playServices = playServices;
		this.multiplayerSessionInfo=multiplayerSessionInfo;
	}


	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameScreenManager();
		gsm.push(new MenuScreen(this, gsm));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.render(Gdx.graphics.getDeltaTime());
	}
}
