package com.MattiaDiMeglio.progettogamedesign;

abstract class WeaponComponent extends Component{

    protected int mag; //max amount of bullets
    protected int bullets; //actual amount of bullets
    protected float range; //weapon range
    protected int lineAmt; //amount of aim lines drawn by the weapon
    protected float[] aimLineX; //x-aim line length
    protected float[] aimLineY; //x-aim line length
    protected String shooter; //Player/Enemy

    public abstract void aim(float normalizedX, float normalizedY, float angle, GameWorld gameWorld); //compute aim lines
    public abstract void addAimLine(GameWorld gameWorld); //draw aim lines
    public abstract void shoot(GameWorld gameWorld); //shoot calling raycast
    public abstract boolean checkLineOfFire(GameWorld gameWorld); //to check that the line of fire is free (used by enemies)
    public abstract void reload();

    public abstract float getRange();
    public abstract void setShooter(String shooter);

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
