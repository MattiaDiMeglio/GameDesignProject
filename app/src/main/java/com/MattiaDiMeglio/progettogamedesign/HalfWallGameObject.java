package com.MattiaDiMeglio.progettogamedesign;

public class HalfWallGameObject extends GameObject{
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private StaticBodyComponent staticBodyComponent;

    public HalfWallGameObject(GameWorld gameWorld, int worldX, int worldY){
        this.gameWorld = gameWorld;
        this.worldX = worldX;
        this.worldY = worldY;
        this.name = "HalfWall";
    }

    @Override
    public void updatePosition(int x, int y) {
        drawableComponent = (DrawableComponent) this.getComponent(ComponentType.Drawable);
        staticBodyComponent = (StaticBodyComponent) this.getComponent(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float physX = gameWorld.toPixelsTouchX(x);
        float physY = gameWorld.toPixelsTouchY(y);
        staticBodyComponent.setTrasform(gameWorld.toMetersX(physX), gameWorld.toMetersY(physY));
    }

    @Override
    public void outOfView() {
        staticBodyComponent.setTrasform(40, 40);
    }
}
