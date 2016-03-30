package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Weapons.FireBall;
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


public class MainCharacter extends Sprite {
    public final String[] area = {"Team1Spawn","Team2Spawn"};
    private float xSpeed,ySpeed;
    public World world;
    public Body b2body;
    private PlayScreen screen;
    TiledMap map;
    protected Fixture fixture;
    private TextureRegion character;
    private int charWeight = 0;
    private float radius = 8;
    private int charScore;
    private Array<FireBall> fireballs;
    private float scale = (float) (1.0/20);

    public MainCharacter(World world,PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.screen = screen;
        this.world = world;
        map =screen.getMap();
        defineCharacter();
        character = new TextureRegion(getTexture(),0,8,16,16);
        setBounds(0,0, 16, 16);
        setRegion(character);
        fireballs = new Array<FireBall>();

    }

    public void defineCharacter(){
        BodyDef bdef = new BodyDef();
        //Array<RectangleMapObject> object = map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class);
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches(area[screen.getUserID()])) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(0).getRectangle();
                bdef.position.set(rect.getX()*SpaceConquest.MAP_SCALE, rect.getY()*SpaceConquest.MAP_SCALE); //temp set position

            }
        }
//        bdef.position.set(150,150); //temp set position
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        xSpeed = 0;
        ySpeed = 0;
        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.MAIN_CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                | SpaceConquest.IRON_BIT
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
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //System.out.println("My weight is " + charWeight);
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

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
        shape.setRadius( radius + (this.charWeight*scale*5));
        System.out.println(shape.getRadius());

        System.out.println("charweight: "+this.charWeight);


        //stop user from collecting resource
        if(this.charWeight>=10){
            Filter filter = fix.get(0).getFilterData();
            filter.maskBits =  SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.STATION_BIT
                    |SpaceConquest.CHARACTER_BIT;
            fix.get(0).setFilterData(filter);
        }

    }

    public void setCharWeight(int w){
        this.charWeight=w;
    }

    public void fire(float xSpd, float ySpd){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, xSpd, ySpd));
    }
    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

    public void depositResource() {
        charWeight = 0;
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
                |SpaceConquest.CHARACTER_BIT;
        fix.get(0).setFilterData(filter);
    }

    public float getCharacterScale() {

        return ((float)1+ (charWeight*scale));
    }
}
