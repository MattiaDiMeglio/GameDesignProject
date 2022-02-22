package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Sound;

//static class containing all the pixmaps, sounds and musics of the game
//they're loaded once in the loadingScreen and can be used everywhere without instantiating the class

public class AssetManager {
    //Pixmaps
    public static Pixmap background;
    public static Pixmap backgroundPixmap;
    public static Pixmap player;
    public static Pixmap playerKilled;
    public static Pixmap enemy;
    public static Pixmap enemy1;
    public static Pixmap enemy2;
    public static Pixmap enemyKilled;
    public static Pixmap horizontalWall;
    public static Pixmap horizontalHalfWall;
    public static Pixmap verticalWall;
    public static Pixmap verticalHalfWall;
    public static Pixmap door;
    public static Pixmap WallPixmap;
    public static Pixmap HorizontalHalfWallPixmap;
    public static Pixmap VerticalHalfWallPixmap;
    public static Pixmap BoxPixmap;
    public static Pixmap MovableBoxPixmap;
    public static Pixmap PausePixmap;
    public static Pixmap PlayPixmap;
    public static Pixmap ResumeButtonPixmap;
    public static Pixmap OptionsButtonPixmap;
    public static Pixmap ExitButtonPixmap;
    public static Pixmap PlayButtonPixmap;
    public static Pixmap MainMenuBackground;
    public static Pixmap EndLevelPixmap;
    public static Pixmap PlayerDeadPixmap;
    public static Pixmap EndGamePixmap;

    //Sound effects
    public static Sound BoxHit;
    public static Sound BoxDestroyed;
    public static Sound GunShoot;
    public static Sound GunReload;
    public static Sound RifleShoot;
    public static Sound RifleReload;
    public static Sound ShotgunShoot;
    public static Sound ShotgunReload;

    //BGM
    public static Music MainMenuMusic;
    public static Music GameMusic;
}
