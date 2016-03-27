package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.Resource.Oil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lakshita on 3/26/2016.
 */
public class ResourceManager {

    private int iron_count;
    private ArrayList<Iron> iron_array;
    private int gunpowder_count;
    private ArrayList<GunPowder> gunpowder_array;
    private int oil_count;
    private ArrayList<Oil> oil_array;
    PlayScreen screen;

    public ResourceManager(PlayScreen screen){
        this.screen = screen;
        iron_count=0;
        iron_array=new ArrayList<Iron>();
        gunpowder_count=0;
        gunpowder_array=new ArrayList<GunPowder>();
        oil_count = 0;
        oil_array = new ArrayList<Oil>();
    }

    public int getIron_count() {
        return iron_count;
    }

    public int getGunpowder_count() {
        return gunpowder_count;
    }

    public int getOil_count() {
        return oil_count;
    }

    public Iron getIron_array(int i) {
        return iron_array.get(i);
    }

    public GunPowder getGunpowder_array(int i) {
        return gunpowder_array.get(i);
    }

    public Oil getOil_array(int i){
        return oil_array.get(i);
    }

    public void generateResources(float x, float y, float width, float height){
        Random rand = new Random();
        Iron iron = new Iron(screen, (int)((rand.nextInt((int) width) + x)*SpaceConquest.MAP_SCALE) , (int)((rand.nextInt((int)(height*SpaceConquest.MAP_SCALE))+y)*SpaceConquest.MAP_SCALE));
        iron_array.add(iron);
        iron_count++;
        GunPowder gunpd = new GunPowder(screen, (int)((rand.nextInt((int) width) + x)*SpaceConquest.MAP_SCALE) , (int)((rand.nextInt((int)(height*SpaceConquest.MAP_SCALE))+y)*SpaceConquest.MAP_SCALE));
        gunpowder_array.add(gunpd);
        gunpowder_count++;
        Oil oil = new Oil(screen, (int)((rand.nextInt((int) width) + x)*SpaceConquest.MAP_SCALE) , (int)((rand.nextInt((int)(height*SpaceConquest.MAP_SCALE))+y)*SpaceConquest.MAP_SCALE));
        oil_array.add(oil);
        oil_count++;
    }

    public void updateIron(float dt){
        for (int n=0; n<iron_array.size();n++){
            Iron I = iron_array.get(n);
            I.update(dt);
            if (I.ifDestroyed()){
                System.out.println("iron");
                iron_array.remove(n);
                iron_count--;
            }
        }
    }
    public void updateGunPowder(float dt){
        for (int n=0; n<gunpowder_array.size();n++){
            GunPowder gp = gunpowder_array.get(n);
            gp.update(dt);
            if (gp.ifDestroyed()){
                System.out.println("gp");
                gunpowder_array.remove(n);
                gunpowder_count--;
            }
        }
    }
    public void updateOil(float dt){
        for (int n=0; n<oil_array.size();n++){
            Oil ol = oil_array.get(n);
            ol.update(dt);
            if (ol.ifDestroyed()){
                System.out.println("oil");
                oil_array.remove(n);
                oil_count--;
            }
        }
    }

//    public String[] coordinatesR (){
//        String iron = "Ri";
//
//    }

}