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
        staticBodyComponent = (StaticBodyComponent) this.getComponent(ComponentType.Physics);
        staticBodyComponent.setTrasform(40, 40);
    }

    public void Damage() {
        if(!destroyed) {
            life -= 1;
            if (life == 0) {
                AssetManager.BoxDestroyed.play(0.8f);
                outOfView();
                destroyed = true;
                gameWorld.levelGrid.removeBox(worldX, worldY);
                gameWorld.removeGameObject(this);
                PhysicsComponent physicsComponent = (PhysicsComponent) getComponent(ComponentType.Physics);
                physicsComponent.body.destroyFixture(physicsComponent.body.getFixtureList());
                physicsComponent.body.delete();
                gameWorld.gameScreen.removeDrawable((DrawableComponent) getComponent(ComponentType.Drawable));
                removeComponent(ComponentType.Physics);
                removeComponent(ComponentType.Drawable);
            }
            else AssetManager.BoxHit.play(0.8f);
        }
    }

    public boolean isDestroyed(){return destroyed;}
}