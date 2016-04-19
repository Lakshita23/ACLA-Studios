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
    private float deathCount;

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

    private int ammunition;
    private int default_ammunition = 20;
    private int ammoLevel = 1;
    private float jetpack_time;
    private float default_jetpack_time = 6;
    private int jpLevel = 1;
    private float characterSize =  25/ SpaceConquest.PPM;
    private boolean inEnemyZone = false;
    private ArrayList<Integer> killedBy = new ArrayList<Integer>();
    private Sound sound;

    Music music;

    public MainCharacter(World world,PlayScreen screen, String SpriteName){
        super(screen.getAtlas().findRegion(SpriteName));
        this.screen = screen;
        this.world = world;
        map =screen.getMap();
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/fireball.mp3"));
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
        deathCount = 0;

        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/walk.mp3"));

    }

    public void defineCharacter(){
        knapsackCount = 0;
        buffMode = false;
        updateGadgetLevel();
        BodyDef bdef = new BodyDef();
        //Array<RectangleMapObject> object = map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class);
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches(area[screen.getUserID()/(screen.getNumOfPlayers()/2)])) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(screen.getUserID()%3).getRectangle();
                last_x_coord = (rect.getX()*SpaceConquest.MAP_SCALE)/ SpaceConquest.PPM;
                last_y_coord = (rect.getY()*SpaceConquest.MAP_SCALE)/ SpaceConquest.PPM;
                bdef.position.set(last_x_coord,last_y_coord); //temp set position

            }
        }
//        bdef.position.set(150,150); //temp set position
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
//        fixture = b2body.createFixture(fdef);
    }

    public void updateGadgetLevel(){
        ammoLevel = Math.min(iron_storage/threshold,gun_powder_storage/threshold);
        jpLevel = Math.min(iron_storage/threshold,oil_storage/threshold);
        ammunition = default_ammunition+ammoLevel*20;
        jetpack_time = default_jetpack_time + jpLevel*5;
    }
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
    public void defineBuffCharacter(){
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

        if(buffMode && buffTimer >= buffoutTime){
            buffMode = false;
            buffTimer = 0;
            redefineCharacter();
        }
        if (setToDestroy ) {
            destroyed = true;
            setToDestroy = false;
            stateTime = 0;
            deathCount+=1;
            world.destroyBody(b2body);
        }
        if(destroyed) {
//            if (stateTime > (deathCount * 1.5)) {
                stateTime = 0;
                defineCharacter();
                destroyed = false;
            //}
        }else {
            last_x_coord = b2body.getPosition().x;
            last_y_coord = b2body.getPosition().y;
            setPosition(last_x_coord - getWidth() / 2, last_y_coord - getHeight() / 2);
            setRegion(getFrame(dt));
//            setScale(getCharacterScale());
            //System.out.println("My weight is " + additionalWeight);
        }

        x_value=b2body.getPosition().x - getWidth() / 2;
        y_value=b2body.getPosition().y - getHeight() / 2;
        setPosition(x_value, y_value);
        //System.out.println("My weight is " + additionalWeight);
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
        setRotation(getAngle());
    }

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

    public int getCharScore() {
        return charScore;
    }

    public void increaseKnapSack(float charWeight) {
        knapsackCount++;
        if(knapsackCount > threshold) {
            this.additionalWeight += charWeight;
            Array<Fixture> fix = b2body.getFixtureList();
            Shape shape = fix.get(0).getShape();
//            radius = defaultRadius + ((this.additionalWeight * scale * 7)) / SpaceConquest.PPM;
//            radius = defaultRadius + ((this.additionalWeight * scale * 7)) / SpaceConquest.PPM;
//            shape.setRadius((buffMode)?buffRadius:radius);

//        System.out.println(shape.getRadius());
//
//        System.out.println("charweight: "+this.additionalWeight);


            //stop user from collecting resource
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
//            setScale(getCharacterScale());
        }
    }

    public void setAdditionalWeight(int w){
        this.additionalWeight =w;
    }

    public float[] fire(){
        if(!buffMode) {
            fireCount += 1;
        }else{
            IFCount+=1;
        }
        ammunition-=1;
        float[] s = {b2body.getPosition().x,b2body.getPosition().y};
        FireBall f = new FireBall(screen, s[0], s[1], lastXPercent,
                lastYPercent, (buffMode)?buffRadius:radius, false, screen.getUserID(), buffMode,sound);

        fireballs.add(f);
//        System.out.println("ammunition left: "+ ammunition);
        return s;
    }
    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

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

//    public float getCharacterScale() {
//
//        return ((float)1+ (additionalWeight *scale));
//    }
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

//    public String[] getFireballData(){
//        String s;
//        for(FireBall f: networkFireballs){
//            String s1 ={f.getX(),f.getY(),}
//        }
//    }
    public float[] getLast_xy_coord() {
        return new float[]{last_x_coord, last_y_coord};
    }
    public float getX_value(){
        return x_value;
    }
    public float getY_value(){
        return y_value;
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
        playerHP-=(imbaOrNot?10:4);
        if (playerHP<=0){
            dead();
            playerHP=maxHp;
        }
    }
    public float getHP(){
        return playerHP;
    }

    public int[] getKnapsackInfo() {
        return new int[]{oil_count+gun_powder_count+iron_count, oil_storage,iron_storage,
                gun_powder_storage};
//        return new int[]{oil_count+gun_powder_count+iron_count, oil_count+oil_storage,iron_storage+iron_count,
//                gun_powder_storage+gun_powder_count};
    }
    public float[] getGadgetInfo() {
        return new float[]{ammunition,jetpack_time};
    }

    public int getOil_count() {
        return oil_count;
    }

    public int getGun_powder_count() {
        return gun_powder_count;
    }
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

