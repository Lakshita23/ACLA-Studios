package com.aclastudios.spaceconquest.Weapons;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class FireBall extends Sprite {
    PlayScreen screen;
    World world;
    Array<TextureRegion> frames;
    TextureRegion fb;
    Animation fireAnimation;
    float stateTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean enemyFire;
    float xSpd;
    float ySpd;
    float distance;
    Body b2body;
    int firerID;

    public FireBall(PlayScreen screen, float x, float y, float xSpd,float ySpd,float radius, boolean enemyFire, int firerID, boolean imba){
        this.xSpd = xSpd;
        this.ySpd = ySpd;
        this.screen = screen;
        this.world = screen.getWorld();
        this.enemyFire = enemyFire;
        this.firerID = firerID;
        this.distance = radius;
        frames = new Array<TextureRegion>();
        fb=new TextureAtlas("Mario_and_Enemies.pack").findRegion("fireball");
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(fb, i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));

        //imba refers to imbalance in gaming
        //if firer is sumo mode, fireball will be imba and sprite will be larger
        if(imba) {
            setBounds(x, y, 18 / SpaceConquest.PPM, 18 / SpaceConquest.PPM);
            defineIMBAFireBall();
        }
        else{
            setBounds(x, y, 6 / SpaceConquest.PPM, 6 / SpaceConquest.PPM);
            defineFireBall();
        }
    }

    //define normal fireball
    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + (this.xSpd *(distance))/ SpaceConquest.PPM, getY() + (this.ySpd*distance)/ SpaceConquest.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3/ SpaceConquest.PPM);
        if(enemyFire){
            fdef.filter.categoryBits = SpaceConquest.FIREBALL_BIT;
            fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.MAIN_CHARACTER_BIT;
        }
        else {
            fdef.filter.categoryBits = SpaceConquest.FRIENDLY_FIREBALL_BIT;
            fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.CHARACTER_BIT;
        }

        fdef.shape = shape;
        fdef.restitution = 1;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2((this.xSpd * 1000), (this.ySpd * 1000)));
    }

    //this method is called if firer is in sumo mode
    public void defineIMBAFireBall(){

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + (this.xSpd *(distance))/ SpaceConquest.PPM, getY() + (this.ySpd*distance)/ SpaceConquest.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(9/ SpaceConquest.PPM);
        if(enemyFire){
            fdef.filter.categoryBits = SpaceConquest.IMBA_FIREBALL_BIT;
            fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.MAIN_CHARACTER_BIT;
        }
        else {
            fdef.filter.categoryBits = SpaceConquest.FRIENDLY_FIREBALL_BIT;
            fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                    |SpaceConquest.CHARACTER_BIT;
        }

        fdef.shape = shape;
        fdef.restitution = 1;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2((this.xSpd * 1000), (this.ySpd * 1000)));
    }

    //updates the animation and position of the sprite according to the body
    public void update(float dt){
        stateTime += dt;
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
    }

    //setToDestroy and isDestroyed is used when fireball collides with a character
    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    //used to get the firer id so that server can know who does the kill
    public int getFirerID() {
        return firerID;
    }

}
