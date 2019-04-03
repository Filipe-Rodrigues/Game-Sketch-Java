package engine.application;

import static engine.application.CellContent.BLOCK;
import static engine.application.CellContent.EMPTY;
import static engine.application.CellContent.PELLET;
import static engine.application.CellContent.POWER_PELLET;
import static engine.application.GameCommand.FRIGHTEN_ALL_GHOSTS;
import static engine.application.GameCommand.COUNT_SCORE;
import static engine.application.ai.GhostAIMode.FLEE;
import engine.core.Command;
import engine.core.Level;
import engine.graphics.CameraControl;
import engine.utils.Coordinate2d;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2i;
import static engine.utils.Coordinate2i.ORIGIN;
import static engine.utils.DrawingUtils.disableTexture;
import static engine.utils.DrawingUtils.disableTransparency;
import static engine.utils.DrawingUtils.enableTexture;
import static engine.utils.DrawingUtils.enableTransparency;
import engine.utils.Pair;

public class PacmanPlayer extends PacmanActor {

    
    
    public PacmanPlayer(PacmanApplication app) {
        super(app, "pacman_sprite_56.png", 56, new Coordinate2d(32 * 14, 32 * 26.5));
        defaultSpeedMult = 0.8;
        speedMult = defaultSpeedMult;
        spriteCycle = 0;
        spriteFrames = 5;
        spriteTimerMult = 35;
        spriteCycleIncrement = 2;
        sizeRatio = 14d / (double) GRID_RESOLUTION;
        eatenPellets = 0;
        onCellEnterAnalyze = true;
        loadSound("wak.wav", "0");
        loadSound("kaw.wav", "1");
    }

    @Override
    public void draw(CameraControl camera) {
        updateSpriteCycle();
        enableTexture();
        enableTransparency();

        if (spriteCycle == 0) {
            spriteSheet.getSubImage(0, 0).draw(absolutePosition2i.x - tileWidth / 2,
                    absolutePosition2i.y - tileHeight / 2, tileWidth, tileHeight);
        } else {
            spriteSheet.getSubImage(spriteCycle, getFacingDirectionShift()).draw(absolutePosition2i.x - tileWidth / 2,
                    absolutePosition2i.y - tileHeight / 2, tileWidth, tileHeight);
        }
        disableTransparency();
        disableTexture();
    }

    @Override
    protected void onMovementBlock() {
        movementBlocked = true;
    }

    @Override
    protected void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2i pos) {
        pos.normalizeCoordinates(ORIGIN, level.getFieldUpperBoundary());
        if (field[pos.x][pos.y].getLeft() != EMPTY && field[pos.x][pos.y].getLeft() != BLOCK) {
            app.sendCommand(new Command<>(COUNT_SCORE, field[pos.x][pos.y]));
            if (field[pos.x][pos.y].getLeft() == PELLET || field[pos.x][pos.y].getLeft() == POWER_PELLET) {
                soundEffects.get((eatenPellets % 2) + "").play();
                eatenPellets++;
                if (field[pos.x][pos.y].getLeft() == POWER_PELLET) {
                    app.sendCommand(new Command<>(FRIGHTEN_ALL_GHOSTS, FLEE));
                }
                level.setGridPosition(pos, new Pair<>(EMPTY, null));
            }
        }
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
