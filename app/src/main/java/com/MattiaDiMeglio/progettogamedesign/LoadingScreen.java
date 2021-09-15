package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;

public class LoadingScreen extends Screen {
    int width, height;
    public LoadingScreen(Game game, int width, int height) {
        super(game);
        this.height = height;
        this.width = width;
    }

    //loads the pixmaps then passes to the main menu screen
    @Override
    public void update(float deltaTime) {
        Graphics graphics = game.getGraphics();

        AssetManager.background = graphics.newPixmap("Background.png", Graphics.PixmapFormat.RGB565);
        AssetManager.player = graphics.newPixmap("testCharacter.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.enemy = graphics.newPixmap("testEnemy.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.enemyKilled = graphics.newPixmap("testEnemyKilled.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.wall = graphics.newPixmap("testWall.png", Graphics.PixmapFormat.ARGB4444);
        AssetManager.door = graphics.newPixmap("testDoor.png", Graphics.PixmapFormat.ARGB4444);

        game.setScreen(new MainMenuScreen(game, width, height));
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
