package com.aclastudios.spaceconquest.Screens;



import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.SideCharacter;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.aclastudios.spaceconquest.Sprites.ResourceManager;
import com.aclastudios.spaceconquest.Scores.Server;
import com.aclastudios.spaceconquest.Tools.B2WorldCreator;
import com.aclastudios.spaceconquest.Tools.HealthBar;
import com.aclastudios.spaceconquest.Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;

public class PlayScreen implements Screen {

    //user id and server id are used for Google Play Services communication identification
    private int userID;
    private int serverID;

    //Used to store the name of the Sprite name for different UserID
    private String[] spriteName = {"PYRO", "DAACTAR"};
    private int numOfPlayers = 2;

    //Use as a instance for the SpaceConquest so that playscreen can get important instances like
    //SpriteBatch in order to run the game
    private SpaceConquest game;
    private TextureAtlas atlas;
    Texture mapTexture;

    //Cameras and viewport for user to see the gameplay
    private OrthographicCamera gamecam;
    private OrthographicCamera controllerCamera;
    private Stage stage;     //Stage is used for the controller
    private Viewport gamePort;
    private Viewport viewport;
    private Hud hud;

    //Fireball variables
    private float rateOfFire = (float) 0.3;
    private float coolDown;
    private float buffCoolDown=20;

    //Variables for spawning of resources
    private float x;
    private float y;
    private float width;
    private float height;

    private TmxMapLoader maploader; //Load the map into the game
    private TiledMap map; //Reference to the map
    private OrthogonalTiledMapRenderer renderer; //Renders the map to the screen

    private World world; //creating a world
    private Box2DDebugRenderer b2dr; //graphic representation of the body in the box 2d

    //For Controller Buttons
    private TextureAtlas buttonsAtlas; //** image of buttons **//
    private Skin buttonSkin; //** images are used as skins of the fireButton **//
    private ImageButton fireButton;
    private ImageButton boostButton;
    private ImageButton buffMode_Button;
    private Texture sumo_up;
    private Texture sumo_down;
    private Texture boost_up;
    private Texture boost_down;
    private Texture red;
    private Texture health;
    private Texture orange;

    //Touchpad
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    //Sprites for MainCharacters, sideCharacters and resources
    private MainCharacter mainCharacter;
    HealthBar healthBar;
    private HashMap<Integer,SideCharacter> enemyhashmap;
    private HashMap<Integer,String[]> positionvalues;
    private ResourceManager resourceManager;

    //Server
    Server server;
    private int time;

    //music
    private Music music;
    private Music boostSound;

    //Game State/Screen Manager
    private GameScreenManager gsm;

