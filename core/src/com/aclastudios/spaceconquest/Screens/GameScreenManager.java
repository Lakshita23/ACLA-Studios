package com.aclastudios.spaceconquest.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/* Manages switching of screens */
public class GameScreenManager {
    //Screens handled by stack as it helps in transitioning without losing data
    private Stack<Screen> states;

    public GameScreenManager(){
        states = new Stack<Screen>();
    }
    //Add screen
    public void push(Screen screen){
        states.push(screen);
    }
    //Remove screen
    public void pop(){
        states.pop().dispose();
    }
    //Remove and add screen
    public void set(Screen screen){
        states.pop().dispose();
        states.push(screen);
    }

    public void render(float dt){
        states.peek().render(dt);
    }
}
