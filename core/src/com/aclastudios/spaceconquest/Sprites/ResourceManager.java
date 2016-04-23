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

/*Handles resource spawning, deleting, updating.
Also sends and handles broadcast messages to generate, update and delete resources.
 */
public class ResourceManager {

    PlayScreen screen;
    //player userID determines methods to be used
    private int userID;
    private SpaceConquest game;
    //Resource counts and arrays to store all resource objects
    private int iron_count;
    private ArrayList<Iron> iron_array;
    private int gunpowder_count;
    private ArrayList<GunPowder> gunpowder_array;
    private int oil_count;
    private ArrayList<Oil> oil_array;

    private String allres;
    float x;
    float y;
    float width;
    float height;

    public ResourceManager(PlayScreen screen, SpaceConquest game, int userID, float x, float y, float width, float height){
        this.game = game;
        this.screen = screen;
        //Initialise resource counts and arrays
        iron_count=0;
        iron_array=new ArrayList<Iron>();
        gunpowder_count=0;
        gunpowder_array=new ArrayList<GunPowder>();
        oil_count = 0;
        oil_array = new ArrayList<Oil>();
        //Set player id to determine if player is server or client
        this.userID = userID;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //getter functions
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

    //Method handles resource generation for all clients
    public void getResourceString(String resources){
        allres = resources;
    }

    //Broadcast all resources' coordinates incase of failure on client's request
    public void broadcastResources(){
        game.playServices.BroadcastMessage("Resources:" + coordinatesR());
    }
    //Generates iron, gunpowder and oil in the spawning area
    public void generateResources(){
        if (userID==0){ //server generates all resources initially
            Random rand = new Random();
            while (iron_count<7)
                generateIron(rand);
            while (gunpowder_count<7)
                generateGunPowder(rand);
            while (oil_count<7)
                generateOil(rand);
            broadcastResources();

        }
        else{   //clients generate resources initially based on server's broadcast of all resources.
            try {

                if (allres.length()<21){
                    //request for resend of resources from server in case server broadcast is faulty
                    game.playServices.BroadcastMessage("ResendR:R");
                }
                String[] igo = allres.split("R");
                String[] irons = igo[1].split(",");
                String[] gunps = igo[2].split(",");
                String[] oils = igo[3].split(",");
                //add all resources and render
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
            //create iron from coordinates given by server
            Iron iron = new Iron(screen, xc, yc);
            iron_array.add(iron);
            iron_count++;
        }catch (Exception e){
            System.out.println("problem in adding iron");
            e.printStackTrace();
        }
    }
    public void addGunPowder(float xc, float yc){
        try{
            //create gunpowder from coordinates given by server
            GunPowder gp = new GunPowder(screen, xc, yc);
            gunpowder_array.add(gp);
            gunpowder_count++;
        }catch (Exception e){
            System.out.println("problem in adding gunpowder");
            e.printStackTrace();
        }
    }
    public void addOil(float xc, float yc){
        try{
            //create oil from coordinates given by server
            Oil oil = new Oil(screen, xc, yc);
            oil_array.add(oil);
            oil_count++;
        }catch (Exception e){
            System.out.println("problem in adding oil");
            e.printStackTrace();
        }
    }

    //Generate new resources
    private void generateIron(Random rand){
        Iron iron = new Iron(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        iron_array.add(iron);
        iron_count++;
    }
    private void generateGunPowder(Random rand){
        GunPowder gunpd = new GunPowder(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        gunpowder_array.add(gunpd);
        gunpowder_count++;
    }
    private void generateOil(Random rand){
        Oil oil = new Oil(screen, (int) ((rand.nextInt((int) width) + x) * SpaceConquest.MAP_SCALE), (int) ((rand.nextInt((int) (height * SpaceConquest.MAP_SCALE)) + y) * SpaceConquest.MAP_SCALE));
        oil_array.add(oil);
        oil_count++;
    }

    //Update resources
    public void updateIron(float dt){
        for (int n=0; n<iron_array.size();n++){     //update all
            Iron I = iron_array.get(n);
            I.update(dt);
            if (I.ifDestroyed()){       //check if destroyed
                iron_array.remove(n);   //remove destroyed resource from array and decrement count
                iron_count--;
                //broadcast to all players about destroyed resource
                game.playServices.BroadcastMessage("Delete:Iron:" + n + ":" +dt);
                //generate new resource as 1 resource was destroyed
                genIron();
            }
        }
    }
    public void genIron(){  //generate 1 new resource
        Random rand = new Random();
        generateIron(rand);
        Iron iron = iron_array.get(iron_count-1);
        //Broadcast to all players about the generated resource
        game.playServices.BroadcastMessage("Generate:Iron:"+iron.getX()+":"+iron.getY());
    }

    //On receiving broadcast, remove the specific resource
    public void delIron(int n, float dt){
        try {
            Iron I = iron_array.get(n);
            I.destroy();
            I.update(dt);
            iron_array.remove(n);
            iron_count--;
        }catch (Exception e){
            System.out.println("delete iron got problem");
            e.printStackTrace();
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
        GunPowder gp = gunpowder_array.get(n);
        gp.destroy();
        gp.update(dt);
        gunpowder_array.remove(n);
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

    //Sends coordinates of all currently spawned resources
    public String coordinatesR(){
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

        return resourceCoodinates;
    }

}
