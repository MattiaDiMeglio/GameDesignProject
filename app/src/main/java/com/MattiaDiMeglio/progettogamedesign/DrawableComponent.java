package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Graphics;

//drawables
abstract class DrawableComponent extends Component{
    protected int x, y;
    protected boolean canBeDrawn;
    @Override
    public ComponentType getType() {
        return ComponentType.Drawable;
    }

    public abstract void Draw(Graphics graphics);

    public abstract void setPosition(int x, int y);

    public abstract int getPositionX();

    public abstract int getPositionY();
}
