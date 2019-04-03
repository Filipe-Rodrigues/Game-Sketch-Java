package engine.application.ai;

import engine.application.PacmanApplication;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static engine.utils.Coordinate2i.sum;

public final class Inky extends PacmanGhost {

    public Inky(PacmanApplication app) {
        super(app, INKY, new Coordinate2d(14 * 32, 17.5 * 32), new Coordinate2i(27, 35), true);
    }

    @Override
    public void updatePursuitTargetCell() {
        Coordinate2i target = (Coordinate2i) app.getAttribute("PlayerPosition");
        Coordinate2i direction = (Coordinate2i) app.getAttribute("PlayerDirection");
        Coordinate2i blinkyPos = (Coordinate2i) app.getAttribute("BlinkyPosition");
        if (direction.equals(UP)) {
            direction.x = -1;
        }
        target.sum(direction.getMultipliedByScalar(2));
        int xMid = (target.x - blinkyPos.x);
        int yMid = (target.y - blinkyPos.y);
        currentFieldTarget = sum(target, new Coordinate2i(xMid, yMid));
    }

}
