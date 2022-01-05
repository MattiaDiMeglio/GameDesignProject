package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.google.fpl.liquidfun.World;

import java.nio.file.Path;
import java.util.Random;
//the factory class to create the GO
public class GameObjectFactory {
    private GameWorld gameWorld;//gameworld
    private World world;//physical world

    public GameObjectFactory(GameWorld gameWorld, World world){
        this.gameWorld = gameWorld;
        this.world = world;
    }

//playerGO factory TODO piazzare tutte le posizioni basandosi sul worldpos
    public GameObject makePlayer(float x, float y){
        PlayerGameObject player = new PlayerGameObject(gameWorld);//new player GO
        //new physics component for playerGO
        CharacterBodyComponent body = new CharacterBodyComponent(gameWorld.toMetersX(gameWorld.toPixelsTouchX(x)),
                gameWorld.toMetersY(gameWorld.toPixelsTouchY(y)),
                gameWorld.toMetersXLength(AssetManager.player.getWidth()),
                gameWorld.toMetersYLength(AssetManager.player.getHeight()),
                world, player.name);
        ControllableComponent controllableComponent = new ControllableComponent(gameWorld);//new controllable component

        //new pixmap component
        PixMapComponent pixmap = new PixMapComponent(AssetManager.player, (int)x, (int)y);

        ShotgunComponent shotgunComponent = new ShotgunComponent();

        player.addComponent(body);
        player.addComponent(pixmap);
        player.addComponent(controllableComponent);
        player.addComponent(shotgunComponent);

        return player;
    }

    //enemy factory
    public GameObject makeEnemy(int worldX, int worldY){
        EnemyGameObject enemy = new EnemyGameObject(gameWorld, worldX, worldY);
        CharacterBodyComponent bodyComponent = new CharacterBodyComponent(40, 40,
                gameWorld.toMetersXLength(AssetManager.enemy.getWidth()),
                gameWorld.toMetersYLength(AssetManager.enemy.getHeight()),
                world, enemy.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.enemy, -100, -100);
        PathfindingComponent pathfindingComponent = new PathfindingComponent();

        enemy.addComponent(bodyComponent);
        enemy.addComponent(pixMapComponent);
        enemy.addComponent(pathfindingComponent);
        return enemy;
    }

    //horizontal wall factory
    public GameObject makeHorizontalWall(int worldX, int worldY){
        WallGameObject wall = new WallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.horizontalWall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.horizontalWall.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.horizontalWall, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //horizontal half wall (the ones you can shoot through
    public GameObject makeHorizontalHalfWall(int worldX, int worldY){
        HalfWallGameObject wall = new HalfWallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.horizontalHalfWall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.horizontalHalfWall.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.horizontalHalfWall, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //vertical wall
    public GameObject makeVerticalWall(int worldX, int worldY){
        WallGameObject wall = new WallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.verticalWall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.verticalWall.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.verticalWall, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //vertical half wall
    public GameObject makeVerticalHalfWall(int worldX, int worldY){
        HalfWallGameObject wall = new HalfWallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.verticalHalfWall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.verticalHalfWall.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.verticalHalfWall, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //TODO not actually implemented, created a wall with a door
    public GameObject makeDoor(WallGameObject wall, int x, int y){
        PixMapComponent wallPixmap = (PixMapComponent) wall.getComponent(ComponentType.Drawable);
        StaticBodyComponent wallBody = (StaticBodyComponent) wall.getComponent(ComponentType.Physics);
        DoorGameObject door = new DoorGameObject(gameWorld, x, y, wall);

        DynamicBodyComponent dynamicBodyComponent = new DynamicBodyComponent(wallBody.x, wallBody.y,
                gameWorld.toMetersXLength(AssetManager.horizontalWall.getWidth()),
                gameWorld.toMetersYLength(AssetManager.horizontalWall.getHeight())/2, world, door.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.door, -100, -100);

        door.addComponent(dynamicBodyComponent);
        door.addComponent(pixMapComponent);
        //door.makeJoint();
        return door;
    }
}
