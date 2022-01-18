package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
//the loading screen. Just loads the pixmaps for now

public class LoadingScreen extends Screen {
    int width, height;
    Context context;
    public LoadingScreen(Game game, int width, int height, Context context) {
        super(game);
        this.height = height;
        this.width = width;
        this.context = context;
    }

    //loads the pixmaps then passes to the main menu screen
    @Override
    public void update(float deltaTime) {
        Graphics graphics = game.getGraphics();

        //loads the pixmaps
        AssetManager.background = graphics.newPixmap("Background.png", Graphics.PixmapFormat.RGB565);
        AssetManager.backgroundPixmap = graphics.newPixmap("BackgroundPixmap.png", Graphics.PixmapFormat.RGB565);
        AssetManager.player = graphics.newPixmap("testCharacter.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.enemy = graphics.newPixmap("testEnemy.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.enemyKilled = graphics.newPixmap("testEnemyKilled.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.horizontalWall = graphics.newPixmap("testWallHorizontal.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.horizontalHalfWall = graphics.newPixmap("testWallHorizontalHalf.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.verticalWall = graphics.newPixmap("testWallVertical.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.verticalHalfWall = graphics.newPixmap("testWallVerticalHalf.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.door = graphics.newPixmap("testDoor.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.WallPixmap = graphics.newPixmap("WallPixmap.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.VerticalHalfWallPixmap = graphics.newPixmap("VerticalHalfWallPixmap.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.HorizontalHalfWallPixmap = graphics.newPixmap("HorizontalHalfWallPixmap.png", Graphics.PixmapFormat.ARGB4444);

        //sets the next screen
        game.setScreen(new MainMenuScreen(game, width, height, context));
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
