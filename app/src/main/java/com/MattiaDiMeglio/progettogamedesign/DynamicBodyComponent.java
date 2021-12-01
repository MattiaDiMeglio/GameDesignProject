package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Graphics;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

//dynamic bodyes
public class DynamicBodyComponent extends PhysicsComponent{
    private Body body;


    public DynamicBodyComponent(float x, float y, float width, float height, World world, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);

        body = world.createBody(bodyDef);
        body.setSleepingAllowed(true);
        body.setUserData(this);


        PolygonShape box = new PolygonShape();
        box.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(box);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0);
        fixtureDef.setDensity(2);
        body.createFixture(fixtureDef);

        bodyDef.delete();
        box.delete();
        fixtureDef.delete();
    }

    public void setTransform(float x, float y){
        this.x = x;
        this.y = y;
        body.setTransform(x, y, body.getAngle());
    }

    @Override
    public void update() {

    }

    public Body getBody(){return body;}


    public void applyForce(Vec2 force, Vec2 point){
        body.applyForce(force, point, true);
    }

    public void draw(Graphics graphics, GameWorld gameWorld){
        int sx = (int) (gameWorld.toPixelsX(body.getPositionX()) - (gameWorld.toPixelsXLength(width)/2));
        int sy = (int) (gameWorld.toPixelsY(body.getPositionY()) - (gameWorld.toPixelsYLength(height)/2));
        graphics.drawRect(sx, sy, (int)gameWorld.toPixelsXLength(width), (int) gameWorld.toPixelsYLength(height) , Color.WHITE);
    }

}
