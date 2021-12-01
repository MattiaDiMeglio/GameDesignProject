package com.MattiaDiMeglio.progettogamedesign;

abstract class PhysicsComponent extends Component{
    public String name;
    float x, y;
    float width, height;
    @Override
    public ComponentType getType() {
        return ComponentType.Physics;
    }

    public abstract void update();

    public float getPositionX(){return x;}
    public float getPositionY(){return y;}
    public float getWidth(){return width;}
    public float getHeight(){return height;}
}
