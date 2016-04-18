package com.aclastudios.spaceconquest.Tools;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;

public class B2WorldCreator {
    public final String[] area = {"Left Spaceship","Right Spaceship","Left Deposit Point", "Right Deposit Point"};
    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //Temp create here first but later we will move this to the individual class
        BodyDef bdef = new BodyDef(); //Define what the body consist of
        PolygonShape shape = new PolygonShape(); //Shape for the fixture
        FixtureDef fdef = new FixtureDef(); //Define the fixture
        Body body; //The Body
        Array<Body> cuerpos = new Array<Body>();
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(PolylineMapObject.class)) {

            float vertices[] = ((PolylineMapObject) object).getPolyline()
                    .getTransformedVertices();
            for(int i = 0; i< vertices.length;i++){
                vertices[i] *= SpaceConquest.MAP_SCALE/ SpaceConquest.PPM;
            }
            ChainShape shape2 = new ChainShape();
            shape2.createChain(vertices);
            bdef.position.set(0, 0);
            cuerpos.add(world.createBody(bdef));
            cuerpos.get(cuerpos.size - 1).createFixture(shape2, 0);
        }
        for (MapObject object : map.getLayers().get(4+(screen.getUserID()/(screen.getNumOfPlayers()/2))).getObjects().getByType(PolylineMapObject.class)) {
            float vertices[] = ((PolylineMapObject) object).getPolyline()
                    .getTransformedVertices();
            for(int i = 0; i< vertices.length;i++){
                vertices[i] *= SpaceConquest.MAP_SCALE/ SpaceConquest.PPM;
            }
            ChainShape shape2 = new ChainShape();
            shape2.createChain(vertices);

            bdef.type = BodyDef.BodyType.StaticBody;
            //DynamicBody affected by gravity forces and velocity
            //StaticBody dont move, not affected by anything
            //KinematicBody Not affected by gravity but affected by forces and velocity
            bdef.position.set(0,0);
            body = world.createBody(bdef); // Add body to the world
            fdef.shape = shape2;
            //fdef.isSensor = true;
            fdef.filter.categoryBits = SpaceConquest.STATION_BIT;
            fdef.filter.maskBits = SpaceConquest.MAIN_CHARACTER_BIT;
            body.createFixture(fdef).setUserData("SpaceStation"); // Add fixture to the body
        }
        for (MapObject object : map.getLayers().get(2+(screen.getUserID()/(screen.getNumOfPlayers()/2))).getObjects().getByType(PolylineMapObject.class)) {
            float vertices[] = ((PolylineMapObject) object).getPolyline()
                    .getTransformedVertices();
            for(int i = 0; i< vertices.length;i++){
                vertices[i] *= SpaceConquest.MAP_SCALE/ SpaceConquest.PPM;
            }
            ChainShape shape2 = new ChainShape();
            shape2.createChain(vertices);

            bdef.type = BodyDef.BodyType.StaticBody;
            //DynamicBody affected by gravity forces and velocity
            //StaticBody dont move, not affected by anything
            //KinematicBody Not affected by gravity but affected by forces and velocity
            bdef.position.set(0,0);
            body = world.createBody(bdef); // Add body to the world
            fdef.shape = shape2;
            //fdef.isSensor = true;
            fdef.filter.categoryBits = SpaceConquest.ENEMY_STATION_BIT;
            fdef.filter.maskBits = SpaceConquest.MAIN_CHARACTER_BIT;
            body.createFixture(fdef).setUserData("SpaceStation"); // Add fixture to the body
        }


    }
}


