package com.aclastudios.spaceconquest.Sprites.Resource;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by Lakshita on 3/24/2016.
 */
public class GunPowder extends Resources {

    public GunPowder(PlayScreen screen, float x, float y) {
        super(screen,"gunpowder_ore", x, y);
    }

    @Override
    protected void defineResources(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x,y); //temp set position
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8);

        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.GUNPOWDER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                |SpaceConquest.MAIN_CHARACTER_BIT
                |SpaceConquest.CHARACTER_BIT
                |SpaceConquest.OBJECT_BIT; //What can the character collide with?


        //Body
        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void use(MainCharacter player) {
        destroy();
    }


    @Override
    public void update(float dt){
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

}
