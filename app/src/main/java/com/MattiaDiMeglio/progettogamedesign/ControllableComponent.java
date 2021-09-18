package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Pixmap;

public class ControllableComponent extends Component {
    int destX, destY;
    float percentage = 1;

    @Override
    public ComponentType getType() {
        return ComponentType.Controllable;
    }

    public void setDestination(int x, int y){
        destX = x;
        destY = y;
        percentage = 0;
    }

    public boolean moveCharacter(Pixmap pixmap, int currentX, int currentY){
        if(percentage < 1 && currentX != destX || currentY != destY){
            int currX = currentX + pixmap.getWidth()/2;
            int curry = currentY + pixmap.getHeight()/2;
            percentage += 0.1f;
            owner.updatePosition((int)(currX + percentage * (destX - currX)),
                    (int)(curry + percentage * (destY - curry)));
        }
        return (percentage < 1);
    }
}
