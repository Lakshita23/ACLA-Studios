package com.aclastudios.spaceconquest.Weapons;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.Space;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
    Body b2body;
    int id;
    public FireBall(PlayScreen screen, float x, float y, float xSpd,float ySpd, boolean enemyFire, int id){
        this.xSpd = xSpd;
        this.ySpd = ySpd;
        this.screen = screen;
        this.world = screen.getWorld();
        this.enemyFire = enemyFire;
        this.id=id;
        frames = new Array<TextureRegion>();
        fb=new TextureAtlas("Mario_and_Enemies.pack").findRegion("fireball");
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(fb, i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));
        setBounds(x, y, 6, 6);
        defineFireBall();
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + (this.xSpd), getY() + (this.ySpd));
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;
        //if(!world.isLocked())
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 );
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
//        short characterBit = (this.enemyFire)?SpaceConquest.MAIN_CHARACTER_BIT:SpaceConquest.CHARACTER_BIT;
//        fdef.filter.categoryBits = SpaceConquest.FIREBALL_BIT;
//        fdef.filter.maskBits = (short) (SpaceConquest.OBSTACLE_BIT |characterBit);

        fdef.shape = shape;
        fdef.restitution = 1;
        //fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2((this.xSpd * 1000), (this.ySpd * 1000)));
//        System.out.println(b2body.getLinearVelocity().x + "y speed is "+b2body.getLinearVelocity().y);
        //b2body.setLinearVelocity(new Vector2(fireRight ? 2 : -200, 200));
    }

    public void update(float dt){
        stateTime += dt;
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);

            destroyed = true;
        }
//        if(b2body.getLinearVelocity().y > 2f)
//            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 100f);
//        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
//            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public int getid() {
        return id;
    }
}
