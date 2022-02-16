package com.MattiaDiMeglio.progettogamedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.RelativeLayout;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class GamePlayActivity extends AndroidGame {
    public int height, width;
    JoystickView leftJ;
    JoystickView rightJ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets the fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            height = windowMetrics.getBounds().height();
            width = windowMetrics.getBounds().width();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = getWindowManager().getDefaultDisplay().getHeight();
            width = getWindowManager().getDefaultDisplay().getWidth();
        }

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.addView(getRenderView());

        leftJ = new JoystickView(this);
        leftJ.setButtonColor(Color.RED);
        leftJ.setBackgroundColor(Color.BLACK);
        //joystickView.setBackgroundSizeRatio(0.2f);
        //joystickView.setButtonSizeRatio(0.1f);

        int jWidth = 300;
        int jHeight = 300;
        int left = 50;
        int top =  height - 300;
        int right = 0;
        int bottom = 0;

        RelativeLayout.LayoutParams leftjParams=new RelativeLayout.LayoutParams(jWidth,jHeight);
        leftjParams.setMargins(left,top,right,bottom);
        leftJ.setLayoutParams(leftjParams);

        relativeLayout.addView(leftJ);

        rightJ = new JoystickView(this);
        rightJ.setButtonColor(Color.YELLOW);
        rightJ.setBackgroundColor(Color.BLACK);
        //joystickView.setBackgroundSizeRatio(0.2f);
        //joystickView.setButtonSizeRatio(0.1f);

        //int left = 50;
        int rleft = width - jWidth - left;


        RelativeLayout.LayoutParams rightjParams=new RelativeLayout.LayoutParams(jWidth,jHeight);
        rightjParams.setMargins(rleft,top,right,bottom);
        rightJ.setLayoutParams(rightjParams);

        relativeLayout.addView(rightJ);

        setContentView(relativeLayout);

    }

    @Override
    public Screen getStartScreen() {
        return null;
    }

    @Override
    public JoystickView getLeftJoystick() {
        return null;
    }

    @Override
    public JoystickView getRightJoystick() {
        return null;
    }
}