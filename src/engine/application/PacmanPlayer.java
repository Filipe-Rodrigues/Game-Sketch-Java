package engine.application;

import engine.core.Actor;
import engine.core.Level;
import engine.graphics.CameraControl;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static engine.utils.DrawingUtils.*;
import static java.lang.Math.*;
import static engine.application.CellContent.*;
import engine.utils.Pair;

public final class PacmanPlayer extends Actor {

    public static final int FACING_RIGHT = 0;
    public static final int FACING_LEFT = 1;
    public static final int FACING_UP = 2;
    public static final int FACING_DOWN = 3;

    private int facingDirection = FACING_RIGHT;
    private double speedMult = 200;
    private int spriteCycle = 0;
    private double lastCycleTime;
    private double spriteTimerMult = 35;
    private int spriteCycleIncrement = 2;
    private double sizeRatio = 14d / (double) GRID_RESOLUTION;

    public PacmanPlayer() {
        super("pacman_sprite_56.png", 56, 56);
        lastCycleTime = System.nanoTime();
        warpToPosition(new Coordinate2d(FIELD_WIDTH / 2, 32 * 9.5));
        movementBlocked = false;
    }

    private void updateSpriteCycle() {
        if (!movementBlocked) {
            long time = System.nanoTime();
            if (time - lastCycleTime >= spriteTimerMult * DELTA) {
                spriteCycle += spriteCycleIncrement;
                if (spriteCycle > 4 || spriteCycle < 0) {
                    spriteCycleIncrement *= -1;
                    spriteCycle += 2 * spriteCycleIncrement;
                }
                lastCycleTime = time;
            }
        } else {
            if (spriteCycle == 0) {
                spriteCycle += abs(spriteCycleIncrement);
            }
        }
    }

    @Override
    public void draw(CameraControl camera) {
        updateSpriteCycle();
        enableTexture();
        enableTransparency();

        if (spriteCycle == 0) {
            spriteSheet.getSubImage(0, 0).draw((int) position.x - 28, (int) position.y + 28, 56, -56);
        } else {
            spriteSheet.getSubImage(spriteCycle, facingDirection).draw((int) position.x - 28, (int) position.y + 28, 56, -56);
        }
        disableTransparency();
        disableTexture();
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
                gridPosition.y += 1;
                if (field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.x - floor(gridPosition.x));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > 0.4 && checkValue < 0.6) {
                        return true;
                    }
                }
                return false;
            case FACING_DOWN:
                gridPosition.y -= 1;
                if (field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.x - floor(gridPosition.x));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > 0.4 && checkValue < 0.6) {
                        return true;
                    }
                }
                return false;
            case FACING_RIGHT:
                gridPosition.x += 1;
                if (field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.y - floor(gridPosition.y));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > 0.4 && checkValue < 0.6) {
                        return true;
                    }
                }
                return false;
            case FACING_LEFT:
                gridPosition.x -= 1;
                if (field[(int) floor(gridPosition.x)][(int) floor(gridPosition.y)].getLeft() != BLOCK) {
                    double checkValue = abs(gridPosition.y - floor(gridPosition.y));
                    //System.err.println("CHECK: " + checkValue);
                    if (checkValue > 0.4 && checkValue < 0.6) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void warpToPosition(Coordinate2d newPosition) {
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

    @Override
    protected void update(Level level) {
        Pair<CellContent, Coordinate2i>[][] field = ((PacmanLevel) level).getFieldConfiguration();
        Coordinate2d gridPosition = level.getGridPosition(position);
        switch (facingDirection) {
            case FACING_RIGHT:
                if (!movementBlocked) {
                    position.x += speedMult * DELTA_IN_SECONDS;
                    if (position.x > FIELD_WIDTH + OFFSCREEN_LIMIT) {
                        warpToPosition(new Coordinate2d(-OFFSCREEN_LIMIT, position.y));
                    } else {
                        gridPosition.x = floor(gridPosition.x + sizeRatio);
                        gridPosition.y = floor(gridPosition.y);
                        getNormalizedGridPosition(gridPosition, field);
                        CellContent nextCellContent = field[(int) gridPosition.x][(int) gridPosition.y].getLeft();
                        if (nextCellContent == BLOCK) {
                            movementBlocked = true;
                        }
                    }
                }
                break;
            case FACING_LEFT:
                if (!movementBlocked) {
                    position.x -= speedMult * DELTA_IN_SECONDS;
                    if (position.x < -OFFSCREEN_LIMIT) {
                        warpToPosition(new Coordinate2d(FIELD_WIDTH + OFFSCREEN_LIMIT, position.y));
                    } else {
                        gridPosition.x = floor(gridPosition.x - sizeRatio);
                        gridPosition.y = floor(gridPosition.y);
                        getNormalizedGridPosition(gridPosition, field);
                        CellContent nextCellContent = field[(int) gridPosition.x][(int) gridPosition.y].getLeft();
                        if (nextCellContent == BLOCK) {
                            movementBlocked = true;
                        }
                    }
                }
                break;
            case FACING_UP:
                if (!movementBlocked) {
                    position.y += speedMult * DELTA_IN_SECONDS;
                    if (position.y > FIELD_HEIGHT + OFFSCREEN_LIMIT) {
                        warpToPosition(new Coordinate2d(position.x, -OFFSCREEN_LIMIT));
                    } else {
                        gridPosition.x = floor(gridPosition.x);
                        gridPosition.y = floor(gridPosition.y + sizeRatio);
                        getNormalizedGridPosition(gridPosition, field);
                        CellContent nextCellContent = field[(int) gridPosition.x][(int) gridPosition.y].getLeft();
                        if (nextCellContent == BLOCK) {
                            movementBlocked = true;
                        }
                    }
                }
                break;
            case FACING_DOWN:
                if (!movementBlocked) {
                    position.y -= speedMult * DELTA_IN_SECONDS;
                    if (position.y < -OFFSCREEN_LIMIT) {
                        warpToPosition(new Coordinate2d(position.x, FIELD_HEIGHT + OFFSCREEN_LIMIT));
                    } else {
                        gridPosition.x = floor(gridPosition.x);
                        gridPosition.y = floor(gridPosition.y - sizeRatio);
                        getNormalizedGridPosition(gridPosition, field);
                        CellContent nextCellContent = field[(int) gridPosition.x][(int) gridPosition.y].getLeft();
                        if (nextCellContent == BLOCK) {
                            movementBlocked = true;
                        }
                    }
                }
                break;
        }
    }

}
