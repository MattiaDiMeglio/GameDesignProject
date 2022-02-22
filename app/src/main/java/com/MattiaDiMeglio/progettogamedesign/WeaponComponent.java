package com.MattiaDiMeglio.progettogamedesign;

abstract class WeaponComponent extends Component{

    protected int mag; //max amount of bullets
    protected int bullets; //actual amount of bullets
    protected float range; //weapon range
    protected int lineAmt; //amount of aim lines drawn by the weapon
    protected float[] aimLineX; //x-aim line
    protected float[] aimLineY; //y-aim line
    protected String shooter; //Player/Enemy

    public abstract void aim(float normalizedX, float normalizedY, float angle, GameWorld gameWorld); //compute aim lines
    public abstract void addAimLine(GameWorld gameWorld); //draw aim lines
    public abstract void shoot(GameWorld gameWorld); //shoot calling raycast
    public abstract boolean checkLineOfFire(GameWorld gameWorld); //to check that the line of fire is free (used by enemies)

    public void reload(){ bullets = mag; }
    public void setShooter(String shooter){ this.shooter = shooter;} //used by raycast and addAimLine (GunComponent)

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
