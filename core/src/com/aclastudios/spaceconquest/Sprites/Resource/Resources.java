package com.aclastudios.spaceconquest.Sprites.Resource;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Resources extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body body;
    protected boolean toDestroy;
    protected boolean destroyed;
    private TextureRegion resource;

    public Resources(PlayScreen screen, String resourceName, float x, float y){
        super(screen.getAtlas().findRegion(resourceName));
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        resource = new TextureRegion(getTexture(),0,0,16,16);
        setBounds(getX(),getY(),16/ SpaceConquest.PPM,16/ SpaceConquest.PPM);
        defineResources(x,y);
        toDestroy=false;
        destroyed=false;
    }

    protected abstract void defineResources(float x, float y);

    public abstract void use(MainCharacter player);

    public void update(float dt){
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public void draw(Batch batch){
        if(!destroyed){
            super.draw(batch);
        }
    }
    public void destroy(){
        toDestroy = true;
    }
    public boolean ifDestroyed(){
        return destroyed;
    }

}
