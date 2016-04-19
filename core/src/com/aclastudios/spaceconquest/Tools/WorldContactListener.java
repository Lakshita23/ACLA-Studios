package com.aclastudios.spaceconquest.Tools;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.Resource.Oil;
import com.aclastudios.spaceconquest.Weapons.FireBall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    private PlayScreen screen;
    private SpaceConquest game;
    private boolean fromEnemyRegion = false;
    private boolean fromNeutralRegion = true;
    private Sound pickupSound;
    private Sound depositSound;

    public WorldContactListener(PlayScreen screen,SpaceConquest game) {
        this.screen=screen;
        this.game=game;
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pickup.wav"));
        depositSound = Gdx.audio.newSound(Gdx.files.internal("sounds/deposit.mp3"));
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        int[] forHud;
        switch (cDef){
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.IRON_BIT:
                pickupSound.play(1f);
                if(fixA.getFilterData().categoryBits == SpaceConquest.IRON_BIT) {
                    ((Iron) fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addIron_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixB.getUserData()).increaseKnapSack(1);
                }
                else {
                    ((Iron) fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addIron_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixA.getUserData()).increaseKnapSack(1);
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.GUNPOWDER_BIT:
                pickupSound.play(1f);
                if(fixA.getFilterData().categoryBits == SpaceConquest.GUNPOWDER_BIT){
                    ((GunPowder)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addGun_powder_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixB.getUserData()).increaseKnapSack(1);
                }
                else{
                    ((GunPowder)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addGun_powder_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixA.getUserData()).increaseKnapSack(1);
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.OIL_BIT:
                pickupSound.play(1f);
                if(fixA.getFilterData().categoryBits == SpaceConquest.OIL_BIT){
                    ((Oil)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addOil_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixB.getUserData()).increaseKnapSack(1);
                }
                else{
                    ((Oil)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addOil_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                    ((MainCharacter) fixA.getUserData()).increaseKnapSack(1);
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT |SpaceConquest.STATION_BIT:
                float[] gadget;
                depositSound.play(1f);
                if(fixA.getFilterData().categoryBits == SpaceConquest.MAIN_CHARACTER_BIT){
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                    gadget = ((MainCharacter) fixA.getUserData()).getGadgetInfo();
                    ((MainCharacter) fixA.getUserData()).depositResource();
                }else{

                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                    gadget = ((MainCharacter) fixB.getUserData()).getGadgetInfo();
                    ((MainCharacter) fixB.getUserData()).depositResource();
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                Hud.updateGadget((int)gadget[0],gadget[1]);
                break;
            case SpaceConquest.FRIENDLY_FIREBALL_BIT | SpaceConquest.OBSTACLE_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FRIENDLY_FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;


            case SpaceConquest.FIREBALL_BIT | SpaceConquest.OBSTACLE_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case SpaceConquest.IMBA_FIREBALL_BIT | SpaceConquest.OBSTACLE_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.IMBA_FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case SpaceConquest.FRIENDLY_FIREBALL_BIT | SpaceConquest.CHARACTER_BIT:
                System.out.println("collision with side character started");
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                }
                else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                }
                System.out.println("collision with side character ended");
                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.MAIN_CHARACTER_BIT:
                System.out.println("collision with main character started");
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT) {
                    if(((MainCharacter) fixB.getUserData()).getHP()<=4){
                        game.playServices.MessagetoParticipant(((FireBall) fixA.getUserData()).getFirerID(), "KillBonus:"+1);
                        int team = 0;
                        if (game.multiplayerSessionInfo.mId_num<screen.getNumOfPlayers()/2){
                            team=team+game.multiplayerSessionInfo.mParticipants.size()/2;
                        } else {
                            team=team-game.multiplayerSessionInfo.mParticipants.size()/2;
                        }
                        System.out.println("team from contact listener: "+team);
                        if (game.multiplayerSessionInfo.mId_num!=0) {
                            game.playServices.MessagetoServer("Serverpoints:" + team + ":" + 50);
                        } else {
                            screen.addscore(team+"",50);
                        }
                    }
                    try {
                        ((MainCharacter) fixB.getUserData()).setKilledBy(((FireBall) fixA.getUserData()).getFirerID());
                        ((FireBall) fixA.getUserData()).setToDestroy();
                        ((MainCharacter) fixB.getUserData()).takeFireballDamage(false);
                    }catch (Exception e){

                    }
                }
                else {
                    if(((MainCharacter) fixA.getUserData()).getHP()<=4){
                        game.playServices.MessagetoParticipant(((FireBall) fixB.getUserData()).getFirerID(), "KillBonus:"+1);
                        int team = 0;
                        if (game.multiplayerSessionInfo.mId_num<screen.getNumOfPlayers()/2){
                            team=team+game.multiplayerSessionInfo.mParticipants.size()/2;
                        } else {
                            team=team-game.multiplayerSessionInfo.mParticipants.size()/2;
                        }
                        if (game.multiplayerSessionInfo.mId_num!=0) {
                            game.playServices.MessagetoServer("Serverpoints:" + team + ":" + 50);
                        } else {
                            screen.addscore(team+"",50);
                        }
                    }
                    try {
                        ((MainCharacter) fixA.getUserData()).setKilledBy(((FireBall) fixB.getUserData()).getFirerID());
                        ((FireBall) fixB.getUserData()).setToDestroy();
                        ((MainCharacter) fixA.getUserData()).takeFireballDamage(false);
                    }catch (Exception e){
                    }
                }
                System.out.println("collision with main character ended");
                break;
            case SpaceConquest.IMBA_FIREBALL_BIT | SpaceConquest.MAIN_CHARACTER_BIT:
                System.out.println("collision with main character started");
                if(fixA.getFilterData().categoryBits == SpaceConquest.IMBA_FIREBALL_BIT) {
                    if(((MainCharacter) fixB.getUserData()).getHP()<=10){
                        game.playServices.MessagetoParticipant(((FireBall) fixA.getUserData()).getFirerID(), "KillBonus:"+1);
                        int team = 0;
                        if (game.multiplayerSessionInfo.mId_num<screen.getNumOfPlayers()/2){
                            team=team+game.multiplayerSessionInfo.mParticipants.size()/2;
                        } else {
                            team=team-game.multiplayerSessionInfo.mParticipants.size()/2;
                        }
                        System.out.println("team from contact listener: "+team);
                        if (game.multiplayerSessionInfo.mId_num!=0) {
                            game.playServices.MessagetoServer("Serverpoints:" + team + ":" + 50);
                        } else {
                            screen.addscore(team+"",50);
                        }
                    }
                    try {
                        ((MainCharacter) fixB.getUserData()).setKilledBy(((FireBall) fixA.getUserData()).getFirerID());
                        ((FireBall) fixA.getUserData()).setToDestroy();
                        ((MainCharacter) fixB.getUserData()).takeFireballDamage(true);
                    }catch (Exception e){
                    }
                }
                else {
                    if(((MainCharacter) fixA.getUserData()).getHP()<=10){
                        game.playServices.MessagetoParticipant(((FireBall) fixB.getUserData()).getFirerID(), "KillBonus:"+1);
                        int team = 0;
                        if (game.multiplayerSessionInfo.mId_num<screen.getNumOfPlayers()/2){
                            team=team+game.multiplayerSessionInfo.mParticipants.size()/2;
                        } else {
                            team=team-game.multiplayerSessionInfo.mParticipants.size()/2;
                        }
                        if (game.multiplayerSessionInfo.mId_num!=0) {
                            game.playServices.MessagetoServer("Serverpoints:" + team + ":" + 50);
                        } else {
                            screen.addscore(team+"",50);
                        }
                    }
                    try {
                        ((MainCharacter) fixA.getUserData()).setKilledBy(((FireBall) fixB.getUserData()).getFirerID());
                        ((FireBall) fixB.getUserData()).setToDestroy();
                        ((MainCharacter) fixA.getUserData()).takeFireballDamage(true);
                    }catch (Exception e){
                    }
                }
                System.out.println("collision with main character ended");
                break;
            case SpaceConquest.ENEMY_STATION_BIT | SpaceConquest.MAIN_CHARACTER_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.MAIN_CHARACTER_BIT){
                    ((MainCharacter) fixA.getUserData()).setInEnemyZone(true);
                }else{
                    ((MainCharacter) fixB.getUserData()).setInEnemyZone(true);
                }
//                if(hp<=1) {
//                    int team = 0;
//                    if (game.multiplayerSessionInfo.mId_num < screen.getNumOfPlayers() / 2) {
//                        team = team + game.multiplayerSessionInfo.mParticipants.size() / 2;
//                    } else {
//                        team = team - game.multiplayerSessionInfo.mParticipants.size() / 2;
//                    }
//                    System.out.println("team from contact listener: " + team);
//                    if (game.multiplayerSessionInfo.mId_num != 0) {
//                        game.playServices.MessagetoServer("Serverpoints:" + team + ":" + 50);
//                    } else {
//                        screen.addscore(team + "", 50);
//                    }
//                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef) {
            case SpaceConquest.ENEMY_STATION_BIT | SpaceConquest.MAIN_CHARACTER_BIT:
                if (fixA.getFilterData().categoryBits == SpaceConquest.MAIN_CHARACTER_BIT) {
                    ((MainCharacter) fixA.getUserData()).setInEnemyZone(false);

                } else {
                    ((MainCharacter) fixB.getUserData()).setInEnemyZone(false);
                }

                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
