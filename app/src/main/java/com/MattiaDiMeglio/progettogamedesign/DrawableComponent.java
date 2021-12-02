package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Graphics;

//the drawable component containts all the possibile drawable elements, pixmap, sprite ecc
abstract class DrawableComponent extends Component{
    protected int x, y;//position
    protected boolean canBeDrawn;//if the element is on screen it can be drawn
    @Override
    public ComponentType getType() {
        return ComponentType.Drawable;
    }//returns type

    public abstract void Draw(Graphics graphics);//draw the element

    public abstract void setPosition(int x, int y);//set pos
    //getters
    public abstract int getPositionX();

    public abstract int getPositionY();
}
