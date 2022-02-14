package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Game;

public class MovableBoxGameObject extends GameObject {
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private DynamicBodyComponent dynamicBodyComponent;
    private int life = 3;
    private boolean destroyed = false;

    private int previousCellX, previousCellY, currentCellX, currentCellY;

    public MovableBoxGameObject(GameWorld gameWorld, int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        this.gameWorld = gameWorld;
        this.name = "MovableBox";

        currentCellX = worldX / gameWorld.gridSize;
        currentCellY = worldY / gameWorld.gridSize;
        previousCellX = currentCellX;
        previousCellY = currentCellY;
    }

    @Override
    public void update() {
        /*dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);
        dynamicBodyComponent.update(0, 0, 0);
        int currentGX = (int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX());
        int currentGY = (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY());
        drawableComponent.setPosition(currentGX, currentGY);

        worldX = gameWorld.updateWorldX(drawableComponent.getPositionX());
        worldY = gameWorld.updateWorldY(drawableComponent.getPositionY());*/
    }

    public void update(Node[][] cells, GameWorld gWorld){
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);
        dynamicBodyComponent.update(0, 0, 0);
        int currentGX = (int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX());
        int currentGY = (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY());
        drawableComponent.setPosition(currentGX, currentGY);

        worldX = gameWorld.updateWorldX(drawableComponent.getPositionX());
        worldY = gameWorld.updateWorldY(drawableComponent.getPositionY());

        updateCells(cells, gameWorld);
    }

    @Override
    public void updatePosition(int x, int y) {
        drawableComponent = (DrawableComponent) this.getComponent(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float physX = gameWorld.toPixelsTouchX(x);
        float physY = gameWorld.toPixelsTouchY(y);
        dynamicBodyComponent.setTransform(gameWorld.toMetersX(physX),
                gameWorld.toMetersY(physY));
    }

    @Override
    public void outOfView() {
        dynamicBodyComponent.setTransform(40, 40);
    }

    public void applyForce(float x, float y){
        drawableComponent = (DrawableComponent) this.getComponent(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);
        dynamicBodyComponent.applyForce(x, y);

        int currentGX = (int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX());
        int currentGY = (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY());
        drawableComponent.setPosition(currentGX, currentGY);

        worldX = gameWorld.updateWorldX(drawableComponent.getPositionX());
        worldY = gameWorld.updateWorldY(drawableComponent.getPositionY());
    }

    public void Damage() {
        if(!destroyed) {
            life -= 1;
            if (life == 0) {
                //components.clear();
                outOfView();
                destroyed = true;
                gameWorld.levelGrid.removeBox(worldX, worldY);
                //gameWorld.removeActiveGameObject(this);
                gameWorld.removeGameObject(this);

                PhysicsComponent physicsComponent = (PhysicsComponent) getComponent(ComponentType.Physics);
                physicsComponent.body.destroyFixture(physicsComponent.body.getFixtureList());
                physicsComponent.body.delete();
                gameWorld.gameScreen.removeDrawable((DrawableComponent) getComponent(ComponentType.Drawable));
                removeComponent(ComponentType.Physics);
                removeComponent(ComponentType.Drawable);

            }
        }
    }

    public void updateCells(Node[][] cells, GameWorld gameWorld){

        currentCellX = worldX / gameWorld.gridSize;
        currentCellY = worldY / gameWorld.gridSize;

        if(!((previousCellX == currentCellX) && (previousCellY == currentCellY))){
            cells[previousCellY][previousCellX].setMovableBox(false);
            cells[currentCellY][currentCellX].setMovableBox(true);
            previousCellX = currentCellX;
            previousCellY = currentCellY;
        }
    }

}
