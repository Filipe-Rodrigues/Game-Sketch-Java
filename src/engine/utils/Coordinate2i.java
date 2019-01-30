package engine.utils;

import static java.lang.Math.*;

public class Coordinate2i {
    public int x;
    public int y;
    
    public Coordinate2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate2i(Coordinate2i another) {
        this.x = another.x;
        this.y = another.y;
    }
    
    public void update(Coordinate2d continuous) {
        this.x = (int) floor(continuous.x);
        this.y = (int) floor(continuous.y);
    }
    
    public void update(Coordinate2d continuous, int gridStepX, int gridStepY) {
        this.x = (int) floor(continuous.x / gridStepX) * gridStepX;
        this.y = (int) floor(continuous.y / gridStepY) * gridStepY;
    }
    
    public Coordinate2i getScaled(int factor) {
        return new Coordinate2i(x / factor, y / factor);
    }
    
    public void copyCoordinates(Coordinate2i another) {
        this.x = another.x;
        this.y = another.y;
    }
    
    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
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
