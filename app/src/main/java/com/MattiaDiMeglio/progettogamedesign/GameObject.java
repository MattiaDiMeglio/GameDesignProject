package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

//the GO main class
public class GameObject {
    protected Map<ComponentType, Component> components;
    protected String name;
    protected int worldX, worldY;

    public void addComponent(Component component) {
        if (components == null){
            components  = new HashMap<>();
        }
        component.setOwner(this);
        components.put(component.getType(), component);
        Log.d("Debug", "lunghezza " + components.size());
    }

    public void updatePosition(int x, int y){};

    public void outOfView(){}

    public Component getComponent(ComponentType componentType){
        return components.get(componentType);
    }
}
