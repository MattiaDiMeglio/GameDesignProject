package com.MattiaDiMeglio.progettogamedesign;

import android.text.method.Touch;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.appcompat.app.ActionBar;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

import java.util.List;

public class MainMenuScreen extends Screen {
    String TAG;
    Graphics graphics;
    int x, y, width, height;
    boolean touched = false;

    public MainMenuScreen(Game game, int width, int height) {
        super(game);
        TAG = "mainmenu";
        this.width = width;
        this.height = height;
    }

    //for now just goes to the gamescreen
    @Override
    public void update(float deltaTime) {
        graphics = game.getGraphics();
        //List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        //game.getInput().getKeyEvents();
        //int length = touchEvents.size();
        game.setScreen(new GameScreen(game, width, height));
    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
