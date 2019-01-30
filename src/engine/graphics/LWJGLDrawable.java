package engine.graphics;

public abstract class LWJGLDrawable implements Comparable<LWJGLDrawable> {

    private int zOrder;
    private static int Z_ORDER_COUNT = 1000;

    public LWJGLDrawable(int zOrder) {
        this.zOrder = zOrder;
    }
    
    public LWJGLDrawable() {
        this.zOrder = Z_ORDER_COUNT++;
    }
    
    protected final void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }
    
    public abstract void draw(CameraControl camera);
    
    @Override
    public int compareTo(LWJGLDrawable another) {
        return another.zOrder - this.zOrder;
    }
    
    public String toString() {
        return "Z-order: " + zOrder;
    }
}
