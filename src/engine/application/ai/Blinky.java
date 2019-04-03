package engine.application.ai;

import engine.application.CellContent;
import engine.application.PacmanApplication;
import engine.core.Level;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import engine.utils.Pair;

public final class Blinky extends PacmanGhost {

    public Blinky(PacmanApplication app) {
        super(app, BLINKY, new Coordinate2d(14 * 32, 14.5 * 32), new Coordinate2i(27, 1), true);
    }

    @Override
    public void updatePursuitTargetCell() {
        Coordinate2i target = (Coordinate2i) app.getAttribute("PlayerPosition");
        currentFieldTarget.copyCoordinates(target);
    }
}
