package com.aclastudios.spaceconquest.Helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.ArrayList;

/**
 * Created by Lakshita on 4/5/2016.
 */
public class AssetLoader {
    //buttons
    public static Texture muteButton;
    public static Texture unmuteButton;
    public static Drawable buttonUp;
    public static Drawable buttonDown;

    public static Texture logoTexture;
    public static TextureRegion logo;
    public static Texture menuBackground;
    public static Skin touchpadSkin;
    public static Skin menuSkin;
    //styles

    //sounds
    public static Music gameMusic;
    public static Music menuMusic;
    public static Sound clickSound;
    public static Music walkSound;
    public static Music runSound;
    private static ArrayList<Music> musicBox;
    private static ArrayList<Sound> soundBox;
    public static float VOLUME;

    public static void loadTutorialScreen(){
        //TODO
    }

    public static void initiate(){
        VOLUME = 1;
        musicBox = new ArrayList<Music>();
        soundBox = new ArrayList<Sound>();
    }

    public static void loadALL(){
        loadLogo();
        loadFont();
        loadMenuScreen();
        loadMenuSfx();
    }

    public static void loadLogo() {
        logoTexture = new Texture(Gdx.files.internal("touchpad/healthbar.png"));
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        logo = new TextureRegion(logoTexture);

    }

    public static void loadFont() {
//        crimesFont36 = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
//        crimesFont48 = new BitmapFont(Gdx.files.internal("Fonts/crimesFont48.fnt"));
//        crimesFont36Time = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
//        crimesFont36Time.setScale(0.7f, 0.7f);
//        crimesFont36Sync = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
//        crimesFont36Sync.setScale(Gdx.graphics.getWidth() / gameWidth);
//        crimesFont36Settings = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
//        crimesFont36Black = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36Black.fnt"));
//        basker32Message = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
    }

    public static void loadMenuScreen() {
      //  menuBackground = new Texture(Gdx.files.internal("basic/menu.png"));
        // Create new skin for menu screen
//        menuSkin = new Skin();
//        // Set menu font
//      //  menuSkin.add("crimesFont36", crimesFont36);
//      //  menuSkin.add("crimesFont48", crimesFont48);
//        // Set menu buttons
//        menuSkin.add("buttonUp", new Texture("basic/button_up.png"));
//        menuSkin.add("buttonDown", new Texture("basic/button_down.png"));
        // Create Text button Style
//        normal = new TextButton.TextButtonStyle();
//        normal.font = menuSkin.getFont("crimesFont36");
//        normal.font.setScale(0.65f, 0.65f);
//        normal.up = menuSkin.getDrawable("buttonUp");
//        normal.down = menuSkin.getDrawable("buttonDown");
//        normal.pressedOffsetY = -1;

//        muteButton = new Texture(Gdx.files.internal("menu_screen/muteButton.png"));
//        unmuteButton = new Texture(Gdx.files.internal("menu_screen/unmuteButton.png"));

        // MAP
      //  tiledMap = new TmxMapLoader().load("map/mansion2.tmx");
    }

    public static void loadMenuSfx() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("bgm/MenuScreen Music.mp3"));
        musicBox.add(menuMusic);
        menuMusic.setLooping(true);
//        soundBox.add(clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.mp3")));
//        clickSound.setLooping(0, false);
//
//        musicBox.add(walkSound = Gdx.audio.newMusic(Gdx.files.internal("sfx/walking.mp3")));
//
//        musicBox.add(runSound = Gdx.audio.newMusic(Gdx.files.internal("sfx/running.mp3")));
    }

    public static void muteSFX() {
        for (Sound s : soundBox) {
            s.setVolume(0, 0f);
        }
        for (Music m : musicBox) {
            m.setVolume(0f);
        }
        VOLUME = 0f;
    }

    public static void unmuteSFX() {
        for (Sound s : soundBox) {
            s.setVolume(0, 1f);
        }
        for (Music m : musicBox) {
            m.setVolume(1f);
        }
        VOLUME = 1f;
    }

    public static void dispose(){
        try {
            muteButton.dispose();
            unmuteButton.dispose();
            menuSkin.dispose();
            logoTexture.dispose();
            touchpadSkin.dispose();
            menuBackground.dispose();
//            score_texture.dispose();
//            score_background_texture.dispose();
//            pause_main.dispose();
//            cooldownTexture.dispose();
//            time.dispose();
//            weapon_parts_counter.dispose();
//            tiledMap.dispose();
//            emptySlot.dispose();
//            settings_button_tex.dispose();
//            civ_weapon_bat_tex.dispose();
//            civ_item_tex.dispose();
//            civ_dash_tex.dispose();
//            mur_weapon_tex.dispose();
//            mur_item_tex.dispose();
//            mur_swap_C_tex.dispose();
//            mur_swap_M_tex.dispose();
//            civilianTexture0.dispose();
//            civilianTexture1.dispose();
//            civilianTexture2.dispose();
//            civ_dead_lines.dispose();
//            ghost_float.dispose();
//            plantedTrapTexture.dispose();
//            restingTrapTexture.dispose();
//            disarmTrapSpriteTexture.dispose();
//            batSpriteTexture.dispose();
//            knifeSpriteTexture.dispose();
//            shotgunPartTexture.dispose();
//            haunt_tex.dispose();
//            ghostHauntT.dispose();
            walkSound.dispose();
            runSound.dispose();
//            civLoad.dispose();
//            murLoad.dispose();
//            civTut.dispose();
//            murTut.dispose();
//            tutorialP1.dispose();
//            hudOverlay.dispose();
//            hudTutorial.dispose();
//            screenTutorial.dispose();
//            mapTutorial.dispose();
//            civButton.dispose();
//            civButtonDown.dispose();
//            murButton.dispose();
//            murButtonDown.dispose();
//            civ_char0.dispose();
//            civ_char1.dispose();
//            civ_char2.dispose();
//            civ_char3.dispose();
//            mur_char.dispose();
//            civCharTut.dispose();
//            murCharTut.dispose();
//            itemTutBegin.dispose();
//            abilityTutCiv.dispose();
//            abilityTutMur.dispose();
//            weaponTutCiv.dispose();
//            weaponTutMur.dispose();
//            itemTutCiv.dispose();
//            itemTutMur.dispose();
//            shotgunTut.dispose();
//            shotgunTutMur.dispose();
//            nextButtonToMenu.dispose();
//            ghostCharTut.dispose();
            // Dispose Sound
            menuMusic.dispose();
            clickSound.dispose();
            walkSound.dispose();
            runSound.dispose();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void disposeSFX(){
        try {
//            plantTrapSound.dispose();
//            knifeStabSound.dispose();
//            batSwingSound.dispose();
//            disarmTrapSound.dispose();
//            pickUpItemSound.dispose();
            gameMusic.dispose();
//            shotgunBlastSound.dispose();
//            knifeThrustSound.dispose();
//            trapDisarmedSound.dispose();
//            trappedSound.dispose();
//            batHitSound.dispose();
//            lightningSound.dispose();
//            obstacleSound1.dispose();
//            obstacleSound2.dispose();
//            obstacleSound3.dispose();
//            obstacleSoundmd.dispose();
//            hauntSound1.dispose();
//            hauntSound2.dispose();
//            hauntSound3.dispose();
//            characterDeathSound.dispose();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
