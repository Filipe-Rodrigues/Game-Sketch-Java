package engine.core;

import engine.application.CellContent;
import engine.graphics.LWJGLDrawable;
import static engine.utils.Constants.SFX_DIR;
import static engine.utils.Constants.SPRITES_DIR;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import engine.utils.Pair;
import java.util.HashMap;
import java.util.Map;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

public abstract class Level extends LWJGLDrawable {

    protected SpriteSheet tileset;
    protected double tileDownscaleFactor;
    protected Coordinate2i fieldSize;
    protected Map<String, Sound> loopingAmbiance;

    public Level(Coordinate2i fieldSize, String tilesetFileName, int tw, int th) {
        super(1);
        this.fieldSize = fieldSize;
        initSpriteSheet(tilesetFileName, tw, th);
        loopingAmbiance = new HashMap<>();
    }

    private void initSpriteSheet(String spriteFileName, int tw, int th) {
        try {
            tileset = new SpriteSheet(SPRITES_DIR + spriteFileName, tw, th);
        } catch (SlickException ex) {
            System.err.println("Exception trying to create new tileset: " + spriteFileName);
        }
    }

    public Coordinate2i getFieldSize() {
        return new Coordinate2i(fieldSize);
    }
    
    public Coordinate2i getFieldUpperBoundary() {
        return new Coordinate2i(fieldSize.x - 1, fieldSize.y - 1);
    }

    protected final void loadSound(String soundName, String key) {
        try {
            Sound sound = new Sound(SFX_DIR + soundName);
            loopingAmbiance.put(key, sound);
        } catch (SlickException ex) {
        }
    }
    
    public abstract void update();

    public abstract Coordinate2d getGridPosition2d(Coordinate2d absolutePosition);

    public abstract Coordinate2i getGridPosition2i(Coordinate2d absolutePosition);

    public abstract void setGridPosition(Coordinate2i position, Pair<CellContent, Coordinate2i> cellType);

}
