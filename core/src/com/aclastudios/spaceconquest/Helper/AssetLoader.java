package com.aclastudios.spaceconquest.Helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AssetLoader {

    //STYLES
    public static TextButtonStyle style;
    public static LabelStyle labelStyle;
    public static ImageButtonStyle imgStyleNext;
    public static ImageButtonStyle imgStylePrev;

    //BACKGROUNDS
    public static Texture background;
    public static Texture gameOverbg;

    //IMAGES
    public static String[] images;

    public static void loadStyles(){
        style = new TextButtonStyle();
        style.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt"));
        style.font.setColor(Color.BLUE);
        style.font.getData().setScale(0.2f, 0.2f);
        style.up= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-notPressed.png")));
        style.down= new TextureRegionDrawable(new TextureRegion(new Texture("button/Button-Pressed.png")));

        labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/spaceAge.fnt"));
        labelStyle.font.setColor(Color.BLUE);
        labelStyle.font.getData().setScale(0.3f, 0.3f);

        imgStyleNext = new ImageButton.ImageButtonStyle();
        imgStyleNext.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/next-notpressed.png")));
        imgStyleNext.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/next-pressed.png")));

        imgStylePrev = new ImageButton.ImageButtonStyle();
        imgStylePrev.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/back-notpressed.png")));
        imgStylePrev.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/back-pressed.png")));
    }
    public static void loadbackgrounds(){
        background = new Texture("screens/Screen.png");
        gameOverbg = new Texture("gameover3.png");
    }


    //LOAD IMAGES
    public static void loadTutorialScreen(){
        images = new String[6];
        images[0] = new String("tutorial/1.png");
        images[1] = new String("tutorial/2.png");
        images[2] = new String("tutorial/3.png");
        images[3] = new String("tutorial/4.png");
        images[4] = new String("tutorial/5.png");
        images[5] = new String("tutorial/6.png");
    }
}
