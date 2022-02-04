package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Graphics;
import com.google.fpl.liquidfun.Body;

//general component class for every physics component
abstract class PhysicsComponent extends Component{
    public String name;
    float x, y, lastX, lastY;
    float width, height;
    Body body;
    @Override
    public ComponentType getType() {
        return ComponentType.Physics;
    }

    public abstract void Draw(Graphics graphics, GameWorld gameWorld, int color);

    public abstract com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge getContactList();

    public float getPositionX(){return x;}
    public float getPositionY(){return y;}
    public float getLastPositionX(){return lastX;}
    public float getLastPositionY(){return lastY;}
    public float getWidth(){return width;}
    public float getHeight(){return height;}
}
