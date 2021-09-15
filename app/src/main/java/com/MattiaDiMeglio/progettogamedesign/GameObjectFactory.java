package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Pixmap;
import com.google.fpl.liquidfun.World;

import java.util.Random;
//the factory class to create the GO
public class GameObjectFactory {
    private GameWorld gameWorld;//gameworld
    private World world;//physical world

    public GameObjectFactory(GameWorld gameWorld, World world){
        this.gameWorld = gameWorld;
        this.world = world;
    }


    public GameObject makePlayer(float x, float y){
        PlayerGameObject player = new PlayerGameObject(gameWorld);
        CharacterBodyComponent body = new CharacterBodyComponent(gameWorld.toMetersX(gameWorld.toPixelsTouchX(x)),
                gameWorld.toMetersY(gameWorld.toPixelsTouchY(y)),
                gameWorld.toMetersXLength(AssetManager.player.getWidth()),
                gameWorld.toMetersYLength(AssetManager.player.getHeight()),
                world, player.name);


        PixMapComponent pixmap = new PixMapComponent(AssetManager.player, (int)x, (int)y);

        player.addComponent(body);
        player.addComponent(pixmap);
        return player;
    }

    public GameObject makeEnemy(int worldX, int worldY){
        Random random = new Random();
        EnemyGameObject enemy = new EnemyGameObject(gameWorld,
                random.nextInt(AssetManager.background.getWidth()),
                random.nextInt(AssetManager.background.getHeight()));
        CharacterBodyComponent bodyComponent = new CharacterBodyComponent(40, 40,
                gameWorld.toMetersXLength(AssetManager.enemy.getWidth()),
                gameWorld.toMetersYLength(AssetManager.enemy.getHeight()),
                world, enemy.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.enemy, -100, -100);

        enemy.addComponent(bodyComponent);
        enemy.addComponent(pixMapComponent);
        return enemy;
    }

    public GameObject makeWall(int worldX, int worldY){
        Random random = new Random();
        WallGameObject wall = new WallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.wall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.wall.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.wall, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //TODO not actually implemented, created a wall with a door
    public GameObject makeDoor(WallGameObject wall, int x, int y){
        PixMapComponent wallPixmap = (PixMapComponent) wall.getComponent(ComponentType.Drawable);
        StaticBodyComponent wallBody = (StaticBodyComponent) wall.getComponent(ComponentType.Physics);
        DoorGameObject door = new DoorGameObject(x, y, wall);

        DynamicBodyComponent dynamicBodyComponent = new DynamicBodyComponent(wallBody.x, wallBody.y,
                gameWorld.toMetersXLength(AssetManager.wall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.wall.getHeight()), world, door.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.door, -100, -100);

        door.addComponent(dynamicBodyComponent);
        door.addComponent(pixMapComponent);
        return door;
    }
}
