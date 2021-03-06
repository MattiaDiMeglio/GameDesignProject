package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowMetrics;
import android.widget.RelativeLayout;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;

import io.github.controlwear.virtual.joystick.android.JoystickView;

//then entry point
public class ProgettoGameDesign extends AndroidGame {
    public int height, width;
    JoystickView leftJ;
    JoystickView rightJ;
    @Override
    public Screen getStartScreen() {
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

        int jWidth =(int)(0.120 * width);
        int jHeight = (int)(0.22 * height);
        int left = (int)(0.025 * width);
        int top =  height - (int)(0.248 * height);
        int right = 0;
        int bottom = 0;

        RelativeLayout.LayoutParams leftjParams=new RelativeLayout.LayoutParams(jWidth,jHeight);
        leftjParams.setMargins(left,top,right,bottom);
        leftJ.setLayoutParams(leftjParams);

        relativeLayout.addView(leftJ);

        rightJ = new JoystickView(this);
        rightJ.setButtonColor(Color.YELLOW);
        rightJ.setBackgroundColor(Color.BLACK);

        int rleft = width - jWidth - left;

        RelativeLayout.LayoutParams rightjParams=new RelativeLayout.LayoutParams(jWidth,jHeight);
        rightjParams.setMargins(rleft,top,right,bottom);
        rightJ.setLayoutParams(rightjParams);

        relativeLayout.addView(rightJ);
        rightJ.setVisibility(View.GONE);
        leftJ.setVisibility(View.GONE);

        setContentView(relativeLayout);

        return new MainMenuScreen(this, width, height, getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loads the libs
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");
        //fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();

            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
                controller.hide(WindowInsets.Type.navigationBars());
            }
        }
        else {
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    public JoystickView getLeftJoystick() { return leftJ; }

    public JoystickView getRightJoystick() { return rightJ; }

    public void ExitGame(){
        this.finishAffinity();
    }
}