    public PlayScreen(SpaceConquest game, GameScreenManager gsm){
        // playing the music
        music = Gdx.audio.newMusic(Gdx.files.internal("menuMusic/In-game.mp3"));
        music.setLooping(false);
        music.play();

        //this sound is for the jet pack
        boostSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/boost.wav"));

        //This rocket sound is played when Boost Button is pressed
        boostSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/boost.wav"));

        //Getting the texture atlas
        atlas = new TextureAtlas("sprites/sprite.txt");

        //For game
        this.game = game;
        this.gsm = gsm;

        //player 0 is the server by default
        this.userID=0;
        this.serverID=0;

        //Number of players are 2 by default unless the game become 2v2 or 3v3
        this.numOfPlayers = 2;

        //time limit of the game is 300 seconds
        this.time = 300;

        //getting the info from Google Play Services
        this.userID = game.multiplayerSessionInfo.mParticipantsId.indexOf(game.multiplayerSessionInfo.mId);
        numOfPlayers =  game.multiplayerSessionInfo.mParticipants.size();
        game.multiplayerSessionInfo.mId_num=this.userID;

        //Background and Character assets
        mapTexture = new Texture("map/map_spaceship.png");

        //Game map and Game View
        gamecam  = new OrthographicCamera();
        gamecam.setToOrtho(false,SpaceConquest.V_WIDTH/ SpaceConquest.PPM,SpaceConquest.V_HEIGHT/SpaceConquest.PPM);

        //controller controllerCamera of the game
        controllerCamera = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio
        gamePort = new FitViewport(SpaceConquest.V_WIDTH/SpaceConquest.PPM,SpaceConquest.V_HEIGHT/SpaceConquest.PPM,gamecam);
        viewport = new FitViewport(SpaceConquest.V_WIDTH, SpaceConquest.V_HEIGHT, controllerCamera);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch,this);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("map/map-orthogonal_2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1/ SpaceConquest.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //Creating the box 2d world
        world = new World(new Vector2(0,0),true);
        b2dr = new Box2DDebugRenderer();

        //Creating the world
        new B2WorldCreator(this);

        //Sprites and Characters
        positionvalues = new HashMap<Integer, String[]>();
        enemyhashmap = new HashMap<Integer, SideCharacter>();
        mainCharacter = new MainCharacter(world,this,spriteName[userID/(numOfPlayers/2)]);
        for (int i = 0; i< numOfPlayers;i++) {
            if (i!=userID) {
                SideCharacter sideCharacter = new SideCharacter(world, this, i,spriteName[i/(numOfPlayers/2)]);
                enemyhashmap.put(i, sideCharacter);
            }
        }
        //set origin to center so for sprite rotation and scaling
        mainCharacter.setOriginCenter();

        //set firebutton cooldown to 0
        coolDown = 0;

        //Initialize Spawn Area : around center of the map
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches("resourceSpawningArea")) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(0).getRectangle();
                this.x = rect.getX()/ SpaceConquest.PPM;
                this.y = rect.getY()/ SpaceConquest.PPM;
                this.width = rect.getWidth()/ SpaceConquest.PPM;
                this.height = rect.getHeight()/ SpaceConquest.PPM;
            }
        }

        //set world listener
        world.setContactListener(new WorldContactListener(this,game));

        //Initialise resource manager
        resourceManager = new ResourceManager(this, game, userID, x, y, width, height);

        //touchpad setup
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("touchpad/knob.png"));
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(0, 0, 70, 70);

        //get fireButton atlas
        buttonsAtlas = new TextureAtlas("button/button.pack");
        buttonSkin = new Skin(buttonsAtlas);

        //creating fireButton texture for each fireButton condition
        sumo_up = new Texture(Gdx.files.internal("button/sumo_button_up.png"));
        sumo_down = new Texture(Gdx.files.internal("button/sumo_button_down.png"));
        boost_up = new Texture(Gdx.files.internal("button/boost_button_up.png"));
        boost_down = new Texture(Gdx.files.internal("button/boost_button_down.png"));
        red = new Texture(Gdx.files.internal("button_red.png"));
        orange = new Texture(Gdx.files.internal("button_orange.png"));
        health = new Texture(Gdx.files.internal("healthbar.png"));

        //create health bar for main character
        healthBar = new HealthBar(new TextureRegion(health),mainCharacter);

        //create a tint colour
        Color tintColor = new Color(0.5f, 0.5f, 0.5f, 1f);

        //creating fire buttons
        ImageButton.ImageButtonStyle imbFire = new ImageButton.ImageButtonStyle(new TextureRegionDrawable(new TextureRegion(red)), new TextureRegionDrawable(new TextureRegion(orange))
                ,null,null,null,null);
        imbFire.disabled = buttonSkin.newDrawable(new TextureRegionDrawable(new TextureRegion(orange)),tintColor);
        fireButton = new ImageButton(imbFire);
        fireButton.setBounds(0, 0, 40, 40);

        //creating boost buttons
        ImageButton.ImageButtonStyle imbBoost = new ImageButton.ImageButtonStyle(new TextureRegionDrawable(new TextureRegion(boost_up)), new TextureRegionDrawable(new TextureRegion(boost_down))
                ,null,null,null,null);
        imbBoost.disabled = buttonSkin.newDrawable(new TextureRegionDrawable(new TextureRegion(boost_down)),tintColor);
        boostButton = new ImageButton(imbBoost);
        boostButton.setBounds(0, 0, 40, 40);

        //creating sumo buttons
        ImageButton.ImageButtonStyle imbSumo = new ImageButton.ImageButtonStyle(new TextureRegionDrawable(new TextureRegion(sumo_up)), new TextureRegionDrawable(new TextureRegion(sumo_down))
                ,null,null,null,null);
        imbSumo.disabled = buttonSkin.newDrawable(new TextureRegionDrawable(new TextureRegion(sumo_down)),tintColor);
