package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Graphics;

//general component class for every physics component
abstract class PhysicsComponent extends Component{
    public String name;
    float x, y;
    float width, height;
    @Override
    public ComponentType getType() {
        return ComponentType.Physics;
    }

    public abstract void update();

    public abstract void Draw(Graphics graphics, GameWorld gameWorld, int color);

    public float getPositionX(){return x;}
    public float getPositionY(){return y;}
    public float getWidth(){return width;}
    public float getHeight(){return height;}
}
