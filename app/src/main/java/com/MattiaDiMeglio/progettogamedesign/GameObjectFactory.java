package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Pixmap;
import com.google.fpl.liquidfun.World;

//the factory class to create the GO
public class GameObjectFactory {
    private GameWorld gameWorld;//gameworld
    private World world;//physical world

    public GameObjectFactory(GameWorld gameWorld, World world){
        this.gameWorld = gameWorld;
        this.world = world;
    }

//playerGO factory
    public GameObject makePlayer(float x, float y){
        PlayerGameObject player = new PlayerGameObject(gameWorld);//new player GO
        float playerSpeed = 16;
        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        //new physics component for playerGO
        DynamicBodyComponent body = new DynamicBodyComponent(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY),
                gameWorld.toMetersXLength(AssetManager.player.getWidth()),
                gameWorld.toMetersYLength(AssetManager.player.getHeight()),
                world, player.name, playerSpeed);
        ControllableComponent controllableComponent = new ControllableComponent(gameWorld);//new controllable component

        //new pixmap component
        PixMapComponent pixmap = new PixMapComponent(AssetManager.player, (int)x, (int)y);
        player.worldX = (int)x;
        player.worldY = (int)y;

        WeaponComponent weaponComponent = new GunComponent();

        player.addComponent(body);
        player.addComponent(pixmap);
        player.addComponent(controllableComponent);
        player.addComponent(weaponComponent);
        weaponComponent.setShooter(player.name);
        return player;
    }

    //enemy factory
    public GameObject makeEnemy(int worldX, int worldY, AIType aiType){
        EnemyGameObject enemy = new EnemyGameObject(gameWorld, worldX, worldY);
        float enemySpeed = 0;

        AIComponent aiComponent;
        WeaponComponent weaponComponent;
        Pixmap pixmap = AssetManager.enemy;
        if(aiType == AIType.Dummy){
            aiComponent = new DummyAI();
            weaponComponent = new ShotgunComponent();
            enemySpeed = 12f;
        }
        else if(aiType == AIType.Sniper){
            pixmap = AssetManager.enemy2;
            aiComponent = new SniperAI();
            weaponComponent = new RifleComponent();
        }
        else{
            pixmap = AssetManager.enemy1;
            aiComponent = new PatrolAI();
            weaponComponent = new GunComponent();
            enemySpeed = 9.5f;
        }

        aiComponent.setGridSize(gameWorld.gridSize);
        weaponComponent.setShooter(enemy.name);

        DynamicBodyComponent bodyComponent = new DynamicBodyComponent(40, 40,
                gameWorld.toMetersXLength(AssetManager.enemy.getWidth()),
                gameWorld.toMetersYLength(AssetManager.enemy.getHeight()),
                world, enemy.name, enemySpeed);
        PixMapComponent pixMapComponent = new PixMapComponent(pixmap, -100, -100);
        ControllableComponent controllableComponent = new ControllableComponent(gameWorld);

        enemy.addComponent(controllableComponent);
        enemy.addComponent(bodyComponent);
        enemy.addComponent(pixMapComponent);
        enemy.addComponent(aiComponent);
        enemy.addComponent(weaponComponent);
        return enemy;
    }

    public GameObject makeBox(int worldX, int worldY){
        DestructibleBoxGameObject box = new DestructibleBoxGameObject(gameWorld, worldX, worldY);
        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.BoxPixmap.getWidth()),
                gameWorld.toMetersYLength(AssetManager.BoxPixmap.getHeight()),
                world, box.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.BoxPixmap, -100, -100);

        box.addComponent(staticBodyComponent);
        box.addComponent(pixMapComponent);
        return box;
    }

    public GameObject makeMovableBox(int worldX, int worldY){
        MovableBoxGameObject box = new MovableBoxGameObject(gameWorld, worldX, worldY);
        DynamicBodyComponent dynamicBodyComponent = new DynamicBodyComponent(40f, 40f,
                gameWorld.toMetersXLength(AssetManager.MovableBoxPixmap.getWidth()),
                gameWorld.toMetersYLength(AssetManager.MovableBoxPixmap.getHeight()),
                world, box.name, 0);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.MovableBoxPixmap, -100, -100);

        box.addComponent(dynamicBodyComponent);
        box.addComponent(pixMapComponent);
        return box;
    }

    //wall factory
    public GameObject makeWall(int worldX, int worldY){
        WallGameObject wall = new WallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.WallPixmap.getWidth()),
                gameWorld.toMetersYLength(AssetManager.WallPixmap.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.WallPixmap, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //horizontal half wall (the ones you can shoot through)
    public GameObject makeHorizontalHalfWall(int worldX, int worldY){
        HalfWallGameObject wall = new HalfWallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.HorizontalHalfWallPixmap.getWidth()),
                gameWorld.toMetersYLength(AssetManager.HorizontalHalfWallPixmap.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.HorizontalHalfWallPixmap, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }

    //vertical half wall
    public GameObject makeVerticalHalfWall(int worldX, int worldY){
        HalfWallGameObject wall = new HalfWallGameObject(gameWorld, worldX, worldY);

        StaticBodyComponent staticBodyComponent = new StaticBodyComponent(40, 40, 0f,
                gameWorld.toMetersXLength(AssetManager.VerticalHalfWallPixmap.getWidth()),
                gameWorld.toMetersYLength(AssetManager.VerticalHalfWallPixmap.getHeight()),
                world, wall.name);
        PixMapComponent pixMapComponent = new PixMapComponent(AssetManager.VerticalHalfWallPixmap, -100, -100);

        wall.addComponent(staticBodyComponent);
        wall.addComponent(pixMapComponent);
        return wall;
    }
}
