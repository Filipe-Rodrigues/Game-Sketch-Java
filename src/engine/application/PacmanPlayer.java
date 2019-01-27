package engine.application;

import engine.utils.Coordinate2d;
import static engine.utils.Constants.*;

public class PacmanPlayer extends PacmanActor {
    
    public PacmanPlayer(PacmanApplication app) {
        super(app, 0.4, 0.6, "spriteTest.png", 60, new Coordinate2d(FIELD_WIDTH / 2, 32 * 26.5));
    }
    
}
