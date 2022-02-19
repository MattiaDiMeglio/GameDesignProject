package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Sound;
//the loading screen. Just loads the pixmaps for now

public class LoadingScreen extends Screen {
    int width, height;
    Context context;
    Graphics graphics;
    Audio audio;
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
        audio = game.getAudio();
    }

    //loads the pixmaps then passes to the main menu screen
    @Override
    public void update(float deltaTime) {
        if(!created) {
            if(AssetManager.background == null) {
                //loads the pixmaps
                AssetManager.background = graphics.newPixmap("RoadPixmap.png", Graphics.PixmapFormat.RGB565);
                AssetManager.backgroundPixmap = graphics.newPixmap("BackgroundPixmap.png", Graphics.PixmapFormat.RGB565);
                AssetManager.player = graphics.newPixmap("Player.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.playerKilled = graphics.newPixmap("DeadPlayer.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.enemy = graphics.newPixmap("Enemy.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.enemy1 = graphics.newPixmap("Enemy1.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.enemy2 = graphics.newPixmap("Enemy2.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.enemyKilled = graphics.newPixmap("DeadEnemy.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.horizontalWall = graphics.newPixmap("testWallHorizontal.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.horizontalHalfWall = graphics.newPixmap("testWallHorizontalHalf.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.verticalWall = graphics.newPixmap("testWallVertical.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.verticalHalfWall = graphics.newPixmap("testWallVerticalHalf.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.door = graphics.newPixmap("testDoor.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.WallPixmap = graphics.newPixmap("WallPixmap.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.VerticalHalfWallPixmap = graphics.newPixmap("VerticalHalfWallPixmap.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.HorizontalHalfWallPixmap = graphics.newPixmap("HorizontalHalfWallPixmap.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.BoxPixmap = graphics.newPixmap("Box.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.MovableBoxPixmap = graphics.newPixmap("MovableBox.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.PausePixmap = graphics.newPixmap("PauseImg.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.PlayPixmap = graphics.newPixmap("PlayImg.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.ResumeButtonPixmap = graphics.newPixmap("ResumeButton.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.EndLevelPixmap = graphics.newPixmap("EndLevel.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.PlayerDeadPixmap = graphics.newPixmap("Dead.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.EndGamePixmap = graphics.newPixmap("EndGame.png", Graphics.PixmapFormat.ARGB4444);
                AssetManager.BoxHit = audio.newSound("BoxHit.mp3");
                AssetManager.BoxDestroyed = audio.newSound("BoxDestroyed.mp3");
                AssetManager.GunShoot = audio.newSound("GunShoot.mp3");
                AssetManager.GunReload = audio.newSound("GunReload.mp3");
                AssetManager.RifleShoot = audio.newSound("RifleShoot.mp3");
                AssetManager.RifleReload = audio.newSound("RifleReload.mp3");
                AssetManager.ShotgunShoot = audio.newSound("ShotgunShoot.mp3");
                AssetManager.ShotgunReload = audio.newSound("ShotgunReload.mp3");

                AssetManager.GameMusic = audio.newMusic("gamemusic.wav");
                AssetManager.GameMusic.setLooping(true);
            }
            //setting the screen and gameworld

            if(nextScreen == null) {
                nextScreen = new GameScreen(game, width, height, context, mainMenuScreen, this);
            }
            GameScreen gs = (GameScreen) nextScreen;
            gs.gameState = GameScreen.GameState.Ready;
            GameWorld gw = gs.getGameWorld();
            GameObjectFactory gameObjectFactory = new GameObjectFactory(gw, gw.world);
            MapManager mapManager = new MapManager(gw, gameObjectFactory, context);

            //making the player
            int x = mapManager.toActualCoord(5);
            int y = mapManager.toActualCoord(5);
            if(gw.player == null) {
                gw.player = (PlayerGameObject) gw.addActiveGameObject(gameObjectFactory.makePlayer(x, y));
                gs.addDrawable((DrawableComponent) gw.player.getComponent(ComponentType.Drawable));
            }else {
                gw.player.updatePosition(x, y);
                gw.player.resetProjectiles();
                if(gw.player.killed){
                    gw.player.killed();
                }
                if(!gw.activeGameObjects.contains(gw.player))
                    gw.addActiveGameObject(gw.player);
            }
            gw.enemyNum = (10 * gw.level) - (gw.level * 2);
            gw.totalEnemies = gw.enemyNum;
            //making the map
            gw.mapCells = mapManager.initMapResized(gw.mapCells, AssetManager.backgroundPixmap.getWidth() / AssetManager.WallPixmap.getWidth(),
                    AssetManager.backgroundPixmap.getHeight() / AssetManager.WallPixmap.getWidth());
            gw.mapCells = mapManager.generateMapResized(gw.mapCells, 0, 0, AssetManager.backgroundPixmap.getWidth() / AssetManager.WallPixmap.getWidth() - 1,
                    AssetManager.backgroundPixmap.getHeight() / AssetManager.WallPixmap.getWidth() - 1, (Math.random() * 6) % 2 == 0);

            mapManager.constructMap(gw.mapCells, 50, 50);
            //pathfinding
            int levelWidth = AssetManager.backgroundPixmap.getWidth();
            int levelHeight = AssetManager.backgroundPixmap.getHeight();
            gw.levelGrid = new GridManager(levelWidth, levelHeight, gw.gridSize);
            gw.levelGrid.addObstacles(gw.gameObjects, gw);
            created = true;
            Log.d("loading", "level: " + gw.level + " enemies: " + gw.enemyNum);
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