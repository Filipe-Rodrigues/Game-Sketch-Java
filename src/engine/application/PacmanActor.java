package engine.application;

import engine.core.Actor;
import engine.core.Level;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static java.lang.Math.*;
import static engine.application.CellContent.*;
import engine.utils.Pair;

public abstract class PacmanActor extends Actor {

    public static final int FACING_RIGHT = 0;
    public static final int FACING_LEFT = 1;
    public static final int FACING_UP = 2;
    public static final int FACING_DOWN = 3;

    protected final PacmanApplication app;
    protected int facingDirection = FACING_LEFT;
    protected double speedMult = 225;
    protected int spriteCycle = 0;
    protected double lastCycleTime;
    protected int spriteFrames;
    protected double spriteTimerMult = 70;
    protected int spriteCycleIncrement = 1;
    protected double sizeRatio = 14d / (double) GRID_RESOLUTION;
    protected int eatenPellets = 0;
    protected double minMoveThreshold;
    protected double maxMoveThreshold;
    protected boolean boomerangMode = true;

    public PacmanActor(PacmanApplication app,
            String spriteName, int tileSize, Coordinate2d initialPosition) {
        //super("pacman_sprite_56.png", 56, 56);
        super(spriteName, tileSize, tileSize);
        lastCycleTime = System.nanoTime();
        //warpToPosition(new Coordinate2d(FIELD_WIDTH / 2, 32 * 26.5));
        warpToPosition(initialPosition);
        movementBlocked = false;
        this.app = app;
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

    public boolean changeWalkDirection(int direction, Level level) {
        if (direction != facingDirection && !isWarping() && canTurnTo(direction, level)) {
            facingDirection = direction;
            movementBlocked = false;
            return true;
        }
        return movementBlocked;
    }

    private boolean canTurnTo(int direction, Level level) {
        Pair<CellContent, Coordinate2i>[][] field = ((PacmanLevel) level).getFieldConfiguration();
        Coordinate2d gridPosition = level.getGridPosition(position);
        switch (direction) {
            case FACING_UP:
                gridPosition.y -= 1;
                if (gridPosition.y < 0 || field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.x - floor(gridPosition.x));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > minMoveThreshold && checkValue < maxMoveThreshold) {
                        return true;
                    }
                }
                return false;
            case FACING_DOWN:
                gridPosition.y += 1;
                if (gridPosition.y >= level.getFieldSize().y || field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.x - floor(gridPosition.x));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > minMoveThreshold && checkValue < maxMoveThreshold) {
                        return true;
                    }
                }
                return false;
            case FACING_RIGHT:
                gridPosition.x += 1;
                if (gridPosition.x >= level.getFieldSize().x || field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.y - floor(gridPosition.y));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > minMoveThreshold && checkValue < maxMoveThreshold) {
                        return true;
                    }
                }
                return false;
            case FACING_LEFT:
                gridPosition.x -= 1;
                if (gridPosition.x < 0 || field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.y - floor(gridPosition.y));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > minMoveThreshold && checkValue < maxMoveThreshold) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public final void warpToPosition(Coordinate2d newPosition) {
        position.copy(newPosition);
    }

    private boolean isWarping() {
        return (position.x < 0 || position.x >= FIELD_WIDTH
                || position.y < 0 || position.y >= FIELD_HEIGHT);
    }

    private void getNormalizedGridPosition(Coordinate2d gridPosition, Pair<CellContent, Coordinate2i>[][] field) {
        if (gridPosition.x >= field.length) {
            gridPosition.x = 0;
        }
        if (gridPosition.x < 0) {
            gridPosition.x = field.length - 1;
        }
        if (gridPosition.y >= field[0].length) {
            gridPosition.x = 0;
        }
        if (gridPosition.y < 0) {
            gridPosition.x = field[0].length - 1;
        }
    }

    public int getActorDirection() {
        return facingDirection;
    }
    
    public Coordinate2d getActorPosition() {
        return position;
    }
    
    public Coordinate2i getFieldPosition(PacmanLevel level) {
        return level.getGridPosition(position).toCoordinate2i();
    }

    @Override
    protected void update(Level level) {
        switch (facingDirection) {
            case FACING_RIGHT:
                move(level, speedMult * DELTA_IN_SECONDS, 'x');
                break;
            case FACING_LEFT:
                move(level, -speedMult * DELTA_IN_SECONDS, 'x');
                break;
            case FACING_UP:
                move(level, -speedMult * DELTA_IN_SECONDS, 'y');
                break;
            case FACING_DOWN:
                move(level, speedMult * DELTA_IN_SECONDS, 'y');
                break;
        }
    }

    private void move(Level level, double rate, char axis) {
        Pair<CellContent, Coordinate2i>[][] field = ((PacmanLevel) level).getFieldConfiguration();
        Coordinate2d gridPosition = level.getGridPosition(position);
        if (gridPosition.x >= 0 && gridPosition.x < level.getFieldSize().x
                && gridPosition.y >= 0 && gridPosition.y < level.getFieldSize().y) {
            checkCurrentCell(level, field, gridPosition);
        }
        if (!movementBlocked) {
            boolean warped = false;
            if (axis == 'x') {
                position.x += rate;
                if (position.x < -OFFSCREEN_LIMIT) {
                    warpToPosition(new Coordinate2d(FIELD_WIDTH + OFFSCREEN_LIMIT, position.y));
                    warped = true;
                } else if (position.x > FIELD_WIDTH + OFFSCREEN_LIMIT) {
                    warpToPosition(new Coordinate2d(-OFFSCREEN_LIMIT, position.y));
                    warped = true;
                }
            } else {
                position.y += rate;
                if (position.y < -OFFSCREEN_LIMIT) {
                    warpToPosition(new Coordinate2d(position.x, FIELD_HEIGHT + OFFSCREEN_LIMIT));
                    warped = true;
                } else if (position.y > FIELD_HEIGHT + OFFSCREEN_LIMIT) {
                    warpToPosition(new Coordinate2d(position.x, -OFFSCREEN_LIMIT));
                    warped = true;
                }
            }
            if (!warped) {
                if (axis == 'x') {
                    gridPosition.x = floor(gridPosition.x + sizeRatio * (rate / abs(rate)));
                    gridPosition.y = floor(gridPosition.y);
                } else {
                    gridPosition.x = floor(gridPosition.x);
                    gridPosition.y = floor(gridPosition.y + sizeRatio * (rate / abs(rate)));
                }
                getNormalizedGridPosition(gridPosition, field);
                CellContent nextCellContent = field[(int) gridPosition.x][(int) gridPosition.y].getLeft();
                if (nextCellContent == BLOCK) {
                    onMovementBlock();
                }
            }
        }
    }

    protected abstract void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2d gridPosition);

    protected abstract void onMovementBlock();

}
