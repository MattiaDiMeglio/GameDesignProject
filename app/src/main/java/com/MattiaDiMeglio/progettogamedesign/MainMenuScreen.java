package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.text.method.Touch;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.appcompat.app.ActionBar;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
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
        AssetManager.PlayButtonPixmap = graphics.newPixmap("PlayButton.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.OptionsButtonPixmap = graphics.newPixmap("OptionsButton.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.ExitButtonPixmap = graphics.newPixmap("ExitButton.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.Lizard = graphics.newPixmap("Lizard.png", Graphics.PixmapFormat.ARGB4444);

        nextScreen = new LoadingScreen(game, width, height, context);
    }

    //for now just goes to the gamescreen
    @Override
    public void update(float deltaTime) {

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN){
                if(event.x > graphics.getWidth()/2 - AssetManager.PlayButtonPixmap.getWidth()/2){
                    if(event.y > graphics.getHeight()/2 - (AssetManager.PlayButtonPixmap.getHeight() * 2)
                            && event.y < graphics.getHeight()/2 - (AssetManager.PlayButtonPixmap.getHeight() * 2)
                            + AssetManager.PlayButtonPixmap.getHeight()){
                        game.setScreen(nextScreen);
                    }
                }

            }
        }
    }

    @Override
    public void present(float deltaTime) {
        graphics.drawPixmap(AssetManager.Lizard, 0, 0);
        graphics.drawPixmap(AssetManager.PlayButtonPixmap, (int)graphics.getWidth()/2 - AssetManager.PlayButtonPixmap.getWidth()/2, graphics.getHeight()/2 - (AssetManager.PlayButtonPixmap.getHeight() * 2));
        graphics.drawPixmap(AssetManager.OptionsButtonPixmap, (int)graphics.getWidth()/2 - AssetManager.OptionsButtonPixmap.getWidth()/2, graphics.getHeight()/2 - (AssetManager.OptionsButtonPixmap.getHeight()/2));
        graphics.drawPixmap(AssetManager.ExitButtonPixmap, (int)graphics.getWidth()/2 - AssetManager.ExitButtonPixmap.getWidth()/2, graphics.getHeight()/2 + (AssetManager.ExitButtonPixmap.getHeight()/2) + AssetManager.ExitButtonPixmap.getHeight()/2);
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
