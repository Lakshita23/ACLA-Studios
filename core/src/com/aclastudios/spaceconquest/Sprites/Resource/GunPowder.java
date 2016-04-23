package com.aclastudios.spaceconquest.Sprites.Resource;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

//Gunpowder resource
public class GunPowder extends Resources {

    //Initialise resource texture from atlas
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
        shape.setRadius(8/ SpaceConquest.PPM);

        //Collision Bits
        fdef.filter.categoryBits = SpaceConquest.GUNPOWDER_BIT; //Fixture Category
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT       //Collidable objects fixtures
                |SpaceConquest.MAIN_CHARACTER_BIT
                |SpaceConquest.CHARACTER_BIT
                |SpaceConquest.OBJECTIVE_BIT;


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
