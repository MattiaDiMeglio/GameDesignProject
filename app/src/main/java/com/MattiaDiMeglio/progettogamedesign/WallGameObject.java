package com.MattiaDiMeglio.progettogamedesign;

public class WallGameObject extends GameObject {
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private StaticBodyComponent staticBodyComponent;


    public WallGameObject(int worldX, int worldY, GameWorld gameWorld){
        this.worldX = worldX;
        this.worldY = worldY;
        this.gameWorld = gameWorld;
        this.name = "Wall";
    }

    @Override
    public void updatePosition(int x, int y) {
        drawableComponent = (DrawableComponent) this.getComponent(ComponentType.Drawable);
        staticBodyComponent = (StaticBodyComponent) this.getComponent(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        staticBodyComponent.setTrasform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }



}
