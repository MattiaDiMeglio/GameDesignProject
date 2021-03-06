package com.MattiaDiMeglio.progettogamedesign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidFastRenderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.controlwear.virtual.joystick.android.JoystickView;

//The main game screen
public class GameScreen extends Screen {
    enum GameState {//game states
        Ready,
        Running,
        Paused,
        GameOver
    }

    GameWorld gameWorld;//GW
    Graphics graphics;
    List<DrawableComponent> drawables;
    GameState gameState = GameState.Ready;
    Box physicalSize, screenSize;
    AndroidFastRenderView renderView;
    JoystickView leftJoystick, rightJoystick;
    MainMenuScreen mainMenuScreen;
    LoadingScreen loadingScreen;

    int width, height;

    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;//physics world dimensions

    //background coordinates to move the world
    int currentBackgroundX = 0, currentBackgroundY = 0;
    Context context;//android context

    Stack<AimLine> aimLineStack;

    //Joystick variables
    float leftX = 0, leftY = 0, leftAngle = 0, leftStrength = 0;
    float rightX = 0, rightY = 0, oldRightX = 0, oldRightY = 0, rightAngle = 0, rightStrength = 0, oldRightAngle = 0, oldRightStrength = 0;

    boolean isShooting = false;
    boolean isGameFinished = false;

