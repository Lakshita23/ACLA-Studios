package com.aclastudios.spaceconquest.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;


public class GameScreenManager {
    private Stack<Screen> states;

    public GameScreenManager(){
        states = new Stack<Screen>();
    }

    public void push(Screen screen){
        states.push(screen);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(Screen screen){
        states.pop().dispose();
        states.push(screen);
    }

    public void render(float dt){
        states.peek().render(dt);
    }
}
