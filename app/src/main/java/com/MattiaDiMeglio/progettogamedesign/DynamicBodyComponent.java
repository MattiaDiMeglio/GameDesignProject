package com.MattiaDiMeglio.progettogamedesign;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.World;

//dynamic bodyes
public class DynamicBodyComponent extends PhysicsComponent{
    private Body body;
    float width, height;
    float x, y;

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
        body.setSleepingAllowed(false);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(box);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0);
        fixtureDef.setDensity(0.6f);
        body.createFixture(fixtureDef);

        bodyDef.delete();
        box.delete();
        fixtureDef.delete();
    }

    @Override
    public void update() {

    }

}
