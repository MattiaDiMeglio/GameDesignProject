package com.MattiaDiMeglio.progettogamedesign;

import com.badlogic.androidgames.framework.Graphics;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.World;
//the physics component for the elements that never move (walls
public class StaticBodyComponent extends PhysicsComponent{
    private Body body;
    float angle;
    public StaticBodyComponent(float x, float y, float angle, float width, float height, World world, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setAngle(angle);
        bodyDef.setType(BodyType.staticBody);

        body = world.createBody(bodyDef);
        this.name = name;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(width/2, height/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(box);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0.4f);
        fixtureDef.setDensity(0.5f);
        body.createFixture(fixtureDef);

        bodyDef.delete();
        box.delete();
        fixtureDef.delete();

    }

    public void setTrasform(float x, float y){
        this.x = x;
        this.y = y;
        body.setTransform(x, y, angle);
    }

    public Body getBody(){return body;}

    @Override
    public void Draw(Graphics graphics, GameWorld gameWorld, int color) {
        int sx = (int) (gameWorld.toPixelsX(body.getPositionX()) - (gameWorld.toPixelsXLength(width)/2));
        int sy = (int) (gameWorld.toPixelsY(body.getPositionY()) - (gameWorld.toPixelsYLength(height)/2));
        graphics.drawRect(sx, sy, (int)gameWorld.toPixelsXLength(width), (int) gameWorld.toPixelsYLength(height), color);
    }

}
