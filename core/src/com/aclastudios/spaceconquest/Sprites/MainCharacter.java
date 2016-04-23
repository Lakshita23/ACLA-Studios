package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Weapons.FireBall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

//This is the main character class where all the features are implemented
public class MainCharacter extends Sprite {
    public final String[] area = {"Team1Spawn","Team2Spawn"};
    private float xSpeedPercent, ySpeedPercent,lastXPercent, lastYPercent;
    private float lastAngle = 0;
    public World world;
    public Body b2body;
    private PlayScreen screen;
    TiledMap map;
    protected Fixture fixture;
    private TextureRegion character;

    private int knapsackCount = 0;
    private int additionalWeight = 0;
    private int threshold = 5;
    private float defaultRadius = 11/ SpaceConquest.PPM;

    private float radius = 11/ SpaceConquest.PPM;
    private int charScore;
    private float playerHP = 10;
    private int maxHp = 10;
    private int defaultMaxHp = 10;

    private Array<FireBall> fireballs;
    private int fireCount;
    private int IFCount;

    private float scale = (float) (1.0/10);

    // used for respawning the character
    private float stateTime;
    private boolean setToDestroy;
    private boolean destroyed;

    // for animating the sprite
    private boolean boostPressed = false;

    private enum State { STANDING, RUNNING, DASHING };
    private State currentState;
    private State previousState;
    private boolean buffMode = false;
    private Animation running;
    private Animation dashing;
    private float stateTimer;
    private float buffTimer;
    private int buffoutTime = 10;
    private int buffWeight = 10;
    private float buffRadius = 25/ SpaceConquest.PPM;
    private boolean enableBuff = false;
    private int valueForBuff = 9;
    private int buffCoolDown = 30;

    //potentially useless
    private float x_value;
    private float y_value;

    //last known position of main character
    private float last_x_coord;
    private float last_y_coord;

    //resource and asset
    private int iron_count = 0;
    private int oil_count = 0;
    private int gun_powder_count = 0;
    private int iron_storage = 0;
    private int oil_storage = 0;
    private int gun_powder_storage = 0;

    //gadgets
    private int ammunition;
    private int default_ammunition = 20;
    private int ammoLevel = 1;
    private float jetpack_time;
    private float default_jetpack_time = 6;
    private int jpLevel = 1;

    private float characterSize =  25/ SpaceConquest.PPM;
    private boolean inEnemyZone = false;
    private ArrayList<Integer> killedBy = new ArrayList<Integer>();
    private Sound fire;
    private Sound imbafire;
    private Music sumo;



