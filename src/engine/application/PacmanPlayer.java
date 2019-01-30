package engine.application;

import static engine.application.CellContent.BLOCK;
import static engine.application.CellContent.EMPTY;
import static engine.application.CellContent.PELLET;
import static engine.application.CellContent.POWER_PELLET;
import static engine.application.GameCommand.CHANGE_GHOST_MODE;
import static engine.application.GameCommand.COUNT_SCORE;
import engine.core.Command;
import engine.core.Level;
import engine.graphics.CameraControl;
import engine.utils.Coordinate2d;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2i;
import static engine.utils.DrawingUtils.disableTexture;
import static engine.utils.DrawingUtils.disableTransparency;
import static engine.utils.DrawingUtils.enableTexture;
import static engine.utils.DrawingUtils.enableTransparency;
import engine.utils.Pair;

public class PacmanPlayer extends PacmanActor {

    public PacmanPlayer(PacmanApplication app) {
        super(app, "pacman_sprite_56.png", 56, new Coordinate2d(32 * 14, 32 * 26.5));
        speedMult = 225;
        spriteCycle = 0;
        spriteFrames = 5;
        spriteTimerMult = 35;
        spriteCycleIncrement = 2;
        sizeRatio = 14d / (double) GRID_RESOLUTION;
        eatenPellets = 0;
        minMoveThreshold = 0.4;
        maxMoveThreshold = 0.6;
        loadSound("wak.wav", "0");
        loadSound("kaw.wav", "1");
    }
    
    @Override
    public void draw(CameraControl camera) {
        updateSpriteCycle();
        enableTexture();
        enableTransparency();

        if (spriteCycle == 0) {
            spriteSheet.getSubImage(0, 0).draw((int) position.x - tileWidth / 2,
                    (int) position.y - tileHeight / 2, tileWidth, tileHeight);
        } else {
            spriteSheet.getSubImage(spriteCycle, facingDirection).draw((int) position.x - tileWidth / 2,
                    (int) position.y - tileHeight / 2, tileWidth, tileHeight);
        }
        disableTransparency();
        disableTexture();
    }

    @Override
    protected void onMovementBlock() {
        movementBlocked = true;
    }
    
    @Override
    protected void checkCurrentCell(Level level, Pair<CellContent, Coordinate2i>[][] field, Coordinate2d gridPosition) {
        Coordinate2i pos = new Coordinate2i((int) gridPosition.x, (int) gridPosition.y);
        if (field[pos.x][pos.y].getLeft() != EMPTY && field[pos.x][pos.y].getLeft() != BLOCK) {
            app.sendCommand(new Command<>(COUNT_SCORE, field[pos.x][pos.y]));
            if (field[pos.x][pos.y].getLeft() == PELLET || field[pos.x][pos.y].getLeft() == POWER_PELLET) {
                soundEffects.get((eatenPellets % 2) + "").play();
                eatenPellets++;
                level.setGridPosition(pos, new Pair<>(EMPTY, null));
                if (field[pos.x][pos.y].getLeft() == POWER_PELLET) {
                    app.sendCommand(new Command<>(CHANGE_GHOST_MODE, "FLEE"));
                }
            }
        }
    }
}
