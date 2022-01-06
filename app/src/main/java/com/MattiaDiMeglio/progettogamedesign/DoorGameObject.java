package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Graphics;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.JointDef;
import com.google.fpl.liquidfun.RevoluteJoint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

public class DoorGameObject extends GameObject {
    /*objects different from the player, will have "world coordinates"
    * that represent the point of the background in which they are.
    * depending on that the object will be drawn or not*/
    WallGameObject wall;
    private GameWorld gameWorld;
    private DrawableComponent drawableComponent;
    private DynamicBodyComponent dynamicBodyComponent;
    Joint joint;

    public DoorGameObject(GameWorld gameWorld, int worldX, int worldY, WallGameObject wall){
        this.gameWorld = gameWorld;
        this.worldX = worldX;
        this.worldY = worldY;
        this.wall = wall;
        this.name = "Door";
    }

    public void makeJoint(){
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);
        StaticBodyComponent staticBodyComponent = (StaticBodyComponent) wall.getComponent(ComponentType.Physics);
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(dynamicBodyComponent.getBody());
        jointDef.setBodyB(staticBodyComponent.getBody());
        jointDef.setLocalAnchorA(dynamicBodyComponent.width/2, 0);
        jointDef.setLocalAnchorB(-(staticBodyComponent.getWidth()/2), 0);

        joint = gameWorld.world.createJoint(jointDef);

        jointDef.delete();
    }

    @Override
    public void updatePosition(int x, int y) {
        drawableComponent = (DrawableComponent) this.getComponent(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);

        drawableComponent.setPosition(x, y);
        dynamicBodyComponent.setTransform(gameWorld.toMetersX(gameWorld.toPixelsTouchX(x)),
                gameWorld.toMetersY(gameWorld.toPixelsTouchY(y)));
    }

    public void applyForce(Vec2 force, Vec2 point){
        dynamicBodyComponent.applyForce(force, point);
    }

    public void draw(Graphics graphics, GameWorld gameWorld){
        dynamicBodyComponent = (DynamicBodyComponent) this.getComponent(ComponentType.Physics);
        if(dynamicBodyComponent != null) {
            //dynamicBodyComponent.draw(graphics, gameWorld);
        }
    }


}