    public MainCharacter(World world,PlayScreen screen, String SpriteName){
        super(screen.getAtlas().findRegion(SpriteName));
        this.screen = screen;
        this.world = world;
        map =screen.getMap();
        imbafire = Gdx.audio.newSound(Gdx.files.internal("sounds/imbafireball.mp3"));
        fire = Gdx.audio.newSound(Gdx.files.internal("sounds/fireball.wav"));
        sumo = Gdx.audio.newMusic(Gdx.files.internal("sounds/sumo_mode.wav"));
        // initializing variables for animation:
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        buffTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // animation for walking
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), getRegionX() + i * 200, getRegionY(), 200, 200));
        }
        running =new Animation(0.15f, frames);
        frames.clear();

        //animation for dashing
        for (int i = 4; i < 8; i++) {
            frames.add(new TextureRegion(getTexture(), getRegionX() + i * 200, getRegionY(), 200, 200));
        }
        dashing =new Animation(0.25f, frames);


        defineCharacter();
        character = new TextureRegion(getTexture(), getRegionX() + 200, getRegionY(), 200, 200);
        setBounds(0, 0, characterSize,characterSize);
        setRegion(character);
        fireballs = new Array<FireBall>();
        fireCount = 0;

        lastXPercent = 1;
        lastYPercent = 0;
        xSpeedPercent = 0;
        ySpeedPercent = 0;

        stateTime = 0;
        setToDestroy = false;
        destroyed = false;
    }

    //defining the essential parts of the main character such as body, what it can collide with and
    //finding the position where it will spawn
    public void defineCharacter(){
        knapsackCount = 0;
        buffMode = false;
        updateGadgetLevel();
        BodyDef bdef = new BodyDef();
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches(area[screen.getUserID()/(screen.getNumOfPlayers()/2)])) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(screen.getUserID()%3).getRectangle();
                last_x_coord = (rect.getX()*SpaceConquest.MAP_SCALE)/ SpaceConquest.PPM;
                last_y_coord = (rect.getY()*SpaceConquest.MAP_SCALE)/ SpaceConquest.PPM;
                bdef.position.set(last_x_coord,last_y_coord); //temp set position

            }
        }
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(defaultRadius);

        xSpeedPercent = 0;
        ySpeedPercent = 0;
        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.MAIN_CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                |SpaceConquest.OBJECTIVE_BIT
                |SpaceConquest.FIREBALL_BIT
                |SpaceConquest.IRON_BIT
                |SpaceConquest.GUNPOWDER_BIT
                |SpaceConquest.OIL_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.ENEMY_STATION_BIT
                |SpaceConquest.CHARACTER_BIT
                |SpaceConquest.IMBA_FIREBALL_BIT; //What can the character collide with?
        //Body
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    //this method updates the ammo level, jp level, ammunition, jetpack time based on the resource that
    //the character has
    public void updateGadgetLevel(){
        ammoLevel = Math.min(iron_storage/threshold,gun_powder_storage/threshold);
        jpLevel = Math.min(iron_storage/threshold,oil_storage/threshold);
        ammunition = default_ammunition+ammoLevel*20;
        jetpack_time = default_jetpack_time + jpLevel*5;
    }

    //this method is called when mainCharacter transit from sumo mode to normal mode
    //it just change the body, hp and sprite size
    public void redefineCharacter(){
        ammunition = (ammunition>100?100:ammunition);
        this.additionalWeight -= buffWeight;
        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        shape.setRadius(radius);
//        setScale(characterSize/(buffRadius));
        setScale(1);
        this.playerHP = (playerHP>maxHp?maxHp:playerHP);
    }

    //this method is called when mainCharacter transit from normal mode to sumo mode
    //it changes the body size, hp and sprite size
    //decreases the resources based on the resource required to enter sumo mode and update hud
    public void defineBuffCharacter(){
        sumo.play();
        iron_storage-=valueForBuff;
        gun_powder_storage-=valueForBuff;
        oil_storage-=valueForBuff;
        if(iron_storage<valueForBuff||gun_powder_storage<valueForBuff||oil_storage<valueForBuff){
            enableBuff=false;
        }
        buffTimer = 0;
        buffMode=true;
        ammunition = 200;
        this.additionalWeight += buffWeight;
        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        shape.setRadius(buffRadius);
        setScale(buffRadius/defaultRadius);
        this.playerHP = ((((this.playerHP+5)*5)>100)?100:(this.playerHP+5)*5);
        Hud.updateknapscore(iron_count + gun_powder_count + oil_count, oil_storage, iron_storage, gun_powder_storage);
    }

    //this method is called by the playscreen at every frame for updating
    public void update(float dt){
        stateTime += dt;
        buffTimer += dt;
        maxHp = 10 + 5*(iron_storage+oil_storage+gun_powder_storage)/9;
        if (playerHP<maxHp){
            playerHP+=0.01;
        }
        if (inEnemyZone){
            playerHP -= 0.2;
            if (playerHP<=0){
                inEnemyZone=false;
                dead();
                playerHP=maxHp;
            }
        }

        //checks if buff time is out
        if(buffMode && buffTimer >= buffoutTime){
            buffMode = false;
            buffTimer = 0;
            redefineCharacter();
        }
        if (setToDestroy ) {
            destroyed = true;
            setToDestroy = false;
            stateTime = 0;
            world.destroyBody(b2body);
        }
        if(destroyed) {
            stateTime = 0;
            defineCharacter();
            destroyed = false;
        }else {
            last_x_coord = b2body.getPosition().x;
            last_y_coord = b2body.getPosition().y;
            setPosition(last_x_coord - getWidth() / 2, last_y_coord - getHeight() / 2);
            setRegion(getFrame(dt));
        }

        x_value=b2body.getPosition().x - getWidth() / 2;
        y_value=b2body.getPosition().y - getHeight() / 2;
        setPosition(x_value, y_value);
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
        setRotation(getAngle());
    }

    //get the animation frame of the current state and state timer
    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DASHING:
                region = dashing.getKeyFrame(stateTimer, true);
                break;
            case RUNNING:
                region = running.getKeyFrame(stateTimer, true);
                break;
            default:
                region = character;
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
//        System.out.println("X: " + b2body.getLinearVelocity().x);
//        System.out.println("Y: " + b2body.getLinearVelocity().y);
        if(boostPressed){
            return State.DASHING;
        }
        if (b2body.getLinearVelocity().x > 5 || b2body.getLinearVelocity().x < -5 || b2body.getLinearVelocity().y > 5 || b2body.getLinearVelocity().y < -5) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public float getySpeedPercent() {
        return ySpeedPercent;
    }

    public float getxSpeedPercent() {
        return xSpeedPercent;
    }

    public void setxSpeedPercent(float xSpeedPercent) {
        this.xSpeedPercent = xSpeedPercent;
    }

    public void setySpeedPercent(float ySpeedPercent) {
        this.ySpeedPercent = ySpeedPercent;
    }

    public int getAdditionalWeight() {
        return additionalWeight;
    }

    //this method is called when the character collides with the resources
    public void increaseKnapSack(float charWeight) {
        knapsackCount++;
        if(knapsackCount > threshold) {
            this.additionalWeight += charWeight;
            Array<Fixture> fix = b2body.getFixtureList();


            //stop user from collecting resource when user is too heavy
            if (this.additionalWeight >= 10) {
                Filter filter = fix.get(0).getFilterData();
                filter.maskBits = SpaceConquest.OBSTACLE_BIT
                        | SpaceConquest.FIREBALL_BIT
                        | SpaceConquest.IMBA_FIREBALL_BIT
                        | SpaceConquest.STATION_BIT
                        | SpaceConquest.ENEMY_STATION_BIT
                        | SpaceConquest.CHARACTER_BIT;
                fix.get(0).setFilterData(filter);
            }
        }
    }

    //fires a fire ball based on the user's mode:normal or sumo
    public float[] fire(){
        if(!buffMode) {
            fireCount += 1;
            fire.play();
        }else{
            IFCount+=1;
            imbafire.play();
        }
        ammunition-=1;
        float[] s = {b2body.getPosition().x,b2body.getPosition().y};

        FireBall f = new FireBall(screen, s[0], s[1], lastXPercent,
                lastYPercent, (buffMode)?buffRadius:radius, false, screen.getUserID(), buffMode);

        fireballs.add(f);
        return s;
    }
    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

    //this method is called when ever the character touches the spaceship
    public void depositResource() {
        additionalWeight = (buffMode?buffWeight:0);
        knapsackCount = 0;
        radius = defaultRadius;
        playerHP = ((playerHP>maxHp)?playerHP:maxHp);
        //storing the resource and converting them into valued item
        iron_storage+=iron_count;
        gun_powder_storage+=gun_powder_count;
        oil_storage+=oil_count;
        iron_count = 0;
        gun_powder_count=0;
        oil_count = 0;

        if(iron_storage>(ammoLevel*2) && gun_powder_storage>(ammoLevel*2)){
            ammoLevel+=1;
        }
        if(oil_storage>(jpLevel*2))
            jpLevel+=1;
        ammunition = 20 + ammoLevel*15;
        jetpack_time = default_jetpack_time+(float) (jpLevel*2.0);

        if(iron_storage>valueForBuff&&gun_powder_storage>valueForBuff&&oil_storage>valueForBuff){
            enableBuff = true;
            buffCoolDown-=(Math.min(Math.min(iron_storage%valueForBuff,gun_powder_storage%valueForBuff),
                    oil_storage%valueForBuff));
        }

        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        shape.setRadius(buffMode?buffRadius:defaultRadius);
        //player can now collide with resource
        Filter filter = fix.get(0).getFilterData();
        filter.maskBits =  SpaceConquest.OBSTACLE_BIT
                |SpaceConquest.IRON_BIT
                |SpaceConquest.GUNPOWDER_BIT
                |SpaceConquest.OIL_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.ENEMY_STATION_BIT
                |SpaceConquest.CHARACTER_BIT
                |SpaceConquest.FIREBALL_BIT
                |SpaceConquest.IMBA_FIREBALL_BIT;
        fix.get(0).setFilterData(filter);
    }

    public void dead(){
        iron_count = 0;
        gun_powder_count = 0;
        oil_count = 0;
        setToDestroy = true;
        radius=defaultRadius;
        additionalWeight = 0;
        knapsackCount = 0;
        Hud.updateknapscore(iron_count+gun_powder_count+oil_count, oil_storage, iron_storage, gun_powder_storage);
        redefineCharacter();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public float[] getLast_xy_coord() {
        return new float[]{last_x_coord, last_y_coord};
    }

    public void addOil_count() {
        this.oil_count++;
    }

    public void addIron_count() {
        this.iron_count++;
    }

    public void addGun_powder_count() {
        this.gun_powder_count++;
    }

    public int getAmmunition() {
        return ammunition;
    }

    public void exhaustJetPack(float dt){
        jetpack_time -= (dt);
    }

    public float getJetpack_time() {
        return jetpack_time;
    }
    public void takeFireballDamage(boolean imbaOrNot){

        //if fireball is imba, then take 10 damage instead
        playerHP-=(imbaOrNot?10:4);
        if (playerHP<=0){
            dead();
            playerHP=maxHp;
        }
    }
    public float getHP(){
        return playerHP;
    }

    //returns the knapsack weight of the character and the resources back in their space station
    public int[] getKnapsackInfo() {
        return new int[]{oil_count+gun_powder_count+iron_count, oil_storage,iron_storage,
                gun_powder_storage};
    }

    //get the ammunition count and jet pack time
    public float[] getGadgetInfo() {
        return new float[]{ammunition,jetpack_time};
    }

    //get the angle of the maincharacter based on the input at the touchpad
    public float getAngle(){

        if(xSpeedPercent != 0 || ySpeedPercent != 0){
            lastXPercent = xSpeedPercent;
            lastYPercent = ySpeedPercent;
        }

        if(xSpeedPercent>0 && ySpeedPercent>0){
            lastAngle =(float)Math.toDegrees(Math.atan(ySpeedPercent / xSpeedPercent));
        }
        else if(xSpeedPercent<0){
            lastAngle =180+(float)Math.toDegrees(Math.atan(ySpeedPercent / xSpeedPercent));
        }
        else if (xSpeedPercent>0 && ySpeedPercent<0){
            lastAngle =360+(float)Math.toDegrees(Math.atan(ySpeedPercent / xSpeedPercent));
        }
        return lastAngle;

    }

    public float getLastXPercent() {
        return lastXPercent;
    }

    public float getLastYPercent() {
        return lastYPercent;
    }

    public float getRadius() {
        return radius;
    }

    public void setKilledBy(int playerID) {
        this.killedBy.add(playerID);
    }

    public int getFireCount() {
        return fireCount;
    }

    public void setInEnemyZone(boolean inEnemyZone) {
        this.inEnemyZone = inEnemyZone;
    }

    public boolean isBuffMode() {
        return buffMode;
    }

    public float getBuffRadius() {
        return buffRadius;
    }

    public int getIFCount() {
        return IFCount;
    }

    public boolean isEnableBuff() {
        return enableBuff;
    }

    public int getBuffCoolDown() {
        return buffCoolDown;
    }

    public void setBoostPressed(boolean boostPressed) {
        this.boostPressed = boostPressed;
    }
}

