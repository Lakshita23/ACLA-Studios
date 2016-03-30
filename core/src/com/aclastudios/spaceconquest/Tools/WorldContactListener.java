package com.aclastudios.spaceconquest.Tools;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.Enemy;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.Resource.Oil;
import com.aclastudios.spaceconquest.Weapons.FireBall;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    private PlayScreen screen;
    public WorldContactListener(PlayScreen screen) {
        this.screen=screen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.IRON_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.IRON_BIT)
                    ((Iron)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                else
                    ((Iron)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                //Hud.addScore(1);
                screen.increaseCharWeight(2);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.GUNPOWDER_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.GUNPOWDER_BIT)
                    ((GunPowder)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                else
                    ((GunPowder)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                //Hud.addScore(1);
                screen.increaseCharWeight(2);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.OIL_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.OIL_BIT)
                    ((Oil)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                else
                    ((Oil)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                //Hud.addScore(1);
                screen.increaseCharWeight(2);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT |SpaceConquest.STATION_BIT:
                System.out.println("inside station");
                int score =screen.depositResource();
                Hud.addScore(score);
                break;
//            case SpaceConquest.OBJECT_BIT| SpaceConquest.IRON_BIT:
//                if(fixA.getFilterData().categoryBits == SpaceConquest.IRON_BIT)
//                    ((Iron)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
//                else
//                    ((Iron)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
//                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.OBSTACLE_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.CHARACTER_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                    ((Enemy) fixB.getUserData()).dead();
                }
                else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                    ((Enemy) fixA.getUserData()).dead();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
