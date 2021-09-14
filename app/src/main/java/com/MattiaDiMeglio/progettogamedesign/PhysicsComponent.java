package com.MattiaDiMeglio.progettogamedesign;

abstract class PhysicsComponent extends Component{
    public String name;
    @Override
    public ComponentType getType() {
        return ComponentType.Physics;
    }

    public abstract void update();
}