//        buffMode_Button = new ImageButton(new TextureRegionDrawable(new TextureRegion(orange)), new TextureRegionDrawable(new TextureRegion(red)));
        buffMode_Button = new ImageButton(imbSumo);
        buffMode_Button.setBounds(0, 0, 40, 40);
        buffMode_Button.setDisabled(true);

        //Create a Stage and add in the controllers
        stage = new Stage(viewport, game.batch);
        stage.addActor(touchpad);
        stage.addActor(fireButton);
        stage.addActor(boostButton);
        stage.addActor(buffMode_Button);
        Gdx.input.setInputProcessor(stage);

        //set screen in android launcher and allows it to call playscreen object
        //for google play services communication
        game.playServices.setScreen(this);

        //If you are the server, create a new server
        if (this.userID==serverID){
            server=new Server(game);
        }

        //show the screen
        show();

        //set the position of buttons and touch pad in the camera and viewport
        fireButton.setPosition(controllerCamera.position.x + viewport.getWorldWidth() / 4 + 40, controllerCamera.position.y - viewport.getWorldHeight() / 2 + 10);
        boostButton.setPosition(controllerCamera.position.x + viewport.getWorldWidth() / 4, controllerCamera.position.y - viewport.getWorldHeight() / 2 + 10);
        buffMode_Button.setPosition(controllerCamera.position.x + viewport.getWorldWidth() / 4 + 20, controllerCamera.position.y-viewport.getWorldHeight()/2+30);
        touchpad.setPosition((controllerCamera.position.x - (viewport.getWorldWidth() / 2)) + 10,
                (controllerCamera.position.y - viewport.getWorldHeight() / 2) + (10));
    }

    @Override
    public void show() {
        MenuScreen.menuMusic.stop();
        //Server creates the first set of resources and then broadcasts resource information to all players
        if (userID==serverID) {
            resourceManager.generateResources();
        }
    }

    //handles the input of the user
    public void handleInput(float dt){
        coolDown +=dt;
        buffCoolDown+=dt;
        //checks if button is pressed when coolDown >rateOfFire and fireButton is enabled
        if (fireButton.isPressed() && coolDown >rateOfFire && !fireButton.isDisabled()) {
            coolDown = 0;
            mainCharacter.b2body.applyLinearImpulse(new Vector2((float) (mainCharacter.b2body.getLinearVelocity().x *-1),
                    (float) (mainCharacter.b2body.getLinearVelocity().y * -1)), mainCharacter.b2body.getWorldCenter(), true);
            mainCharacter.fire();
            //disable fire button when ammunition is 0
            if(mainCharacter.getAmmunition()==0){
                fireButton.setDisabled(true);
            }
        }
        else {
            //enable button if ammunition is >0
            if(mainCharacter.getAmmunition()>0){
                fireButton.setDisabled(false);
            }

            //reduce the speed based on the additional weight of the mainCharacter
            double speedCoeff = 2 * Math.pow(0.9, mainCharacter.getAdditionalWeight()*0.4);

            //if boost button is pressed when jetpack time is > 0.05 seconds
            if(boostButton.isPressed() && mainCharacter.getJetpack_time()>0.05){
                mainCharacter.setBoostPressed(true);
                mainCharacter.exhaustJetPack(dt);
                speedCoeff = 6;
                if (!boostSound.isPlaying()) {
                    boostSound.play();
                }
            }
            else {
                boostSound.stop();
                mainCharacter.setBoostPressed(false);
                //implement friction by applying negative force based on current
                mainCharacter.b2body.applyLinearImpulse(new Vector2((float) (mainCharacter.b2body.getLinearVelocity().x * -0.03),
                        (float) (mainCharacter.b2body.getLinearVelocity().y * -0.03)), mainCharacter.b2body.getWorldCenter(), true);
            }

            //set xSpeedPercent and ySpeedPercent in the mainCharacter and the
            mainCharacter.setxSpeedPercent(touchpad.getKnobPercentX());
            mainCharacter.setySpeedPercent(touchpad.getKnobPercentY());
            mainCharacter.b2body.applyLinearImpulse(new Vector2((float)(mainCharacter.getxSpeedPercent() * speedCoeff),
                    (float)(mainCharacter.getySpeedPercent() * speedCoeff)), mainCharacter.b2body.getWorldCenter(), true);
        }

        //buffbutton have cooled down and the maincharacter has enabled buff
        if(buffCoolDown >mainCharacter.getBuffCoolDown()&&mainCharacter.isEnableBuff()) {
            if(buffMode_Button.isDisabled()) {
                buffMode_Button.setDisabled(false);
            }
            if (buffMode_Button.isPressed() && !mainCharacter.isBuffMode()) {
                buffMode_Button.setDisabled(true);
                mainCharacter.defineBuffCharacter();
                buffCoolDown = 0;
            }
        }

    }

    public void update(float dt){
        if (!game.playServices.checkhost(this.serverID)){
            if (this.userID!=this.serverID && serverID<numOfPlayers-1) {
                this.serverID++;
            }
            if (this.userID==this.serverID){
                server=new Server(game);
                server.setRnBteamScore(hud.getRedScore(),hud.getBlueScore());
            }
        }
        //input updates
        handleInput(dt);

        //Calls box2d calculate the physics every 1/60 seconds
        world.step(1 / 60f, 6, 2);

        //gets the different value of the gadget that the mainCharacter is holding on to
        float[] gadget = mainCharacter.getGadgetInfo();
        //hud timer
        hud.update(dt, (int) gadget[0], gadget[1]);
        //stopping the character
        slowDownCharacter();
        //sprites
        mainCharacter.update(dt);
        //SideCharacter update
        for (int i: enemyhashmap.keySet()){
            SideCharacter sideCharacter = enemyhashmap.get(i);
            if (positionvalues!= null) {
                try{
                    String[] values = positionvalues.get(i);
                    if (values!=null) {
                        sideCharacter.updateEnemy(Float.parseFloat(values[1]),
                                Float.parseFloat(values[2]),
                                Float.parseFloat(values[3]),
                                Float.parseFloat(values[5]),
                                Float.parseFloat(values[7]),
                                Float.parseFloat(values[8]),
                                Integer.parseInt(values[9]),
                                Float.parseFloat(values[10]),
                                Float.parseFloat(values[11]),
                                Integer.parseInt(values[12]));
//                  sideCharacter.setRotation(Float.parseFloat(values[3]));
                        if (values[4].equals("false")) {
                            sideCharacter.dead();
                        }
                    }
                    sideCharacter.b2body.setLinearVelocity(Float.parseFloat(values[7]),Float.parseFloat(values[8]));
                }catch (Exception e){
                    System.out.println("error while updating character coordinate");
                    e.printStackTrace();
                }
            }
            sideCharacter.update(dt);
        }

        //Update resources to check if destroyed
        resourceManager.updateIron(dt);
        resourceManager.updateGunPowder(dt);
        resourceManager.updateOil(dt);

        float x=0,y=0;
        if(!mainCharacter.isDestroyed()) {
            x = mainCharacter.b2body.getPosition().x;
            y = mainCharacter.b2body.getPosition().y;
            gamecam.position.x = x;
            gamecam.position.y = y;
        }else {
            gamecam.position.x = mainCharacter.getLast_xy_coord()[0];
            gamecam.position.y = mainCharacter.getLast_xy_coord()[1];
        }


        //Broadcast your condition to all other players so that your character in other phones will be updated
        try {
            game.playServices.BroadcastUnreliableMessage(userID + ":" + x + ":" + y + ":" + mainCharacter.getAngle() + ":" +
                    String.valueOf(!mainCharacter.isDestroyed()) + ":" + (mainCharacter.isBuffMode() ? mainCharacter.getBuffRadius() : mainCharacter.getRadius())
                    + ":" + mainCharacter.getHP() +
                    ":" + mainCharacter.getLastXPercent() + ":" + mainCharacter.getLastYPercent() + ":" +
                    mainCharacter.getFireCount() + ":" + mainCharacter.b2body.getLinearVelocity().x +
                    ":" + mainCharacter.b2body.getLinearVelocity().y + ":" + mainCharacter.getIFCount());

        }catch (Exception e){
            System.out.println("error while sending message");
            e.printStackTrace();
        }


        //gamecam updates
        gamecam.update();
        //render only what the gamecam can see
        renderer.setView(gamecam);
    }

    //render
    @Override
    public void render(float delta) {
        //End game and transition to Game Over Screen when time is up
        try {
            //if time is up, update score and calculate which team is the winner
            if (hud.isTimeUp() == true) {
                music.stop();
                int len = game.multiplayerSessionInfo.mParticipants.size(); //Collect information from server regarding scores
                int myId = game.multiplayerSessionInfo.mId_num;
                int redScore = hud.getRedScore();
                int blueScore = hud.getBlueScore();
                int mykillScore = hud.getkills();
                game.playServices.leaveRoom();  //close session and exit room
                game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_NULL;
                game.playServices.submitScoreGPGS(hud.getkills());  //submit score to server before exiting
                game.multiplayerSessionInfo.mState = game.multiplayerSessionInfo.ROOM_MENU;
                gsm.set(new GameOver(game, gsm, len, myId, redScore, blueScore, mykillScore));  //goto gameOver screen to display result of the game
                dispose();

            }

            //calls update method to make sure that everything is updated
            update(delta);

            //clear screen
            Gdx.gl.glClearColor(0, 0, 0, 1); //clear colour
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clear the screen

            //render the map
            renderer.render();
            game.batch.setProjectionMatrix(gamecam.combined);
            game.batch.begin(); //opens the "box"
            game.batch.draw(mapTexture, 0, 0, (mapTexture.getWidth() * SpaceConquest.MAP_SCALE) / SpaceConquest.PPM,
                    (mapTexture.getHeight() * SpaceConquest.MAP_SCALE) / SpaceConquest.PPM);

            //Draw Side Characters
            for (int i: enemyhashmap.keySet()) {
                SideCharacter sideCharacter = enemyhashmap.get(i);
                try {
                    if (positionvalues != null) {
                        if (positionvalues.get(i)[6] != null) {
                            if (!sideCharacter.isDestroyed()) {
                                sideCharacter.draw(game.batch);
                                //sideCharacter healthbar
                                HealthBar SC_healthBar = new HealthBar(new TextureRegion(health), sideCharacter);
                                float SC_hp = Float.parseFloat(positionvalues.get(i)[6]);
                                SC_healthBar.setWidth(SC_hp);
                                SC_healthBar.draw(game.batch, SC_hp);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("error updating enemies coordinate");
                }
            }
            //generate a each resource if the respective resource count is < 7
            //Render resources: Iron, Gunpowder and Oil
            for (int i = 0; i < resourceManager.getIron_count(); i++)
                resourceManager.getIron_array(i).draw(game.batch);
            for (int i = 0; i < resourceManager.getGunpowder_count(); i++)
                resourceManager.getGunpowder_array(i).draw(game.batch);
            for (int i = 0; i < resourceManager.getOil_count(); i++)
                resourceManager.getOil_array(i).draw(game.batch);

            //draw main character
            mainCharacter.draw(game.batch);

            //draw maincharacter healthbar
            float hp = mainCharacter.getHP();
            healthBar.setWidth(hp);
            healthBar.draw(game.batch, hp);
            game.batch.end(); //close the "box" and draw it on the screen


            //render our Box2DDebugLines
//            b2dr.render(world, gamecam.combined);

            //Join/Combine hud controllerCamera to game batch
            game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud.stage.draw();

            //Draw the touch pad and buttons
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();

            //broadcast your time if you are the server, otherwise, update the time you receive from the server
            if (userID==serverID){
                System.out.println("updating time");
                try {
                    game.playServices.BroadcastUnreliableMessage("Time:" + hud.getTime());
                }catch (Exception e){}
            } else {
                hud.setTime(time);
            }

        }catch (Exception e){
            System.out.println("error in render");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("updating");
        gamePort.update(width, height);
        viewport.update(width, height);
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

    //for proper disposal of the assets so that subsequent match will be bug-free
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        stage.dispose();
    }
    public TextureAtlas getAtlas() {
        return atlas;
    }

    //slow down the character when touchpad isn't touched
    private void slowDownCharacter() {
        if(!touchpad.isTouched()) {
            mainCharacter.b2body.applyLinearImpulse(new Vector2((float) (mainCharacter.b2body.getLinearVelocity().x * -0.1), (float) (mainCharacter.b2body.getLinearVelocity().y * -0.1)), mainCharacter.b2body.getWorldCenter(), true);
        }
    }

    //listen to message sent over the network
    public void MessageListener(byte[] bytes){

        try {
            String message = new String (Arrays.copyOfRange(bytes, 0, bytes.length),"UTF-8");
            final String[] data = message.split(":");
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (data[0].equals("0") || data[0].equals("1") || data[0].equals("2")|| data[0].equals("3")|| data[0].equals("4")|| data[0].equals("5")) {
                        System.out.println("received enemies's coordinate");
                        String[] position = data.clone();
                        positionvalues.put(Integer.parseInt(data[0]), position);
                        System.out.println("finished updating ");
                    }
                    else if (data[0].equals("Serverpoints") && userID==0){
                        System.out.println("received points update from other players");
                        addscore(data[1], Integer.parseInt(data[2]));
                        System.out.println(data[0]+":"+data[1]+":"+data[2]);
                    }
                    else if (data[0].equals("UpdateScoreAll")){
                        System.out.println("received point update from server");
                        Hud.updatescore(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                    }
        //            else if (data[0].equals("fire")){
        //                FireBall f = new FireBall(this, Float.parseFloat(data[2]),
        //                        Float.parseFloat(data[3]), Float.parseFloat(data[4]), Float.parseFloat(data[5]),true);
        //                networkFireballs.add(f);
        //            }
                    else if (data[0].equals("Time")){
                        System.out.println("received time update");
                        time = Integer.parseInt(data[1]);
                    }
                    //Handle Resource generation broadcast
                    else if (data[0].equals("Resources")){
                        if (data[1].length()<21){   //If data is insufficient, ask server to resend via broadcast
                            game.playServices.BroadcastMessage("ResendR:"); //broadcast to resend resource data
                        }
                        else {
                            resourceManager.getResourceString(data[1]); //Send resource information to resource manager
                            resourceManager.generateResources();    //Initial resource generation for clients
                        }
                    }
                    else if (data[0].equals("ResendR")){    //Resend broadcast in case client's request server
                        if (getServerID()==getUserID()){
                            resourceManager.broadcastResources();
                        }

                    }
                    //Broadcast to handle Resource deletion (One at a time)
                    else if (data[0].equals("Delete")){
                        System.out.println("delete resource"+data[2]+" "+data[3]);
                        if (data[1].equals("Iron"))
                            resourceManager.delIron(Integer.parseInt(data[2]), Float.parseFloat(data[3]));
                        else if (data[1].equals("GunPowder"))
                            resourceManager.delGunPowder(Integer.parseInt(data[2]), Float.parseFloat(data[3]));
                        else if (data[1].equals("Oil"))
                            resourceManager.delOil(Integer.parseInt(data[2]), Float.parseFloat(data[3]));
                    }
                    //Broadcast to handle Resource Generation (One at a time)
                    else if (data[0].equals("Generate")) {
                        System.out.println("generate resources" + data[1] +" "+data[2] + " "+data[3]);
                        try {
                            if (data[1].equals("Iron")) {
                                System.out.println("generate iron");
                                resourceManager.addIron(Float.parseFloat(data[2]), Float.parseFloat(data[3]));
                            }
                            else if (data[1].equals("GunPowder")) {
                                System.out.println("generate gunpowder");
                                resourceManager.addGunPowder(Float.parseFloat(data[2]), Float.parseFloat(data[3]));
                            }
                            else if (data[1].equals("Oil")) {
                                System.out.println("generate oil");
                                resourceManager.addOil(Float.parseFloat(data[2]), Float.parseFloat(data[3]));
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if (data[0].equals("KillBonus")){
                        hud.addkill();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in receiving message");
        }
    }

    //adds the score and update the hud
    public void addscore(String id,int data){
        int num = Integer.parseInt(id);
        if (num<numOfPlayers/2){
            server.addRedScore(data);
        } else {
            server.addBlueScore(data);
        }
    }


    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public int getUserID() {
        return userID;
    }

    public int getServerID() {
        return serverID;
    }

    public float getRateOfFire() {
        return rateOfFire;
    }

}

