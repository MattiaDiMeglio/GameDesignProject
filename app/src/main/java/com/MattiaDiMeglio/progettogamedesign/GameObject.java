package com.MattiaDiMeglio.progettogamedesign;

import java.util.HashMap;
import java.util.Map;

//the GO main class
public class GameObject {
    protected Map<ComponentType, Component> components;//the list of components
    protected String name;//object name
    protected int worldX, worldY;//position on the map

    public void addComponent(Component component) {
        if (components == null){
            components  = new HashMap<>();
        }
        component.setOwner(this);
        components.put(component.getType(), component);
    }

    public void removeComponent(ComponentType componentType){
        if(components == null || componentType == null){
            return;
        }
        components.remove(componentType);
    }

    public void updatePosition(int x, int y){};

    public void updatePosition(float x, float y, float angle){};

    //public void updatePosition(int x, int y, int angle, int strength){};

    public void outOfView(){}

    public Component getComponent(ComponentType componentType){
        return components.get(componentType);
    }

    public void update(){}
}
