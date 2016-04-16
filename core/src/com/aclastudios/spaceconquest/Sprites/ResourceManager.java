package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.Resource.Oil;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class ResourceManager {

    private int iron_count;
    private ArrayList<Iron> iron_array;
    private int gunpowder_count;
    private ArrayList<GunPowder> gunpowder_array;
    private int oil_count;
    private ArrayList<Oil> oil_array;
    PlayScreen screen;
    private int userID;
    private SpaceConquest game;
    private String allres;
    float x;
    float y;
    float width;
    float height;

    public ResourceManager(PlayScreen screen, SpaceConquest game, int userID, float x, float y, float width, float height){
        this.game = game;
        this.screen = screen;
        iron_count=0;
        iron_array=new ArrayList<Iron>();
        gunpowder_count=0;
        gunpowder_array=new ArrayList<GunPowder>();
        oil_count = 0;
        oil_array = new ArrayList<Oil>();
        this.userID = userID;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public void getResourceString(String resources){
        allres = resources;
        System.out.println("Broadcast msg: "+allres);
    }

    public void generateResources(){
        if (userID==0){
            Random rand = new Random();
            while (iron_count<7)
                generateIron(rand);
            while (gunpowder_count<7)
                generateGunPowder(rand);
            while (oil_count<7)
                generateOil(rand);
            game.playServices.BroadcastMessage("Resources:" + coordinatesR());

        }
        else{
            try {
                System.out.println("in generate resources for player");
                System.out.println("Allres: "+allres);
                String[] igo = allres.split("R");
                String[] irons = igo[1].split(",");
                String[] gunps = igo[2].split(",");
                String[] oils = igo[3].split(",");

                for (int i = 0; i < irons.length; i++) {
                    addIron(Float.parseFloat(irons[i].split(" ")[0]),Float.parseFloat(irons[i].split(" ")[1]));
                }
                for (int i = 0; i < gunps.length; i++) {
                    addGunPowder(Float.parseFloat(gunps[i].split(" ")[0]), Float.parseFloat(gunps[i].split(" ")[1]));
                }
                for (int i = 0; i < oils.length; i++) {
                    addOil(Float.parseFloat(oils[i].split(" ")[0]), Float.parseFloat(oils[i].split(" ")[1]));
                }
            }catch (Exception e){
                System.out.println("Check resource if error: ");
                e.printStackTrace();
            }
        }

    }
    public void addIron(float xc, float yc){
        try {
            System.out.println("creating iron");
            Iron iron = new Iron(screen, xc, yc);
            System.out.println("iron added");
            iron_array.add(iron);
            System.out.println("iron added in resource manager");
            iron_count++;
        }catch (Exception e){
            System.out.println("problem in adding iron");
            e.printStackTrace();
        }
    }
    public void addGunPowder(float xc, float yc){
        try{
            System.out.println("creating GP");
            GunPowder gp = new GunPowder(screen, xc, yc);
            System.out.println("gunpowder added");
            gunpowder_array.add(gp);
            System.out.println("gunpowder added in resource manager");
            gunpowder_count++;
        }catch (Exception e){
            System.out.println("problem in adding gunpowder");
            e.printStackTrace();
        }
    }
    public void addOil(float xc, float yc){
        try{
            System.out.println("creating oil");
            Oil oil = new Oil(screen, xc, yc);
            System.out.println("oil added");
            oil_array.add(oil);
            System.out.println("oil added in resource manager");
            oil_count++;
        }catch (Exception e){
            System.out.println("problem in adding oil");
            e.printStackTrace();
        }
    }
    private void generateIron(Random rand){
        Iron iron = new Iron(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        System.out.println("IRON: "+userID+": " + iron.getX() + ", " + iron.getY());
        iron_array.add(iron);
        iron_count++;
    }
    private void generateGunPowder(Random rand){
        GunPowder gunpd = new GunPowder(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        System.out.println("GUNPD: " + gunpd.getX() + ", " + gunpd.getY());
        gunpowder_array.add(gunpd);
        gunpowder_count++;
    }
    private void generateOil(Random rand){
        Oil oil = new Oil(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        System.out.println("OIL: " + oil.getX() + ", " + oil.getY());
        oil_array.add(oil);
        oil_count++;
    }

    public void updateIron(float dt){
        for (int n=0; n<iron_array.size();n++){
            Iron I = iron_array.get(n);
            I.update(dt);
            if (I.ifDestroyed()){
                iron_array.remove(n);
                iron_count--;
                game.playServices.BroadcastMessage("Delete:Iron:" + n + ":" +dt);
                genIron();
            }
        }
    }
    public void genIron(){
        Random rand = new Random();
        generateIron(rand);
        Iron iron = iron_array.get(iron_count-1);
        game.playServices.BroadcastMessage("Generate:Iron:"+iron.getX()+":"+iron.getY());
    }
    public void delIron(int n, float dt){
        try {
            System.out.println("deleting iron");
            Iron I = iron_array.get(n);
            System.out.println("deleting iron2");
            I.destroy();
            System.out.println("deleted iron");
            I.update(dt);
            iron_array.remove(n);
            System.out.println("deleted iron from iron array");
            iron_count--;
        }catch (Exception e){
            System.out.println("delete iron got problem");
        }
    }
    public void updateGunPowder(float dt){
        for (int n=0; n<gunpowder_array.size();n++){
            GunPowder gp = gunpowder_array.get(n);
            gp.update(dt);
            if (gp.ifDestroyed()){
                gunpowder_array.remove(n);
                gunpowder_count--;
                game.playServices.BroadcastMessage("Delete:GunPowder:" + n + ":" +dt);
                genGunPowder();
            }
        }
    }
    public void genGunPowder(){
        Random rand = new Random();
        generateGunPowder(rand);
        GunPowder gp = gunpowder_array.get(gunpowder_count-1);
        game.playServices.BroadcastMessage("Generate:GunPowder:"+gp.getX()+":"+gp.getY());
    }
    public void delGunPowder(int n, float dt){

        System.out.println("deleting GP");
        GunPowder gp = gunpowder_array.get(n);
        gp.destroy();
        System.out.println("deleted GP");
        gp.update(dt);
        System.out.println("removing GP");
        gunpowder_array.remove(n);
        System.out.println("removed GP");
        gunpowder_count--;

    }
    public void updateOil(float dt){
        for (int n=0; n<oil_array.size();n++){
            Oil ol = oil_array.get(n);
            ol.update(dt);
            if (ol.ifDestroyed()){
                oil_array.remove(n);
                oil_count--;
                game.playServices.BroadcastMessage("Delete:Oil:" + n + ":" +dt);
                genOil();
            }
        }
    }
    public void delOil(int n, float dt){
        Oil oil = oil_array.get(n);
        oil.destroy();
        oil.update(dt);
        oil_array.remove(n);
        oil_count--;
    }
    public void genOil(){
        Random rand = new Random();
        generateOil(rand);
        Oil oil = oil_array.get(oil_count-1);
        game.playServices.BroadcastMessage("Generate:Oil:"+oil.getX()+":"+oil.getY());
    }

    public String coordinatesR (){
        String iron = "R";
        for (int i=0;i<iron_array.size();i++){
            iron += iron_array.get(i).getX() + " " + iron_array.get(i).getY() + ",";    // Ri- x1 coord : y1 coord , x2 coord : y2 coord , so on
        }
        String gunpowder = "R";
        for (int i=0;i<gunpowder_array.size();i++){
            gunpowder += gunpowder_array.get(i).getX() + " " + gunpowder_array.get(i).getY() + ",";    // g- x1 coord : y1 coord , x2 coord : y2 coord , so on
        }
        String oil = "R";
        for (int i=0;i<oil_array.size();i++){
            oil += oil_array.get(i).getX() + " " + oil_array.get(i).getY() + ",";    // o- x1 coord : y1 coord , x2 coord : y2 coord , so on
        }

        String resourceCoodinates = iron + gunpowder + oil;

        System.out.println("Resources:"+resourceCoodinates);
        return resourceCoodinates;
    }

}
