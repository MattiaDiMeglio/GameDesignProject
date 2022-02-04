package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
//the loading screen. Just loads the pixmaps for now

public class LoadingScreen extends Screen {
    int width, height;
    Context context;
    Graphics graphics;
    Screen nextScreen;
    boolean created = false;
    MainMenuScreen mainMenuScreen;
    public LoadingScreen(Game game, int width, int height, Context context, MainMenuScreen mainMenuScreen) {
        super(game);
        this.height = height;
        this.width = width;
        this.context = context;
        this.mainMenuScreen = mainMenuScreen;
        graphics = game.getGraphics();
    }

    //loads the pixmaps then passes to the main menu screen
    @Override
    public void update(float deltaTime) {
        if(!created) {
            if(AssetManager.background == null) {
                //loads the pixmaps
                AssetManager.background = graphics.newPixmap("RoadPixmap.png", Graphics.PixmapFormat.RGB565);
                AssetManager.backgroundPixmap = graphics.newPixmap("BackgroundPixmap.png", Graphics.PixmapFormat.RGB565);
                AssetManager.player = graphics.newPixmap("testCharacter.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.playerKilled = graphics.newPixmap("testCharacterKilled.png", Graphics.PixmapFormat.ARGB4444);
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
                AssetManager.BoxPixmap = graphics.newPixmap("Box.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.PausePixmap = graphics.newPixmap("PauseImg.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.PlayPixmap = graphics.newPixmap("PlayImg.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.ResumeButtonPixmap = graphics.newPixmap("ResumeButton.png", Graphics.PixmapFormat.ARGB4444);
            }
            //setting the screen and gameworld

            nextScreen = new GameScreen(game, width, height, context, mainMenuScreen);
            GameScreen gs = (GameScreen) nextScreen;
            GameWorld gw = gs.getGameWorld();
            GameObjectFactory gameObjectFactory = new GameObjectFactory(gw, gw.world);

            //making the player
            int x = graphics.getWidth() / 2;
            int y = graphics.getHeight() / 2;
            gw.player = (PlayerGameObject) gw.addActiveGameObject(gameObjectFactory.makePlayer(x, y));
            gs.addDrawable((DrawableComponent) gw.player.getComponent(ComponentType.Drawable));

            //making test enemy
            int testEnemyX = 250;
            int testEnemyY = 300;
            gw.testEnemy = (EnemyGameObject) gameObjectFactory.makeEnemy(testEnemyX, testEnemyY, AIType.Wimp);
            gw.addGameObject(gw.testEnemy);

            //making the map
            MapManager mapManager = new MapManager(gw, gameObjectFactory, context);
            gw.mapCells = mapManager.initMapResized(gw.mapCells, AssetManager.backgroundPixmap.getWidth() / AssetManager.WallPixmap.getWidth(),
                    AssetManager.backgroundPixmap.getHeight() / AssetManager.WallPixmap.getWidth());
            gw.mapCells = mapManager.generateMapResized(gw.mapCells, 0, 0, AssetManager.backgroundPixmap.getWidth() / AssetManager.WallPixmap.getWidth() - 1,
                    AssetManager.backgroundPixmap.getHeight() / AssetManager.WallPixmap.getWidth() - 1, (Math.random() * 6) % 2 == 0);

            mapManager.constructMap(gw.mapCells, 50, 50);

            //pathfinding
            int levelWidth = AssetManager.backgroundPixmap.getWidth();
            int levelHeight = AssetManager.backgroundPixmap.getHeight();
            gw.levelGrid = new GridManager(levelWidth, levelHeight, gw.gridSize, gw);
            gw.levelGrid.addObstacles(gw.gameObjects, gw);
            created = true;
        }

        //sets the next screen
        game.setScreen(nextScreen);
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

    public void setNonCreated(){
        created = false;
    }
}
