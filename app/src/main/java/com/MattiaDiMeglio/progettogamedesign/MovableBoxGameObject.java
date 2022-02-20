package com.MattiaDiMeglio.progettogamedesign;

public class MovableBoxGameObject extends GameObject {
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private DynamicBodyComponent dynamicBodyComponent;

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
        dynamicBodyComponent.update(0, 0, 0);
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

    public void updateCells(Node[][] cells, GameWorld gameWorld){

        int newCellX = worldX / gameWorld.gridSize;
        int newCellY = worldY / gameWorld.gridSize;

        if(newCellX >= 0 && newCellX < gameWorld.levelGrid.getCells().length &&
                newCellY >= 0 && newCellY < gameWorld.levelGrid.getCells().length){

            currentCellX = newCellX;
            currentCellY = newCellY;

            if(!((previousCellX == currentCellX) && (previousCellY == currentCellY))){
                cells[previousCellY][previousCellX].setBox(false);
                cells[currentCellY][currentCellX].setBox(true);
                previousCellX = currentCellX;
                previousCellY = currentCellY;
            }
        }
    }

}
