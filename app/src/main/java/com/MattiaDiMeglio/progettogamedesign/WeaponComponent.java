package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

abstract class WeaponComponent extends Component{

    protected int mag; //max amount of bullets
    protected int bullets; //actual amount of bullets
    protected float range; //weapon range
    protected int lineAmt; //amount of aim lines draw by the weapon
    protected float[] aimLineX; //x coordinates of the aim lines
    protected float[] aimLineY; //y coordinates of the aim lines

    public abstract void shoot(GameWorld gameWorld);
    public abstract void reload();
    public abstract void aim(int angle, GameWorld gameWorld); //draw aim lines
    public abstract float getRange();
    public abstract int getLineAmt();
    public abstract float[] getAimLineX();
    public abstract float[] getAimLineY();

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
