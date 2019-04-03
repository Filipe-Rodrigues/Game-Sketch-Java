package engine.utils;

import java.io.Serializable;
import static java.lang.Math.*;

public class Coordinate2i implements Serializable {

    public static final Coordinate2i ORIGIN = new Coordinate2i(0, 0);
    
    public int x;
    public int y;

    public static Coordinate2i sum(Coordinate2i vector1, Coordinate2i vector2) {
        return new Coordinate2i(vector1.x + vector2.x, vector1.y + vector2.y);
    }

    public Coordinate2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate2i(Coordinate2i another) {
        this.x = another.x;
        this.y = another.y;
    }

    public Coordinate2d toCoordinate2D() {
        return new Coordinate2d(x, y);
    }
    
    public void update(Coordinate2d continuous) {
        this.x = (int) floor(continuous.x);
        this.y = (int) floor(continuous.y);
    }

    public void update(Coordinate2d continuous, int gridStepX, int gridStepY) {
        this.x = (int) floor(continuous.x / gridStepX) * gridStepX;
        this.y = (int) floor(continuous.y / gridStepY) * gridStepY;
    }

    public Coordinate2i getDividedByScalar(int factor) {
        return new Coordinate2i(x / factor, y / factor);
    }

    public Coordinate2i getMultipliedByScalar(int factor) {
        return new Coordinate2i(x * factor, y * factor);
    }
    
    public void sum(Coordinate2i another) {
        this.x += another.x;
        this.y += another.y;
    }
    
    public void mult(int factor) {
        this.x *= factor;
        this.y *= factor;
    }

    public void copyCoordinates(Coordinate2i another) {
        this.x = another.x;
        this.y = another.y;
    }

    public Coordinate2i getCopy() {
        return new Coordinate2i(x, y);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public int getManhattanDistance(Coordinate2i destiny) {
        return abs(this.x - destiny.x) + abs(this.y - destiny.y);
    }
    
    public double getSquaredEuclideanDistance(Coordinate2i destiny) {
        return pow(this.x - destiny.x, 2) + pow(this.y - destiny.y, 2);
    }

    public boolean isFloorLimitedBy(Coordinate2i limit) {
        return this.x >= limit.x && this.y >= limit.y;
    }

    public boolean isCeilLimitedBy(Coordinate2i limit) {
        return this.x <= limit.x && this.y <= limit.y;
    }
    
    public boolean isCoordinateBetween(Coordinate2i limit1, Coordinate2i limit2) {
        return isFloorLimitedBy(limit1) && isCeilLimitedBy(limit2);
    }
    
    public void normalizeCoordinates(Coordinate2i limit1, Coordinate2i limit2) {
        int xMin = min(limit1.x, limit2.x);
        int yMin = min(limit1.y, limit2.y);
        int xSize = abs(limit1.x - limit2.x) + 1;
        int ySize = abs(limit1.y - limit2.y) + 1;
        int xShift = floorMod(xMin, xSize);
        int yShift = floorMod(yMin, ySize);
        this.x = xMin + floorMod((this.x - xShift), xSize);
        this.y = yMin + floorMod((this.y - yShift), ySize);
    }

    public boolean equals(Coordinate2i another) {
        if (another != null) {
            if (this == another) {
                return true;
            }
            return this.x == another.x && this.y == another.y;
        }
        return false;
    }

}
