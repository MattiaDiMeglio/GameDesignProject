package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

public class PhysicsContactListener extends ContactListener {


    @Override
    public void beginContact(Contact contact) {
        //super.beginContact(contact);
        Log.d("collision", "Begin collision");
        if(contact.getFixtureA() != null && contact.getFixtureB()!=null){
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            Body bodyA = fixtureA.getBody();
            Body bodyB = fixtureB.getBody();
            Object userDataA = bodyA.getUserData();
            Object userDataB = bodyB.getUserData();
            if(userDataA != null && userDataB!=null){
                PhysicsComponent gameObjectA = (PhysicsComponent) userDataA;
                PhysicsComponent gameObjectB = (PhysicsComponent) userDataB;
                Log.d("Collision", "GameObject: " + gameObjectA.name + " and GameObject: " + gameObjectB.name + " collided");
            } else {
                Log.d("Collision", "null user data");
            }
        } else {
            Log.d("Collision", "null fixture");
        }
    }


}
