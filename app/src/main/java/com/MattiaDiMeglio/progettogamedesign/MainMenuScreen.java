package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;

import java.util.List;

public class MainMenuScreen extends Screen {
    String TAG;
    Graphics graphics;
    Audio audio;
    int width, height;
    Context context;
    Screen nextScreen;

    public MainMenuScreen(Game game, int width, int height, Context context) {
        super(game);
        TAG = "mainmenu";
        this.width = width;
        this.height = height;
        this.context = context;

        graphics = game.getGraphics();
        audio = game.getAudio();

        AssetManager.MainMenuBackground = graphics.newPixmap("menu.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.PlayButtonPixmap = graphics.newPixmap("PlayButton.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.ExitButtonPixmap = graphics.newPixmap("ExitButton.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.MainMenuMusic = audio.newMusic("mainmenumusic.wav");
        AssetManager.MainMenuMusic.setLooping(true);
        AssetManager.MainMenuMusic.play();
        nextScreen = new LoadingScreen(game, width, height, context, this);
    }

    @Override
    public void update(float deltaTime) {

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            if(!touchEvents.isEmpty()) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                    if (event.x > graphics.getWidth() / 2 - AssetManager.PlayButtonPixmap.getWidth() / 2) {
                        if (event.y > graphics.getHeight() / 2 - (AssetManager.PlayButtonPixmap.getHeight() * 2)
                                && event.y < graphics.getHeight() / 2 - (AssetManager.PlayButtonPixmap.getHeight() * 2)
                                + AssetManager.PlayButtonPixmap.getHeight()) {
                            if(AssetManager.MainMenuMusic.isPlaying())
                                AssetManager.MainMenuMusic.stop();
                            LoadingScreen loadingScreen = (LoadingScreen) nextScreen;
                            loadingScreen.setNonCreated();
                            game.setScreen(nextScreen);
                        } else if (event.y > graphics.getHeight() / 2 + (AssetManager.ExitButtonPixmap.getHeight() * 2 - AssetManager.ExitButtonPixmap.getHeight())
                                && event.y < graphics.getHeight() / 2 + (AssetManager.ExitButtonPixmap.getHeight() * 2)) {
                            ProgettoGameDesign progettoGameDesign = (ProgettoGameDesign) game;
                            progettoGameDesign.ExitGame();
                        }
                    }

                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        graphics.drawPixmap(AssetManager.MainMenuBackground, 0, 0);
        graphics.drawPixmap(AssetManager.PlayButtonPixmap, graphics.getWidth() /2 - AssetManager.PlayButtonPixmap.getWidth()/2, graphics.getHeight()/2 - (AssetManager.PlayButtonPixmap.getHeight() * 2));
        graphics.drawPixmap(AssetManager.ExitButtonPixmap, graphics.getWidth() /2 - AssetManager.ExitButtonPixmap.getWidth()/2, graphics.getHeight()/2 + (AssetManager.ExitButtonPixmap.getHeight()/2) + AssetManager.ExitButtonPixmap.getHeight()/2);
    }

    @Override
    public void pause() {
        if(AssetManager.MainMenuMusic.isPlaying())
            AssetManager.MainMenuMusic.stop();
    }

    @Override
    public void resume() {
        if(AssetManager.MainMenuMusic.isStopped())
            AssetManager.MainMenuMusic.play();
    }

    @Override
    public void dispose() { }
}
