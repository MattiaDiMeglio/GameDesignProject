package com.MattiaDiMeglio.progettogamedesign;

import com.google.fpl.liquidfun.RevoluteJointDef;

//the wallGO
public class WallGameObject extends GameObject {
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private StaticBodyComponent staticBodyComponent;


    public WallGameObject(GameWorld gameWorld, int worldX, int worldY){
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

        float physX = gameWorld.toPixelsTouchX(x);
        float physY = gameWorld.toPixelsTouchY(y);
        staticBodyComponent.setTrasform(gameWorld.toMetersX(physX),
                gameWorld.toMetersY(physY));
    }



    @Override
    public void outOfView() {
        staticBodyComponent = (StaticBodyComponent) this.getComponent(ComponentType.Physics);
        staticBodyComponent.setTrasform(40, 40);
    }
}
