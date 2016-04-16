package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Weapons.FireBall;
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
    private int charWeight = 0;
    private float radius = 13/ SpaceConquest.PPM;
    private int charScore;
    private float playerHP = 20;

    private Array<FireBall> fireballs;
    private int fireCount;

    private float scale = (float) (1.0/10);

    // used for respawning the character
    private float stateTime;
    private boolean setToDestroy;
    private boolean destroyed;
    private float deathCount;

    // for animating the sprite
    private enum State { STANDING, RUNNING };
    private State currentState;
    private State previousState;
    private Animation running;
    private float stateTimer;

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
    private float jetpack_time;

    private ArrayList<Integer> killedBy = new ArrayList<Integer>();
    public MainCharacter(World world,PlayScreen screen, String SpriteName){
        super(screen.getAtlas().findRegion(SpriteName));
        this.screen = screen;
        this.world = world;
        map =screen.getMap();

        // initializing variables for animation:
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        // animation for walking
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), getRegionX() + i * 200, getRegionY(), 200, 200));
        }
        running =new Animation(0.2f, frames);

        defineCharacter();
        character = new TextureRegion(getTexture(), getRegionX() + 200, getRegionY(), 200, 200);
        setBounds(0, 0, 25/ SpaceConquest.PPM, 25/ SpaceConquest.PPM);
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
    }

    public void defineCharacter(){
        ammunition = 100;
        jetpack_time = 8;
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
        shape.setRadius(radius);
        xSpeedPercent = 0;
        ySpeedPercent = 0;
        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.MAIN_CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                |SpaceConquest.FIREBALL_BIT
                |SpaceConquest.IRON_BIT
                |SpaceConquest.GUNPOWDER_BIT
                |SpaceConquest.OIL_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.ENEMY_STATION_BIT
                |SpaceConquest.CHARACTER_BIT; //What can the character collide with?
        //Body
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
//        fixture = b2body.createFixture(fdef);
    }
    public void update(float dt){
        if (playerHP<20){
            playerHP+=0.01;
        }
        stateTime += dt;
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
            setScale(getCharacterScale());
            //System.out.println("My weight is " + charWeight);
        }

        x_value=b2body.getPosition().x - getWidth() / 2;
        y_value=b2body.getPosition().y - getHeight() / 2;
        setPosition(x_value, y_value);
        //System.out.println("My weight is " + charWeight);
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


    public int getCharWeight() {
        return charWeight;
    }

    public int getCharScore() {
        return charScore;
    }

    public void addCharWeight(float charWeight) {
        this.charWeight += charWeight;
        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        radius = (13 + (this.charWeight * scale * 7))/ SpaceConquest.PPM;
        shape.setRadius(radius);
//        System.out.println(shape.getRadius());
//
//        System.out.println("charweight: "+this.charWeight);


        //stop user from collecting resource
        if(this.charWeight>=15){
            Filter filter = fix.get(0).getFilterData();
            filter.maskBits =  SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.FIREBALL_BIT
                    |SpaceConquest.STATION_BIT
                    |SpaceConquest.ENEMY_STATION_BIT
                    |SpaceConquest.CHARACTER_BIT;
            fix.get(0).setFilterData(filter);
        }
        setScale(getCharacterScale());
    }

    public void setCharWeight(int w){
        this.charWeight=w;
    }

    public float[] fire(){
        fireCount+=1;
        ammunition-=1;
        float[] s = {b2body.getPosition().x,b2body.getPosition().y};
        FireBall f = new FireBall(screen, s[0], s[1], lastXPercent,
                lastYPercent, radius, false, screen.getUserID());
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
        charWeight = 0;

        //storing the resource and converting them into valued item
        iron_storage+=iron_count;
        gun_powder_storage+=gun_powder_count;
        oil_storage+=oil_count;
        iron_count = 0;
        gun_powder_count=0;
        oil_count = 0;
        ammunition +=  ((iron_storage>=gun_powder_storage)?gun_powder_storage:iron_storage)*15;
        jetpack_time += oil_storage * 2;
        //destroying the exhausted resource
        oil_storage=0;
        int lesserone=(iron_storage>=gun_powder_storage)?gun_powder_storage:iron_storage;
        iron_storage -= lesserone;
        gun_powder_storage -=lesserone;

        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        shape.setRadius(radius);
        //player can now collide with resource
        Filter filter = fix.get(0).getFilterData();
        filter.maskBits =  SpaceConquest.OBSTACLE_BIT
                | SpaceConquest.IRON_BIT
                |SpaceConquest.GUNPOWDER_BIT
                |SpaceConquest.OIL_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.ENEMY_STATION_BIT
                |SpaceConquest.CHARACTER_BIT
                |SpaceConquest.FIREBALL_BIT;
        fix.get(0).setFilterData(filter);
    }

    public float getCharacterScale() {

        return ((float)1+ (charWeight*scale));
    }
    public void dead(){
        iron_count = 0;
        gun_powder_count = 0;
        oil_count = 0;
        setToDestroy = true;
        radius=13/ SpaceConquest.PPM;
        this.charWeight = 0;
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
    public void reduceHP(){
        playerHP=playerHP-4;
        if (playerHP<=0){
            dead();
            playerHP=20;
        }
    }
    public float getHP(){
        return playerHP;
    }

    public int[] getKnapsackInfo() {
        return new int[]{charWeight, oil_count+oil_storage,iron_storage+iron_count,gun_powder_storage+gun_powder_count};
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
}

