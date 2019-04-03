package engine.application.ai;

import engine.application.CellContent;
import static engine.application.CellContent.*;
import engine.application.PacmanActor;
import engine.application.PacmanApplication;
import engine.core.Level;
import engine.graphics.CameraControl;
import static engine.utils.Constants.GRID_RESOLUTION;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static engine.utils.DrawingUtils.disableTexture;
import static engine.utils.DrawingUtils.disableTransparency;
import static engine.utils.DrawingUtils.enableTexture;
import static engine.utils.DrawingUtils.enableTransparency;
import static engine.application.ai.GhostAIMode.*;
import static engine.utils.Coordinate2i.ORIGIN;
import engine.utils.Pair;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class PacmanGhost extends PacmanActor {

    public static final int BLINKY = 0;
    public static final int PINKY = 1;
    public static final int INKY = 2;
    public static final int CLYDE = 3;

    private static final List<Coordinate2i> allDirections = new LinkedList<>();

    private final float frightenedSpeedmult = 0.5f;
    protected boolean confined;
    protected int ghostColor;
    protected GhostAIMode mode;
    protected Coordinate2i currentFieldTarget;
    protected Coordinate2i myLastFieldPosition;
    protected Coordinate2i homeFieldPosition;
    protected Coordinate2i myNextTurn;

    static {
        allDirections.add(UP);
        allDirections.add(LEFT);
        allDirections.add(DOWN);
        allDirections.add(RIGHT);
    }

    public PacmanGhost(PacmanApplication app, int ghostColor, Coordinate2d initialPosition, Coordinate2i homeFieldPosition, boolean confined) {
        super(app, "ghost_sprite_60.png", 60, initialPosition);
        this.homeFieldPosition = homeFieldPosition;
        this.confined = confined;
        this.ghostColor = ghostColor;
        defaultSpeedMult = 0.75;
        speedMult = 0.75;
        spriteCycle = 0;
        spriteFrames = 4;
        spriteTimerMult = 50;
        spriteCycleIncrement = 1;
        sizeRatio = 16d / (double) GRID_RESOLUTION;
        eatenPellets = 0;
        currentFieldTarget = (Coordinate2i) app.getAttribute("PlayerPosition");
        boomerangMode = false;
        myNextTurn = new Coordinate2i(ORIGIN);
        setZOrder(1100);
        if (ghostColor == BLINKY) {
            mode = RETURN;
        } else {
            mode = GET_OUT;
        }
    }

    public int getGhostColor() {
        return ghostColor;
    }

    public Coordinate2i getCurrentFieldTarget() {
        return currentFieldTarget;
    }

    protected abstract void updatePursuitTargetCell();

    private void pursueTarget(Level level, Coordinate2i gridPosition,
            Pair<CellContent, Coordinate2i>[][] field) {
        Coordinate2i upperBound = level.getFieldUpperBoundary();
        Coordinate2i nextCell = new Coordinate2i(gridPosition);
        nextCell.sum(facingDirection);
        nextCell.normalizeCoordinates(ORIGIN, upperBound);
        double minimalDistance = Double.MAX_VALUE;
        for (Coordinate2i dir : allDirections) {
            if (!isFacingDirectionOppositeTo(dir)) {
                Coordinate2i auxCell = new Coordinate2i(nextCell);
                auxCell.sum(dir);
                auxCell.normalizeCoordinates(ORIGIN, upperBound);
                if (field[auxCell.x][auxCell.y].getLeft() != BLOCK) {
                    double distance = auxCell.getSquaredEuclideanDistance(currentFieldTarget);
                    if (distance < minimalDistance) {
                        minimalDistance = distance;
                        if (dir.equals(facingDirection)) {
                            myNextTurn.copyCoordinates(ORIGIN);
                        } else {
                            myNextTurn.copyCoordinates(dir);
                        }
                    }
                }
            }
        }
        if (minimalDistance == Double.MAX_VALUE) {
            myNextTurn = facingDirection.getMultipliedByScalar(-1);
        }
    }
    
    protected void flee(Level level, Coordinate2i gridPosition,
            Pair<CellContent, Coordinate2i>[][] field) {
        Coordinate2i upperBound = level.getFieldUpperBoundary();
        Coordinate2i nextCell = new Coordinate2i(gridPosition);
        nextCell.sum(facingDirection);
        nextCell.normalizeCoordinates(ORIGIN, upperBound);
        List<Coordinate2i> randomDirections = new LinkedList<>();
        for (Coordinate2i dir : allDirections) {
            if (!isFacingDirectionOppositeTo(dir)) {
                Coordinate2i auxCell = new Coordinate2i(nextCell);
                auxCell.sum(dir);
                auxCell.normalizeCoordinates(ORIGIN, upperBound);
                if (field[auxCell.x][auxCell.y].getLeft() != BLOCK) {
                    randomDirections.add(dir);
                }
            }
        }
        if (randomDirections.size() > 0) {
            Random rnd = new Random();
            Coordinate2i selectedDir = randomDirections.get(rnd.nextInt(randomDirections.size()));
            if (!selectedDir.equals(facingDirection)) {
                myNextTurn.copyCoordinates(selectedDir);
            }
        }
    }
    
    private void reverseWalkingDirection() {
        changeWalkDirection(facingDirection.getMultipliedByScalar(-1), level);
    }

    public void changeMode(GhostAIMode newMode) {
        if (mode != FLEE) {
            reverseWalkingDirection();
        }
        if (newMode == FLEE) {
            speedMult = frightenedSpeedmult;
        } else {
            speedMult = defaultSpeedMult;
        }
        mode = newMode;
    }

    private void turn(Level level) {
        this.level = level;
        changeWalkDirection(myNextTurn, level);
        myNextTurn.copyCoordinates(ORIGIN);
    }

    @Override
    protected void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2i gridPosition) {
        if (!myNextTurn.equals(ORIGIN)) {
            turn(level);
        } else if (mode != FLEE) {
            pursueTarget(level, gridPosition, field);
        } else {
            flee(level, gridPosition, field);
        }
    }

    @Override
    protected void onMovementBlock() {
        if (mode == IDLE) {
            facingDirection.copyCoordinates(facingDirection.getMultipliedByScalar(-1));
        }
    }

    @Override
    public void draw(CameraControl camera) {
        int color = (mode != FLEE) ? (ghostColor) : (0);
        int frightShift = (mode != FLEE) ? (0) : (4);
        updateSpriteCycle();
        enableTexture();
        enableTransparency();
        spriteSheet.getSubImage((spriteFrames * color) + spriteCycle, frightShift + getFacingDirectionShift())
                .draw(absolutePosition2i.x - tileWidth / 2,
                absolutePosition2i.y - tileHeight / 2, tileWidth, tileHeight);
        disableTransparency();
        disableTexture();
    }

    @Override
    public void update(Level level) {
        super.update(level);
        updatePursuitTargetCell();
    }

    @Override
    protected int getFacingDirectionShift() {
        if (facingDirection.equals(RIGHT)) {
            return 0;
        } else if (facingDirection.equals(LEFT)) {
            return 1;
        } else if (facingDirection.equals(UP)) {
            return 2;
        }
        return 3;
    }

}
