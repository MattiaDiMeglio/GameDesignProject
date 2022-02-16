package com.MattiaDiMeglio.progettogamedesign;

import android.view.View;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.impl.AndroidGame;

public class JoystickRunnable implements Runnable {
    boolean isVisible;
    Game game;
    public JoystickRunnable(Game game, boolean isVisible){
        this.game = game;
        this.isVisible = isVisible;
    }
    @Override
    public void run() {
        if(isVisible){
            game.getRightJoystick().setVisibility(View.VISIBLE);
        } else {
            game.getRightJoystick().setVisibility(View.INVISIBLE);
        }
    }
}
