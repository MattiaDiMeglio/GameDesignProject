package com.MattiaDiMeglio.progettogamedesign;

public class BoxGameObject extends GameObject {
    GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private StaticBodyComponent staticBodyComponent;
    private int life = 3;
    private boolean destroyed = false;


    public BoxGameObject(GameWorld gameWorld, int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        this.gameWorld = gameWorld;
        this.name = "Box";
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
        staticBodyComponent.setTrasform(40, 40);
    }

    public void Damage() {
        life -= 1;
        if( life == 0){
            components.clear();
            destroyed = true;
            gameWorld.removeActiveGameObject(this);
            gameWorld.removeGameObject(this);

        }
    }

    public boolean isDestroyed(){return destroyed;}
}