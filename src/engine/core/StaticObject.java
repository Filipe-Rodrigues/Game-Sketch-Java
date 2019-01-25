package engine.core;

import engine.graphics.LWJGLDrawable;

public abstract class StaticObject extends LWJGLDrawable {
    
    public StaticObject() {
        super();
    }
    
    public StaticObject(int zOrder) {
        super(zOrder);
    }
    
}
