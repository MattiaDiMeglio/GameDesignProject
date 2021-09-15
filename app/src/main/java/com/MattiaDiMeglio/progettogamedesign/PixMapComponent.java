package com.MattiaDiMeglio.progettogamedesign;


import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

public class PixMapComponent extends DrawableComponent {
    Pixmap pixmap;
    int x;
    int y;
    @Override
    public void setOwner(GameObject owner) {
        this.owner = owner;
    }

    //centers the position to the pixmap center
    public PixMapComponent(Pixmap pixmap, int x, int y){
        this.pixmap = pixmap;
        this.x = x - (pixmap.getWidth()/2);
        this.y = y - (pixmap.getHeight()/2);
        this.canBeDrawn = true;
    }

    //centers the position to the pixmap cent
    @Override
    public void setPosition(int x, int y){
        this.x = x - (pixmap.getWidth()/2);
        this.y = y - (pixmap.getHeight()/2);
    }

    @Override
    public int getPositionX(){
        return x;
    }
    @Override
    public int getPositionY(){
        return y;
    }

    @Override
    public void Draw(Graphics g) {
        g.drawPixmap(pixmap, x, y);
    }

    public void Rotate(Graphics g){

    }
}
