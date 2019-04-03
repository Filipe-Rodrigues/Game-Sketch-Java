package engine.application;

import engine.core.Actor;
import engine.core.Level;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static java.lang.Math.*;
import static engine.application.CellContent.*;
import static engine.utils.Coordinate2i.*;
import engine.utils.Pair;

public abstract class PacmanActor extends Actor {

    public static final Coordinate2i RIGHT = new Coordinate2i(1, 0);
    public static final Coordinate2i LEFT = new Coordinate2i(-1, 0);
    public static final Coordinate2i UP = new Coordinate2i(0, -1);
    public static final Coordinate2i DOWN = new Coordinate2i(0, 1);

    public static final Coordinate2d LOWER_BOUND_LIMIT = new Coordinate2d(-GRID_RESOLUTION, -GRID_RESOLUTION);
    public static final Coordinate2d UPPER_BOUND_LIMIT = new Coordinate2d(FIELD_WIDTH + GRID_RESOLUTION, FIELD_HEIGHT + GRID_RESOLUTION);

    protected static final Coordinate2i MID_SUBDIVISION = new Coordinate2i(3, 4);

    private static final double STEP_RATIO = (double) GRID_RESOLUTION / (double) CELL_SUBDIVISION;

    protected final PacmanApplication app;
    protected final Coordinate2i absolutePosition2i;
    protected final Coordinate2i cellSubposition;
    protected final Coordinate2i lastAnalyzedGridPosition;
    protected final Coordinate2i facingDirection = new Coordinate2i(LEFT);
    protected final Coordinate2i turningDirection;
    protected final double maxSpeed = 300;
    protected int spriteCycle = 0;
    protected int spriteFrames;
    protected int spriteCycleIncrement = 1;
    protected int eatenPellets = 0;
    protected double lastCycleTime;
    protected double spriteTimerMult = 70;
    protected double sizeRatio = 14d / (double) GRID_RESOLUTION;
    protected double speedMult;
    protected double defaultSpeedMult;
    protected boolean boomerangMode = true;
    protected boolean analyzedCellSubposition = false;
    protected boolean onCellEnterAnalyze = false;

    public PacmanActor(PacmanApplication app,
            String spriteName, int tileSize, Coordinate2d initialPosition) {
        super(spriteName, tileSize, tileSize);
        lastCycleTime = System.nanoTime();
        movementBlocked = false;
        this.app = app;
        turningDirection = new Coordinate2i(0, 0);
        absolutePosition2i = new Coordinate2i(0, 0);
        cellSubposition = new Coordinate2i(0, 0);
        lastAnalyzedGridPosition = new Coordinate2i(0, 0);
        warpToPosition(initialPosition);
        updateAbsolutePosition2i();
        updateCellSubposition();
    }

    protected void updateSpriteCycle() {
        if (!movementBlocked) {
            long time = System.nanoTime();
            if (time - lastCycleTime >= spriteTimerMult * DELTA) {
                spriteCycle += spriteCycleIncrement;
                if (boomerangMode) {
                    if (spriteCycle >= spriteFrames || spriteCycle < 0) {
                        spriteCycleIncrement *= -1;
                        spriteCycle += 2 * spriteCycleIncrement;
                    }
                } else {
                    if (spriteCycle >= spriteFrames) {
                        spriteCycle = 0;
                    }
                }
                lastCycleTime = time;
            }
        } else {
            if (spriteCycle == 0) {
                spriteCycle += abs(spriteCycleIncrement);
            }
        }
    }

    protected boolean isFacingDirectionOppositeTo(Coordinate2i anotherDirection) {
        return facingDirection.getMultipliedByScalar(-1).equals(anotherDirection);
    }

    public boolean changeWalkDirection(Coordinate2i direction, Level level) {
        if (turningDirection.equals(ORIGIN) && !direction.equals(facingDirection) && canTurnTo(direction, level)) {
            turningDirection.copyCoordinates(direction);
            getNewDirectionVector();
            movementBlocked = false;
            return true;
        }
        return movementBlocked;
    }

    private boolean canTurnTo(Coordinate2i direction, Level level) {
        Pair<CellContent, Coordinate2i>[][] field = ((PacmanLevel) level).getFieldConfiguration();
        Coordinate2i gridPosition2i = level.getGridPosition2i(position);
        gridPosition2i.sum(direction);
        gridPosition2i.normalizeCoordinates(ORIGIN, level.getFieldUpperBoundary());
        if (field[gridPosition2i.x][gridPosition2i.y].getLeft() != BLOCK) {
            if (direction.equals(UP) || direction.equals(RIGHT)) {
                return true;
            }
            if (direction.equals(LEFT)) {
                return cellSubposition.y > 0;
            }
            return cellSubposition.x < 7;
        }
        return false;
    }

