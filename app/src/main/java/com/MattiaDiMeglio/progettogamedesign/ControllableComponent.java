package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Pixmap;

//component that controls a game object movement
//be it the playerGO or the enemy.
//it changes the position of the go components and should work
//indipendently from which class calls it (the AI will call this class to move the enemy
//and the joystick will do the same for the playerGO
public class ControllableComponent extends Component {
    int destX, destY;//destination
    int currX, currY;
    float percentage = 1;//percentage of the lerp

    @Override
    public ComponentType getType() {
        return ComponentType.Controllable;
    }

    public void setDestination(int x, int y){//init of the movement TODO probabilmente inutile
        destX = x;
        destY = y;
        percentage = 0;
    }

    //lerp of the movement TODO modificare per funzionare con gli stick analogici
    public boolean moveCharacter(){
        PixMapComponent pixmapComp = (PixMapComponent) owner.getComponent(ComponentType.Drawable);//gets the drawable component as ref for the movement
        Pixmap pixmap1 = pixmapComp.pixmap;//gets the actual pixmap
        int currentX = pixmapComp.getPositionX();//pixmap position
        int currentY = pixmapComp.getPositionY();
        if(percentage < 1 && currentX != destX || currentY != destY){//if where not ad the destination and percentage is < 1
            if(currentX == destX) { //if where're already on destX it doesn't change
                currX = currentX;
            } else {
                currX = currentX + pixmap1.getWidth() / 2;//otherwise we change it
            }
            if(currentY == destY){//as before
                currY = currentY;
            } else {
                currY = currentY + pixmap1.getHeight() / 2;
            }
            percentage += 0.1f;//update the percentage
            //update pos
            owner.updatePosition((int)(currX + percentage * (destX - currX)),
                    (int)(currY + percentage * (destY - currY)));
        }
        return (percentage < 1);
    }
}
