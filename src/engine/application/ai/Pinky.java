package engine.application.ai;

import engine.application.PacmanActor;
import engine.application.PacmanApplication;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;

public final class Pinky extends PacmanGhost {

    public Pinky(PacmanApplication app) {
        super(app, PINKY, new Coordinate2d(12 * 32, 17.5 * 32), new Coordinate2i(0, 1), true);
    }

    @Override
    public void updatePursuitTargetCell() {
        Coordinate2i target = (Coordinate2i) app.getAttribute("PlayerPosition");
        Coordinate2i direction = (Coordinate2i) app.getAttribute("PlayerDirection");
        target.sum(direction.getMultipliedByScalar(4));
        if (direction.equals(PacmanActor.UP)) {
            target.x -= 4;
        }
        
        currentFieldTarget.copyCoordinates(target);
    }

}
