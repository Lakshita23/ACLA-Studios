package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by User on 17/3/2016.
 */
public class Enemy extends Sprite{
    //private float xSpeed,ySpeed;
    public final String[] area = {"Team1Spawn","Team2Spawn"};
    private int x, y;
    private float angle;
    private int enemyID;
    public World world;
    TiledMap map;
    public Body b2body;
    protected Fixture fixture;
    private TextureRegion character;
    float spawnX;
    float spawnY;
    private float stateTime;
    private boolean setToDestroy;
    private boolean destroyed;
    private float deathCount;

    //private int charWeight;
    //private int charScore;
    public Enemy(World world, PlayScreen screen,int ID){
        super(screen.getAtlas().findRegion("turtle"));
        this.world = world;
        this.enemyID = ID;
        map =screen.getMap();
        defineCharacter();
        character = new TextureRegion(getTexture(),338,26,16,24);
        setBounds(0, 0, 16, 24);
        setRegion(character);
        stateTime = 0;
        setToDestroy = false;
        destroyed = false;
        deathCount = 0;
        x=0;
        y=0;
        angle=0;
    }

    public void defineCharacter(){
        BodyDef bdef = new BodyDef();
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches(area[enemyID])) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(0).getRectangle();
                spawnX = rect.getX()*SpaceConquest.MAP_SCALE;
                spawnY = rect.getY()*SpaceConquest.MAP_SCALE;
                bdef.position.set(spawnX, spawnY); //temp set position

            }
        }
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20);
        //xSpeed = 0;
        //ySpeed = 0;

        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                | SpaceConquest.IRON_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.OBJECT_BIT
                |SpaceConquest.MAIN_CHARACTER_BIT
                |SpaceConquest.FIREBALL_BIT; //What can the character collide with?

        //Body
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

//        fixture = b2body.createFixture(fdef);
    }

    @Override
    public void draw(Batch batch) {
        if(!this.isDestroyed())
             super.draw(batch);
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy ) {
            System.out.println("destroying");
            world.destroyBody(b2body);
            destroyed = true;
            setToDestroy = false;
            stateTime = 0;
            deathCount+=1;
        }
        if(destroyed){
            if(stateTime>(deathCount*1.5)){
                stateTime = 0;
                destroyed = false;
                defineCharacter();
            }
        }else {
            b2body.setTransform(0,0,0);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            //System.out.println("My weight is " + charWeight);
        }
    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }
    public void dead(){
        setToDestroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
    public void updateEnemy(int x,int y, float angle){
        this.x=x;
        this.y=y;
        this.angle = angle;
    }
    /*
    public float getySpeed() {
        return ySpeed;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public Integer getCharWeight() {
        return charWeight;
    }

    public int getCharScore() {
        return charScore;
    }

    public void addCharWeight(int charWeight) {
        this.charWeight += charWeight;
    }

    public void setCharWeight(int w){
        this.charWeight=w;
    }
    */
}
