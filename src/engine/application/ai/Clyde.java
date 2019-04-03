package engine.application.ai;

import engine.application.PacmanApplication;
import engine.application.PacmanLevel;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;

public final class Clyde extends PacmanGhost {

    public Clyde(PacmanApplication app) {
        super(app, CLYDE, new Coordinate2d(16 * 32, 17.5 * 32), new Coordinate2i(0, 35), true);
    }

    @Override
    protected void updatePursuitTargetCell() {
        Coordinate2i target = (Coordinate2i) app.getAttribute("PlayerPosition");
        PacmanLevel level = (PacmanLevel) app.getAttribute("CurrentLevel");
                if (getFieldPosition(level).getSquaredEuclideanDistance(target) > 64) {
                    currentFieldTarget.copyCoordinates(target);
                } else {
                    currentFieldTarget.copyCoordinates(homeFieldPosition);
                }
    }
    
}