    private void getNewDirectionVector() {
        if (isFacingDirectionOppositeTo(turningDirection)
                || facingDirection.equals(turningDirection)
                || cellSubposition.equals(MID_SUBDIVISION)) {
            facingDirection.copyCoordinates(turningDirection);
            turningDirection.copyCoordinates(ORIGIN);
        } else {
            Coordinate2i oldDirection = new Coordinate2i(facingDirection);
            facingDirection.copyCoordinates(turningDirection);
            boolean preMidX = cellSubposition.x < MID_SUBDIVISION.x;
            boolean postMidX = cellSubposition.x > MID_SUBDIVISION.x;
            boolean preMidY = cellSubposition.y < MID_SUBDIVISION.y;
            boolean postMidY = cellSubposition.y > MID_SUBDIVISION.y;
            boolean keepOldDirection = (oldDirection.equals(LEFT) && postMidX)
                    || (oldDirection.equals(RIGHT) && preMidX)
                    || (oldDirection.equals(UP) && postMidY)
                    || (oldDirection.equals(DOWN) && preMidY);
            if (keepOldDirection) {
                turningDirection.copyCoordinates(oldDirection);
            } else {
                turningDirection.copyCoordinates(oldDirection.getMultipliedByScalar(-1));
            }
        }
    }

    public final void warpToPosition(Coordinate2d newPosition) {
        position.copy(newPosition);
    }

    public Coordinate2i getActorDirection() {
        return facingDirection.getCopy();
    }

    public Coordinate2d getActorPosition() {
        return position;
    }

    public Coordinate2i getFieldPosition(PacmanLevel level) {
        return level.getGridPosition2d(position).toCoordinate2i();
    }

    private void updateAbsolutePosition2i() {
        absolutePosition2i.x = (int) (STEP_RATIO * floor(position.x / STEP_RATIO + 1));
        absolutePosition2i.y = (int) (STEP_RATIO * floor(position.y / STEP_RATIO));
    }

    private void updateCellSubposition() {
        Coordinate2i oldCS = new Coordinate2i(cellSubposition);
        cellSubposition.x = (int) floor(position.x / STEP_RATIO) % CELL_SUBDIVISION;
        cellSubposition.y = (int) floor(position.y / STEP_RATIO) % CELL_SUBDIVISION;
        analyzedCellSubposition = !oldCS.equals(cellSubposition);
    }

    @Override
    protected void update(Level level) {
        move(level, speedMult * maxSpeed * DELTA_IN_SECONDS);

        if (!turningDirection.equals(ORIGIN) && analyzedCellSubposition) {
            boolean movingVertical = facingDirection.equals(UP) || facingDirection.equals(DOWN);
            boolean movingHorizontal = facingDirection.equals(LEFT) || facingDirection.equals(RIGHT);
            if ((movingVertical && cellSubposition.x == MID_SUBDIVISION.x) 
                    || (movingHorizontal && cellSubposition.y == MID_SUBDIVISION.y)) {
                turningDirection.copyCoordinates(ORIGIN);
            }
        }
    }

    private void move(Level level, double rate) {
        Pair<CellContent, Coordinate2i>[][] field = ((PacmanLevel) level).getFieldConfiguration();
        Coordinate2i gridPosition = level.getGridPosition2i(position);
        if (!gridPosition.equals(lastAnalyzedGridPosition)) {
            if (onCellEnterAnalyze || cellSubposition.equals(MID_SUBDIVISION)) {
                checkCurrentCell(level, field, gridPosition);
                lastAnalyzedGridPosition.copyCoordinates(gridPosition);
            }
        }
        if (!movementBlocked) {
            position.sum(facingDirection.toCoordinate2D().getScaled(rate));
            position.sum(turningDirection.toCoordinate2D().getScaled(rate));
            position.warpCoordinates(LOWER_BOUND_LIMIT, UPPER_BOUND_LIMIT);
            updateAbsolutePosition2i();
            updateCellSubposition();
            if (turningDirection.equals(ORIGIN)) {
                Coordinate2i nextFieldPos = level.getGridPosition2i(position);
                nextFieldPos.sum(facingDirection);
                nextFieldPos.normalizeCoordinates(ORIGIN, level.getFieldUpperBoundary());
                if (field[nextFieldPos.x][nextFieldPos.y].getLeft() == BLOCK) {
                    if (cellSubposition.equals(MID_SUBDIVISION)) {
                        onMovementBlock();
                    }
                }
            }
        }
    }

    public void setDefaultSpeedMult(double multiplier) {
        defaultSpeedMult = multiplier;
    }
    
    protected abstract void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2i gridPosition);

    protected abstract void onMovementBlock();

    protected abstract int getFacingDirectionShift();

}
