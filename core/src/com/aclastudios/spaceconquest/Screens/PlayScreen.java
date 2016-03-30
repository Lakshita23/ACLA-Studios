package com.aclastudios.spaceconquest.Screens;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.Enemy;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.aclastudios.spaceconquest.SupportThreads.ClientThread;
import com.aclastudios.spaceconquest.Sprites.ResourceManager;
import com.aclastudios.spaceconquest.Tools.B2WorldCreator;
import com.aclastudios.spaceconquest.Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;


public class PlayScreen implements Screen {

    private int userID;
    private SpaceConquest game;
    private TextureAtlas atlas;
    Texture texture;
    Texture spaceman;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private float rateOfFire = (float) 0.1;
    private float coolDown;
    private float x;
    private float y;
    private float lastX ;
    private float lastY ;
    private float width;
    private float height;

    private float lastAngle = 0;
    private float currentAngle;
    private TmxMapLoader maploader; //Load the map into the game
    private TiledMap map; //Reference to the map
    private OrthogonalTiledMapRenderer renderer; //Renders the map to the screen

    private World world;
    private Box2DDebugRenderer b2dr; //graphic representation of the body in the box 2d

    //Touchpad
    private OrthographicCamera camera;
    private Stage stage;
    private Stage stage2;

    private BitmapFont font;
    private TextureAtlas buttonsAtlas; //** image of buttons **//
    private Skin buttonSkin; //** images are used as skins of the button **//
    private Table table;
    private TextButton button;
    private Label heading;

    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private GameScreenManager gsm;
    //Sprites
    private MainCharacter mainCharacter;
    private Enemy enemy;
    //Threads
    private ClientThread clientThread;

    private ResourceManager resourceManager;

    public PlayScreen(SpaceConquest game, GameScreenManager gsm){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        this.gsm = gsm;
        this.userID = 1;
        //Background and Character assets
        texture = new Texture("map.png");

        spaceman = new Texture("astronaut.png");

        //Game map and Game View
        //camera of the map
        gamecam  = new OrthographicCamera();
        //create a FitViewport to maintain virtual aspect ratio
        gamePort = new FitViewport(SpaceConquest.V_WIDTH,SpaceConquest.V_HEIGHT,gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("map-orthogonal.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //Creating the box 2d world
        world = new World(new Vector2(0,0),true);
        b2dr = new Box2DDebugRenderer();

        //B2world
        new B2WorldCreator(this);

        //Sprites
        mainCharacter = new MainCharacter(world,this);
        enemy = new Enemy(world,this,0);
        mainCharacter.setOriginCenter();


        resourceManager = new ResourceManager(this);


        coolDown = 0;
        lastX = 1;
        lastY = 0;

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches("resourceSpawningArea")) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(0).getRectangle();
                this.x = rect.getX();
                this.y = rect.getY();
                this.width = rect.getWidth();
                this.height = rect.getHeight();
            }
        }
        //set world listener
        world.setContactListener(new WorldContactListener(this));

        //touchpad setup
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("touchpad/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("touchpad/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 50, 50);

        buttonsAtlas = new TextureAtlas("button/button.pack");
        buttonSkin = new Skin(buttonsAtlas);

//        table = new Table(buttonSkin);
//        table.setBounds(50,50, 50, 50);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonSkin.getDrawable("touchBackground");
        textButtonStyle.down = buttonSkin.getDrawable("touchKnob");
        font = new BitmapFont();
        textButtonStyle.font = font;

        button = new TextButton("FIRE", textButtonStyle);
        button.setBounds(50, 50, 50, 50);
        //table.add(button);

