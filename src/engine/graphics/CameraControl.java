package engine.graphics;

import engine.utils.Coordinate2d;
import static org.lwjgl.opengl.GL11.*;

public class CameraControl {

    public Coordinate2d position;
    
    public CameraControl(Coordinate2d startingPosition) {
        position = new Coordinate2d(startingPosition);
    }
    
    public void walk(Coordinate2d direction) {
        position.sum(direction);
    }
    
    public void jumpTo(Coordinate2d newPosition) {
        position.copy(newPosition);
    }
    
    public void lookThrough() {
        glTranslated(-position.x, -position.y, 0);
    }
}
