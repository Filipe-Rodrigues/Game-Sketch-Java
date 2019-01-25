package engine.utils;

import org.newdawn.slick.Color;

public class Line {

    public final Coordinate2d origin;
    public final Coordinate2d vector;
    public final Color color;

    public Line(Coordinate2d origin, Coordinate2d vector, Color color) {
        this.origin = new Coordinate2d(origin);
        this.vector = new Coordinate2d(vector);
        this.color = color;
    }

}
