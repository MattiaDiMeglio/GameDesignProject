package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
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

//main menu screen. Not yet implemented
public class MainMenuScreen extends Screen {
    String TAG;
    Graphics graphics;
    int x, y, width, height;
    boolean touched = false;
    Context context;
    Screen nextScreen;

    public MainMenuScreen(Game game, int width, int height, Context context) {
        super(game);
        TAG = "mainmenu";
        this.width = width;
        this.height = height;
        this.context = context;
        graphics = game.getGraphics();
        nextScreen = new GameScreen(game, width, height, context);
        game.setScreen(nextScreen);

    }

    //for now just goes to the gamescreen
    @Override
    public void update(float deltaTime) {
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
