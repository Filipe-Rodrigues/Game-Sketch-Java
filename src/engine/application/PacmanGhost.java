package engine.application;

import engine.core.Level;
import engine.graphics.CameraControl;
import static engine.utils.Constants.GRID_RESOLUTION;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import static engine.utils.DrawingUtils.disableTexture;
import static engine.utils.DrawingUtils.disableTransparency;
import static engine.utils.DrawingUtils.enableTexture;
import static engine.utils.DrawingUtils.enableTransparency;
import static engine.application.GhostAIMode.*;
import engine.utils.Pair;
import static java.lang.Math.abs;

public final class PacmanGhost extends PacmanActor {

    public static final int BLINKY = 0;
    public static final int PINKY = 1;
    public static final int INKY = 2;
    public static final int CLYDE = 3;

    private boolean confined;
    private int ghostColor;
    private GhostAIMode mode;
    private Coordinate2i currentFieldTarget;
    private Coordinate2i myLastFieldPosition;
    private Coordinate2i homeFieldPosition;

    public PacmanGhost(PacmanApplication app, int ghostColor, Coordinate2d initialPosition, Coordinate2i homeFieldPosition, boolean confined) {
        super(app, "ghost_sprite_60.png", 60, initialPosition);
        this.homeFieldPosition = homeFieldPosition;
        this.confined = confined;
        this.ghostColor = ghostColor;
        speedMult = 220;
        spriteCycle = 0;
        spriteFrames = 4;
        spriteTimerMult = 50;
        spriteCycleIncrement = 1;
        sizeRatio = 16d / (double) GRID_RESOLUTION;
        eatenPellets = 0;
        minMoveThreshold = 0.49;
        maxMoveThreshold = 0.51;
        currentFieldTarget = (Coordinate2i) app.getAttribute("PlayerPosition");
        boomerangMode = false;
        setZOrder(1100);
        if (ghostColor == BLINKY) {
            mode = RETURN;
        } else {
            mode = GET_OUT;
        }
    }

    private void getPursuitTargetCell() {
        Coordinate2i target = (Coordinate2i) app.getAttribute("PlayerPosition");
        switch (ghostColor) {
            case BLINKY:
                currentFieldTarget.copyCoordinates(target);
                break;
            case PINKY:
                int direction = (int) app.getAttribute("PlayerDirection");
                switch (direction) {
                    case FACING_UP:
                        target.y -= 2;
                        if (target.y < 0) {
                            target.y = 0;
                        }
                        break;
                    case FACING_DOWN:
                        target.y += 2;
                        int yMax = ((Coordinate2i) app.getAttribute("FieldGridDimension")).y;
                        if (target.y >= yMax) {
                            target.y = yMax - 1;
                        }
                        break;
                    case FACING_LEFT:
                        target.x -= 2;
                        if (target.x < 0) {
                            target.x = 0;
                        }
                        break;
                    case FACING_RIGHT:
                        target.x += 2;
                        int xMax = ((Coordinate2i) app.getAttribute("FieldGridDimension")).x;
                        if (target.x >= xMax) {
                            target.x = xMax - 1;
                        }
                        break;
                }
                currentFieldTarget.copyCoordinates(target);
                break;
            case INKY:
                Coordinate2i blinkyPos = (Coordinate2i) app.getAttribute("BlinkyPosition");
                int xMid = abs(blinkyPos.x - target.x) / 2;
                int yMud = abs(blinkyPos.y - target.y) / 2;
                break;
            case CLYDE:
                
                break;
            
        }
    }
    
    private void flee() {
        
    }
    
    
    
    @Override
    protected void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2d gridPosition) {
        
    }

    @Override
    protected void onMovementBlock() {
        
    }

    @Override
    public void draw(CameraControl camera) {
        updateSpriteCycle();
        enableTexture();
        enableTransparency();
        spriteSheet.getSubImage((spriteFrames * ghostColor) + spriteCycle, facingDirection).draw((int) position.x - tileWidth / 2,
                (int) position.y - tileHeight / 2, tileWidth, tileHeight);
        disableTransparency();
        disableTexture();
    }

}