    public GameScreen(Game game, int width, int height, Context context, MainMenuScreen mainMenuScreen, LoadingScreen loadingScreen) {
        super(game);
        this.width = width;
        this.height = height;
        this.context = context;
        this.mainMenuScreen = mainMenuScreen;
        this.loadingScreen = loadingScreen;

        graphics = game.getGraphics();//we get the graphics from the framework, to draw on screen
        //world sizes
        physicalSize = new Box(XMIN, YMIN, XMAX, YMAX);
        screenSize   = new Box(0, 0, width,height);
        //list of active drawables
        drawables = new ArrayList<>();
        gameWorld = new GameWorld(this, context, physicalSize, screenSize);//creates a new GameWorld

        renderView = game.getRenderView();

        aimLineStack = new Stack<>();

        leftJoystick = game.getLeftJoystick();
        rightJoystick = game.getRightJoystick();

        leftJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                leftX = (float) (leftJoystick.getNormalizedX() - 50) / 50;
                leftY = (float) (leftJoystick.getNormalizedY() - 50) / 50;
                leftX = leftX * ((float)height/(float)width);
                leftAngle = angle;
                leftStrength = strength;
            }
        });

        rightJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                oldRightX = rightX;
                oldRightY = rightY;

                float rightVectorX = rightJoystick.getNormalizedX();
                float rightVectorY = rightJoystick.getNormalizedY();

                rightVectorX = (rightVectorX - 50) / 50;
                rightVectorY = (rightVectorY - 50) / 50;

                if(rightVectorX == 0 && rightVectorY == 0){
                    rightX = rightVectorX;
                    rightY = rightVectorY;
                }
                else{
                    float length = (float) Math.sqrt((rightVectorX * rightVectorX) + (rightVectorY * rightVectorY));
                    rightX = rightVectorX/length;
                    rightY = rightVectorY/length;
                }

                rightAngle = angle;
                rightStrength = strength;

                if(!(rightJoystick.getNormalizedX() >= 40 && rightJoystick.getNormalizedX()<=60
                        && rightJoystick.getNormalizedY() >= 40 && rightJoystick.getNormalizedY()<=60)){
                    oldRightAngle = angle;
                    oldRightStrength = strength;
                }

                if(oldRightStrength != 0 && rightStrength == 0)
                    isShooting = true;
            }
        });

        AssetManager.GameMusic.play();
    }

    //gamescreen update, calls the gameworld update
    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();

        switch(gameState) {
            case Ready:
                gameState = GameState.Running;
                break;
            case Running: //if the game is running update the gameworld
                ((Activity)game).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game.getRightJoystick().setVisibility(View.VISIBLE);
                        game.getLeftJoystick().setVisibility(View.VISIBLE);
                    }
                });
                gameWorld.update(deltaTime);
                if(isShooting){
                    isShooting = false;
                }
                setWorldDestination();
                for(int i = 0; i < touchEvents.size(); i++){
                    Input.TouchEvent event = touchEvents.get(i);
                    if(event.type == Input.TouchEvent.TOUCH_DOWN){
                        if(event.x > gameWorld.bufferWidth - AssetManager.PausePixmap.getWidth() - 48 && event.y < 48){
                            gameState = GameState.Paused;
                        }
                    }
                }
                break;
            case Paused:
                ((Activity)game).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game.getRightJoystick().setVisibility(View.GONE);
                        game.getLeftJoystick().setVisibility(View.GONE);
                    }
                });
                for(int i = 0; i < touchEvents.size(); i++){
                    Input.TouchEvent event = touchEvents.get(i);
                    if(event.type == Input.TouchEvent.TOUCH_DOWN){
                        if(event.x > gameWorld.bufferWidth - AssetManager.PausePixmap.getWidth() - 48 && event.y < 48){
                            gameState = GameState.Running;
                        } else if(event.x > gameWorld.bufferWidth/2 - AssetManager.ResumeButtonPixmap.getWidth()/2){
                            if(event.y > gameWorld.bufferHeight/2 - (AssetManager.ResumeButtonPixmap.getHeight() * 2)
                            && event.y < gameWorld.bufferHeight/2 - (AssetManager.ResumeButtonPixmap.getHeight() * 2)
                                    + AssetManager.ResumeButtonPixmap.getHeight()){
                                gameState = GameState.Running;
                            } else if(event.y > gameWorld.bufferHeight/2 + (AssetManager.ExitButtonPixmap.getHeight() * 2 - AssetManager.ExitButtonPixmap.getHeight())
                                    && event.y < gameWorld.bufferHeight/2 + (AssetManager.ExitButtonPixmap.getHeight() * 2)){
                                gameWorld.destroyGameWorld();
                                currentBackgroundY = 0;
                                currentBackgroundX = 0;

                                game.setScreen(mainMenuScreen);
                            }
                        }
                    }
                }
                break;
            case GameOver:
                ((Activity)game).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game.getRightJoystick().setVisibility(View.GONE);
                        game.getLeftJoystick().setVisibility(View.GONE);
                    }
                });

                if(AssetManager.GameMusic.isPlaying())
                    AssetManager.GameMusic.stop();
                for(int i = 0; i < touchEvents.size(); i++){
                    Input.TouchEvent event = touchEvents.get(i);
                    if(event.type == Input.TouchEvent.TOUCH_DOWN){
                        if(event.x > gameWorld.bufferWidth/2 - AssetManager.EndLevelPixmap.getWidth()/2 && event.x < gameWorld.bufferWidth/2 + AssetManager.EndLevelPixmap.getWidth()/2
                        && event.y > gameWorld.bufferHeight/2 - AssetManager.EndLevelPixmap.getHeight()/2 && event.y < gameWorld.bufferHeight/2 + AssetManager.EndLevelPixmap.getHeight()/2){
                            if(!isGameFinished) {
                                loadingScreen.setNonCreated();
                                currentBackgroundY = 0;
                                currentBackgroundX = 0;
                                game.setScreen(loadingScreen);
                            } else {
                                gameWorld.destroyGameWorld();
                                currentBackgroundY = 0;
                                currentBackgroundX = 0;

                                game.setScreen(mainMenuScreen);
                            }
                        }
                    }
                }
                break;
        }
    }
    @Override
    public void pause() {
        if(AssetManager.GameMusic.isPlaying())
            AssetManager.GameMusic.stop();
    }

    @Override
    public void resume() {
        if(AssetManager.GameMusic.isStopped())
            AssetManager.GameMusic.play();
    }

    @Override
    public void dispose() { }

    //the methods to draw on screen
    @Override
    public void present(float deltaTime) {
        graphics.clear(Color.WHITE);

        //draw the background
        graphics.drawPixmap(AssetManager.background, currentBackgroundX - 250, currentBackgroundY - 250);
        graphics.drawPixmap(AssetManager.backgroundPixmap, currentBackgroundX, currentBackgroundY);
        //draw the drawables
        if(!drawables.isEmpty()) {
            for (DrawableComponent drawable : drawables) {
                drawable.Draw(graphics);
            }
        }
        drawAimLines();
        //drawBodies();
        if(gameState == GameState.Running) {
            graphics.drawPixmap(AssetManager.PausePixmap, gameWorld.bufferWidth - AssetManager.PausePixmap.getWidth() - 16, 16);
            if(gameWorld.player.isReloading()){
                graphics.drawText("Reloading...", (gameWorld.bufferWidth) - (int) (0.14 * gameWorld.bufferWidth), gameWorld.bufferHeight - (int) (0.25 * gameWorld.bufferHeight), Color.BLACK);

            } else
                graphics.drawText(gameWorld.player.getCurrentProjectiles() + "/" + gameWorld.player.getMaxProjectiles(), (gameWorld.bufferWidth) - (int) (0.114 * gameWorld.bufferWidth), gameWorld.bufferHeight - (int) (0.25 * gameWorld.bufferHeight), Color.BLACK);

        }
        if(gameState == GameState.Paused){
            graphics.drawPixmap(AssetManager.PlayPixmap, gameWorld.bufferWidth - AssetManager.PausePixmap.getWidth() - 16, 16);
            graphics.drawPixmap(AssetManager.ResumeButtonPixmap, gameWorld.bufferWidth /2 - AssetManager.ResumeButtonPixmap.getWidth()/2, gameWorld.bufferHeight/2 - (AssetManager.ResumeButtonPixmap.getHeight() * 2));
            graphics.drawPixmap(AssetManager.ExitButtonPixmap, gameWorld.bufferWidth /2 - AssetManager.ResumeButtonPixmap.getWidth()/2, gameWorld.bufferHeight/2 + (AssetManager.ResumeButtonPixmap.getHeight()/2) + AssetManager.ResumeButtonPixmap.getHeight()/2);
        }
        if(gameState == GameState.GameOver){
            if(gameWorld.player.killed){
                graphics.drawPixmap(AssetManager.PlayerDeadPixmap, gameWorld.bufferWidth / 2 - AssetManager.EndLevelPixmap.getWidth() / 2, gameWorld.bufferHeight / 2 - AssetManager.EndLevelPixmap.getHeight() / 2);

            } else if (!isGameFinished)
                graphics.drawPixmap(AssetManager.EndLevelPixmap, gameWorld.bufferWidth / 2 - AssetManager.EndLevelPixmap.getWidth() / 2, gameWorld.bufferHeight / 2 - AssetManager.EndLevelPixmap.getHeight() / 2);
              else
                graphics.drawPixmap(AssetManager.EndGamePixmap, gameWorld.bufferWidth / 2 - AssetManager.EndLevelPixmap.getWidth() / 2, gameWorld.bufferHeight / 2 - AssetManager.EndLevelPixmap.getHeight() / 2);
        }
        graphics.drawText(gameWorld.enemyNum + "/" + gameWorld.totalEnemies + " remaining", (gameWorld.bufferWidth/2) - 20, 10, Color.BLACK);
    }

    //for testing, draws objects bodies
    private void drawBodies(){
        for(GameObject gameObject : gameWorld.activeGameObjects){
            if(gameObject.components.containsKey(ComponentType.Physics)){
                PhysicsComponent comp = (PhysicsComponent) gameObject.getComponent(ComponentType.Physics);
                int color = Color.WHITE;
                switch (comp.name) {
                    case "Enemy":
                        color = Color.RED;
                        break;
                    case "Wall":
                        color = Color.GREEN;
                        break;
                    case "HalfWall":
                        color = Color.CYAN;
                        break;
                    case "Player":
                        color = Color.BLUE;
                        break;
                }
                comp.Draw(graphics, gameWorld, color);
            }
        }
    }

    public void drawAimLines(){
        while(!aimLineStack.isEmpty()){
            AimLine aimLine = aimLineStack.pop();
            graphics.drawLine(aimLine.startX, aimLine.startY, aimLine.targetX, aimLine.targetY, aimLine.color);
        }
    }


    //adds and remove drawables
    public void addDrawable(DrawableComponent drawable){
        drawables.add(drawable);
    }

    public void removeDrawable(DrawableComponent drawable){
        drawables.remove(drawable);
    }

    //sets the destination for the world movement. It moves the map and all the GO other than the player
    public void setWorldDestination(){
        gameWorld.player.CantMove();
        currentBackgroundX = -(gameWorld.player.worldX - gameWorld.bufferWidth/2);
        currentBackgroundY = -(gameWorld.player.worldY - gameWorld.bufferHeight/2);
        for (DrawableComponent drawable : drawables) {
            GameObject gameObject = drawable.owner;
                gameObject.updatePosition(gameWorld.inViewPositionX(gameObject.worldX),
                        gameWorld.inViewPositionY(gameObject.worldY));
        }
        gameWorld.player.CanMove();
    }

    public float getLeftX() {
        return leftX;
    }

    public float getLeftY() {
        return leftY;
    }

    public float getLeftAngle() { return leftAngle; }
    
    public float getRightX(){
        return  rightX;
    }

    public float getRightY() {
        return rightY;
    }

    public float getRightAngle() {
        return rightAngle;
    }

    public float getRightStrength() {
        return rightStrength;
    }

    public float getOldRightX() {
        return oldRightX;
    }

    public float getOldRightY() {
        return oldRightY;
    }

    public float getOldRightAngle() {
        return oldRightAngle;
    }

    public float getOldRightStrength() {
        return oldRightStrength;
    }

    public boolean isShooting() { return isShooting; }

    //background x and y
    public float getBackgroundX(){return currentBackgroundX;}
    public float getBackgroundY(){return currentBackgroundY;}

    public void levelEnded(){
        if(gameWorld.enemyNum == 0){
            if(gameWorld.level < 5) {
                gameWorld.level++;
            }else {
                isGameFinished = true;
            }
        } else if(gameWorld.player.killed){
            gameWorld.level = 1;
        }
        gameState = GameState.GameOver;
        gameWorld.destroyGameWorld();
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
