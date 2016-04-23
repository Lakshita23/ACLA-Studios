package com.aclastudios.spaceconquest.Tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HealthBar extends Actor{

    private Sprite actor;
    private TextureRegion textureRegion;

    //Create health bar on top of the actor
    public HealthBar(TextureRegion textureRegion, Sprite actor){
        this.textureRegion = textureRegion;
        this.actor = actor;
        setSize(actor.getWidth()/2, 8);
        setOrigin(actor.getOriginX(), actor.getOriginY());
        setPosition(actor.getX(), actor.getY());
    }

    @Override
    public void draw(Batch batch, float balance){
        //Set healthbar on top of the actor
        setPosition(actor.getX()+actor.getWidth()/2-balance/2,actor.getY()+actor.getWidth());
        //Draw health bar
        batch.draw(textureRegion,getX(),getY(),getOriginX(),getOriginY(), getWidth(),getHeight(), 1, 1, getRotation());
    }
}