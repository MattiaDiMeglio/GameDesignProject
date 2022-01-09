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

//extends the physics component
//implements a dynamic body from jliquidfun
//represents all physics body that can be moved by forces
public class DynamicBodyComponent extends PhysicsComponent{
    private Body body;


    public DynamicBodyComponent(float x, float y, float width, float height, World world, String name){
        this.x = x;//physical pos
        this.y = y;
        this.width = width;//physical size
        this.height = height;
        this.name = name;

        //bodydef
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);

        //body
        body = world.createBody(bodyDef);
        body.setSleepingAllowed(true);
        body.setUserData(this);//used data needed for the raycast

        //fixture
        PolygonShape box = new PolygonShape();
        box.setAsBox(width/2, height/2);

        //fixturedef
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(box);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0);
        fixtureDef.setDensity(2);
        body.createFixture(fixtureDef);
        //cleanup
        bodyDef.delete();
        box.delete();
        fixtureDef.delete();
    }

    //set position
    public void setTransform(float x, float y){
        this.x = x;
        this.y = y;
        body.setTransform(x, y, body.getAngle());
    }

    public void update(float normalizedX, float normalizedY, int angle) {
        Vec2 velocity = new Vec2();
        velocity.set(normalizedX * 2, normalizedY * 2);
        body.setLinearVelocity(velocity);
        lastX = x;
        lastY = y;
        x = body.getPositionX();
        y = body.getPositionY();
    }

    @Override
    public void Draw(Graphics graphics, GameWorld gameWorld, int color) {
        int sx = (int) (gameWorld.toPixelsX(body.getPositionX()) - (gameWorld.toPixelsXLength(width)/2));
        int sy = (int) (gameWorld.toPixelsY(body.getPositionY()) - (gameWorld.toPixelsYLength(height)/2));
        graphics.drawRect(sx, sy, (int)gameWorld.toPixelsXLength(width), (int) gameWorld.toPixelsYLength(height), color);
    }

    public Body getBody(){return body;}//getter

    public void applyForce(Vec2 force, Vec2 point){
        body.applyForce(force, point, true);
    }


}
