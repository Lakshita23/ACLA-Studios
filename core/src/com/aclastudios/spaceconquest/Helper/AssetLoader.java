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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

/**
 * Created by Lakshita on 4/5/2016.
 */
public class AssetLoader {

    public static String[] images;

    public static void loadTutorialScreen(){
        images = new String[5];
        images[0] = new String("tutorial/img1.png");
        images[1] = new String("tutorial/img2.png");
        images[2] = new String("tutorial/img3.png");
        images[3] = new String("tutorial/img4.png");
        images[4] = new String("tutorial/img5.png");
    }
}
