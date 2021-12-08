package com.MattiaDiMeglio.progettogamedesign;


import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

//the component with the pixmap
public class PixMapComponent extends DrawableComponent {
    Pixmap pixmap;
    @Override
    public void setOwner(GameObject owner) {
        this.owner = owner;
    }

    //centers the position to the pixmap center
    public PixMapComponent(Pixmap pixmap, int x, int y){
        this.pixmap = pixmap;//we set the pixmap
        //the position centered
        this.x = x - (pixmap.getWidth()/2);
        this.y = y - (pixmap.getHeight()/2);
        this.canBeDrawn = true;//if the pixmap is in view it can be drawn
    }

    //centers the position to the pixmap cent
    @Override
    public void setPosition(int x, int y){
        this.x = x - (pixmap.getWidth()/2);
        this.y = y - (pixmap.getHeight()/2);
    }

    @Override
    public int getPositionX(){
        return x + (pixmap.getWidth()/2);
    }
    @Override
    public int getPositionY(){
        return y + (pixmap.getHeight()/2);
    }

    @Override
    public void Draw(Graphics g) {
        g.drawPixmap(pixmap, x, y);
    }

}