        //Create a Stage and add TouchPad
        stage = new Stage(gamePort, game.batch);
        stage.addActor(touchpad);
        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);

        //thread initialize
        clientThread = new ClientThread(game);
        clientThread.start();
    }
    @Override
    public void show() {

    }


    public void handleInput(float dt){
        coolDown +=dt;
        if (button.isPressed() && coolDown >rateOfFire) {
            coolDown = 0;
            mainCharacter.fire(lastX, lastY);
            mainCharacter.b2body.applyLinearImpulse(new Vector2((float) (mainCharacter.b2body.getLinearVelocity().x * -0.9),
                    (float) (mainCharacter.b2body.getLinearVelocity().y * -0.9)), mainCharacter.b2body.getWorldCenter(), true);
        }
        else {
            double speedreduction = Math.pow(0.9, mainCharacter.getCharWeight()*0.5);
            mainCharacter.setScale(mainCharacter.getCharacterScale());
            mainCharacter.setxSpeed((float) (touchpad.getKnobPercentX() * speedreduction));
            mainCharacter.setySpeed((float) (touchpad.getKnobPercentY() * speedreduction));
            mainCharacter.b2body.applyLinearImpulse(new Vector2(mainCharacter.getxSpeed(), mainCharacter.getySpeed()), mainCharacter.b2body.getWorldCenter(), true);
        }
        //System.out.println("last x is " + lastX + " last y is " + lastY);
        currentAngle = getAngle(mainCharacter);
        mainCharacter.setRotation(currentAngle);
    }

    public void update(float dt){
        //input updates
        handleInput(dt);

        //Allows box2d calculate the physics
        world.step(1 / 60f, 6, 2);

        //hud timer
        hud.update(dt);

        //stopping the character
        slowDownCharacter();
        //System.out.println("x speed is " + mainCharacter.b2body.getLinearVelocity().x + "touch pad " + touchpad.isTouched());
        //sprites
        mainCharacter.update(dt);
        enemy.update(dt);

        while ((resourceManager.getIron_count()+resourceManager.getGunpowder_count()+resourceManager.getOil_count())<21)
            resourceManager.generateResources(this.x, this.y, this.width, this.height);

        
        resourceManager.updateIron(dt);
        resourceManager.updateGunPowder(dt);
        resourceManager.updateOil(dt);

        gamecam.position.x = mainCharacter.b2body.getPosition().x;
        gamecam.position.y = mainCharacter.b2body.getPosition().y;
        //gamecam updates
        gamecam.update();
        renderer.setView(gamecam); //render only what the gamecam can see

        button.setPosition(gamecam.position.x+gamePort.getWorldWidth() / 4 +10,gamecam.position.y-gamePort.getWorldHeight()/2+10);
        touchpad.setPosition(gamecam.position.x-gamePort.getWorldWidth() / 2+10,gamecam.position.y-gamePort.getWorldHeight()/2+10);
    }
    //render
    @Override
    public void render(float delta) {
        if(hud.isTimeUp()==true){
            gsm.set(new GameOver(game, gsm));
        }

        //make sure that everything is updated
        update(delta);

        //clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1); //clear colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clear the screen
        //backgroup and character image (Used for test)
        game.batch.setProjectionMatrix(gamecam.combined); //only render what the camera can see

        //render the map
        renderer.render();
        game.batch.begin(); //opens the "box"
        game.batch.draw(texture, 0, 0, texture.getWidth() * SpaceConquest.MAP_SCALE, texture.getHeight() * SpaceConquest.MAP_SCALE);
        //game.batch.draw(spaceman, gamecam.position.x - 20, gamecam.position.y - 20, 50, 50);

        mainCharacter.draw(game.batch);
        enemy.draw(game.batch);
        for(int i=0;i<resourceManager.getIron_count();i++)
            resourceManager.getIron_array(i).draw(game.batch);
        for(int i=0;i<resourceManager.getGunpowder_count();i++)
            resourceManager.getGunpowder_array(i).draw(game.batch);
        for(int i=0;i<resourceManager.getOil_count();i++)
            resourceManager.getOil_array(i).draw(game.batch);

        mainCharacter.draw(game.batch);
        if(!enemy.isDestroyed())
            enemy.draw(game.batch);

        game.batch.end(); //close the "box" and draw it on the screen


        //render our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        //Join/Combine hud camera to game batch
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //Draw the touch pad
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();



        if(hud.isTimeUp()==true){
            gsm.set(new GameOver(game, gsm));
        }
        float x = mainCharacter.b2body.getPosition().x;
        float y = mainCharacter.b2body.getPosition().y;
//        game.playServices.BroadcastMessage(x + " " + y);
    }

    @Override
    public void resize(int width, int height) {

        System.out.println("updating");
        gamePort.update(width, height);
    }


    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }
    public TextureAtlas getAtlas() {
        return atlas;
    }

    //returns angle in degrees
    public float getAngle(MainCharacter c){
        float x =c.getxSpeed();
        float y = c.getySpeed();

        if(x != 0 || y != 0){
            lastX = x;
            lastY = y;
        }

        if(x>0 && y>0){
            lastAngle =(float)Math.toDegrees(Math.atan(y / x));
        }
        else if(x<0){
            lastAngle =180+(float)Math.toDegrees(Math.atan(y / x));
        }
        else if (x>0 && y<0){
            lastAngle =360+(float)Math.toDegrees(Math.atan(y / x));
        }
        return lastAngle;

    }

    private void slowDownCharacter() {
        if(!touchpad.isTouched()) {
            mainCharacter.b2body.applyLinearImpulse(new Vector2((float) (mainCharacter.b2body.getLinearVelocity().x * -0.1), (float) (mainCharacter.b2body.getLinearVelocity().y * -0.1)), mainCharacter.b2body.getWorldCenter(), true);
        }
    }

    public void increaseCharWeight(int w){
        mainCharacter.addCharWeight(w);
    }
    public int depositResource(){
        int res = mainCharacter.getCharWeight();
        mainCharacter.depositResource();
        return res;
    }
    public int getUserID() {
        return userID;
    }
}
