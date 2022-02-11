package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;


public class mRayCastCallback {
    World world;
    private Fixture rayCastFixture;
    private Fixture lineOfFireFixture;

    public mRayCastCallback(World world){
        this.world = world;
    }

    public Fixture checkRaycast (float bodyX, float bodyY, float aimX, float aimY, String shooter, GridManager levelGrid){//does the raycast callback

        //raycast override. if the cast gets from the player to the enemy, we destroy the enemy
        com.google.fpl.liquidfun.RayCastCallback rayCastCallback = new com.google.fpl.liquidfun.RayCastCallback() {
            @Override
            public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
                rayCastFixture = fixture;//raycast callback
                Body castedBody = fixture.getBody();
                PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();
                if(casteduserData.name.equals("HalfWall")){
                    return 1;
                }
                return fraction;
            }
        };

        float targetX = bodyX + (aimX);
        float targetY = bodyY + (aimY);

        Log.d("raycast", "body: " + bodyX + ", " + bodyY + " and target: " + targetX + ", " + targetY);
        world.rayCast(rayCastCallback, bodyX, bodyY, targetX, targetY);//calls the raycast

        Fixture returnFixture = rayCastFixture;

        if (rayCastFixture != null) {//if the ray met a fixture
            Body castedBody = rayCastFixture.getBody();//we get the body
            PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();//we get the component
            if (casteduserData != null) {//if there's user data
                Log.d("Raycast", "hit : " + casteduserData.name);
                switch (casteduserData.name) {
                    case "Enemy"://raycast met an enemy first
                        if(shooter.equals("Enemy"))
                            break;
                        EnemyGameObject enemyGameObject = (EnemyGameObject) casteduserData.getOwner();
                        enemyGameObject.killed(levelGrid.getCells());
                        //destroy enemy
                        break;
                    case "Player":
                        /*PlayerGameObject playerGameObject = (PlayerGameObject) casteduserData.getOwner();
                        playerGameObject.killed();*/
                        break;
                    case "Wall"://met a wall
                        //hit wall
                        break;
                    case "Door"://met a door
                        //open door
                        break;
                    case "HalfWall":
                        //Non dovrebbe mai succedere
                        break;
                    case "Box":
                        BoxGameObject boxGameObject = (BoxGameObject) casteduserData.getOwner();
                        boxGameObject.Damage();
                    default:
                        Log.d("RaycastEvent", "raycast object with no name");
                        break;
                }
                rayCastFixture = null;
                Log.d("Raycast", "Raycast terminato");
            }
        }
        return returnFixture;
    }

    public boolean checkLineOfFire(float bodyX, float bodyY, float aimX, float aimY){
        com.google.fpl.liquidfun.RayCastCallback rayCastCallback = new com.google.fpl.liquidfun.RayCastCallback() {
            @Override
            public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
                lineOfFireFixture = fixture;
                Body castedBody = fixture.getBody();
                PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();
                return fraction;
            }
        };

        float targetX = bodyX + (aimX);
        float targetY = bodyY + (aimY);

        world.rayCast(rayCastCallback, bodyX, bodyY, targetX, targetY);

        boolean freeLineOfFire = false;

        if (lineOfFireFixture != null) {
            Body castedBody = lineOfFireFixture.getBody();
            PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();
            if (casteduserData != null) {
                switch (casteduserData.name) {
                    case "Player":
                        freeLineOfFire = true;
                        break;
                    default:
                        freeLineOfFire = false;
                        break;
                }
                lineOfFireFixture = null;
            }
        }
        return freeLineOfFire;
    }

}
