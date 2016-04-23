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

//this class creates the body of the world
public class B2WorldCreator {
    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef(); //Define what the body consist of
        FixtureDef fdef = new FixtureDef(); //Define the fixture
        Body body; //The Body
        Array<Body> cuerpos = new Array<Body>();

        //creates the obstacle layer
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

        //creates your spacestation layer where you will deposit your collected resource and replenish your supply
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

            //Spacestation can collide with main character
            fdef.filter.categoryBits = SpaceConquest.STATION_BIT;
            fdef.filter.maskBits = SpaceConquest.MAIN_CHARACTER_BIT;

            body.createFixture(fdef); // Add fixture to the body
        }

        //create the enemy zone where main character's health will decrease when he collides
        for (MapObject object : map.getLayers().get(2+(screen.getUserID()/(screen.getNumOfPlayers()/2))).getObjects().getByType(PolylineMapObject.class)) {
            float vertices[] = ((PolylineMapObject) object).getPolyline()
                    .getTransformedVertices();
            for(int i = 0; i< vertices.length;i++){
                vertices[i] *= SpaceConquest.MAP_SCALE/ SpaceConquest.PPM;
            }
            ChainShape shape2 = new ChainShape();
            shape2.createChain(vertices);

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(0,0);
            body = world.createBody(bdef); // Add body to the world
            fdef.shape = shape2;
            fdef.isSensor = true;

            //enemy station can collide with main character
            fdef.filter.categoryBits = SpaceConquest.ENEMY_STATION_BIT;
            fdef.filter.maskBits = SpaceConquest.MAIN_CHARACTER_BIT;
            body.createFixture(fdef).setUserData("SpaceStation"); // Add fixture to the body
        }


    }
}


