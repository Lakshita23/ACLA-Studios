package com.aclastudios.spaceconquest.Tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HealthBar extends Actor{
    private Sprite actor;
    private TextureRegion textureRegion;
    public HealthBar(TextureRegion textureRegion, Sprite actor){
        this.textureRegion = textureRegion;
        this.actor = actor;
        setSize(actor.getWidth()/2, 8);
        setOrigin(actor.getOriginX(), actor.getOriginY());
        setPosition(actor.getX(), actor.getY());
    }

    @Override
    public void draw(Batch batch, float balance){
        setPosition(actor.getX()+actor.getWidth()/2-balance/2,actor.getY()+actor.getWidth());
//        setRotation(actor.getRotation());
        batch.draw(textureRegion,getX(),getY(),getOriginX(),getOriginY(), getWidth(),getHeight(), 1, 1, getRotation());
    }
}