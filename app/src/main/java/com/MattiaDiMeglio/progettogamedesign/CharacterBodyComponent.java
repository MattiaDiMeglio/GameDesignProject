package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Graphics;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;
//extends the physics component so that it can be used as one
//the character body is a kinematic body
//from jliquidfun
public class CharacterBodyComponent extends PhysicsComponent{
    private Body body;


    //needs a world position, sizes, the physical world and a name
    public CharacterBodyComponent(float x, float y, float width, float height, World world, String name){
        this.x = x;//physical position
        this.y = y;
        this.width = width;//physical sizes
        this.height = height;
        this.name = name;

        //body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);
        bodyDef.setGravityScale(0.0f);
        body = world.createBody(bodyDef);

        //userdata
        //needed for the raycast
        body.setUserData(this);

        //fixture
        PolygonShape box = new PolygonShape();
        box.setAsBox(width, height);

        //fixturedef
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(box);
        body.createFixture(fixtureDef);

        //cleanup
        bodyDef.delete();
        box.delete();
        fixtureDef.delete();
    }

    public void update(float normalizedX, float normalizedY, int angle) {
        Vec2 velocity = new Vec2();
        velocity.set(normalizedX * 2, normalizedY * 2);
        body.setLinearVelocity(velocity);
        x = body.getPositionX();
        y = body.getPositionY();
    }


    //TODO Si possono anche levare, se vogliamo
    //position update
    public void setTransform(float x, float y, int angle){
        this.x = x;
        this.y = y;
        body.setTransform(x, y, angle);

    }

    public void setTransform(float x, float y){
        this.x = x;
        this.y = y;
        body.setTransform(x, y, body.getAngle());
    };




    public void destroy(){
        body.destroyFixture(body.getFixtureList());
        body.delete();
    }


    @Override
    public void Draw(Graphics graphics, GameWorld gameWorld, int color) {
        int sx = (int) (gameWorld.toPixelsX(body.getPositionX()) - (gameWorld.toPixelsXLength(width)/2));
        int sy = (int) (gameWorld.toPixelsY(body.getPositionY()) - (gameWorld.toPixelsYLength(height)/2));
        graphics.drawRect(sx, sy, (int)gameWorld.toPixelsXLength(width), (int) gameWorld.toPixelsYLength(height), color);
    }
}
