package engine.graphics;

import engine.utils.Coordinate2d;

public abstract class LWJGLFigure extends LWJGLDrawable {
    
    protected final Coordinate2d originPosition;
    protected final Coordinate2d LL;
    protected final Coordinate2d UR;
    protected double angle;

    public LWJGLFigure(int zOrder, Coordinate2d origin, Coordinate2d LL, Coordinate2d UR) {
        super(zOrder);
        this.originPosition = new Coordinate2d(origin);
        this.LL = new Coordinate2d(LL);
        this.UR = new Coordinate2d(UR);
        this.angle = 0;
    }

    public LWJGLFigure(int zOrder, Coordinate2d origin, Coordinate2d LL, Coordinate2d UR, double angle) {
        this(zOrder, origin, LL, UR);
        this.angle = angle;
    }

    public Coordinate2d getOriginPosition() {
        return originPosition;
    }

    public Coordinate2d getLL() {
        return LL;
    }

    public Coordinate2d getUR() {
        return UR;
    }
    
    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
    
}
