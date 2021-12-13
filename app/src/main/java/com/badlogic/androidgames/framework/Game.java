package com.badlogic.androidgames.framework;

import com.badlogic.androidgames.framework.impl.AndroidFastRenderView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public interface Game {
    public Input getInput();

    public FileIO getFileIO();

    public Graphics getGraphics();

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();

    public AndroidFastRenderView getRenderView();

    public JoystickView getLeftJoystick();

    public JoystickView getRightJoystick();
}