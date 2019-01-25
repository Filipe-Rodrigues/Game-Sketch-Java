package engine.core;

import engine.application.CellContent;
import engine.graphics.LWJGLDrawable;
import static engine.utils.Constants.SPRITES_DIR;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import engine.utils.Pair;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public abstract class Level extends LWJGLDrawable {

    protected SpriteSheet tileset;
    protected double tileDownscaleFactor;
    protected Coordinate2i fieldSize;

    public Level(Coordinate2i fieldSize, String tilesetFileName, int tw, int th) {
        super(1);
        this.fieldSize = fieldSize;
        initSpriteSheet(tilesetFileName, tw, th);
    }
    
    private void initSpriteSheet(String spriteFileName, int tw, int th) {
        try {
            tileset = new SpriteSheet(SPRITES_DIR + spriteFileName, tw, th);
        } catch (SlickException ex) {
            System.err.println("Exception trying to create new tileset: " + spriteFileName);
        }
    }
    
    public abstract void update();
    
    public abstract Coordinate2d getGridPosition(Coordinate2d absolutePosition);
    
    public abstract void setGridPosition(Coordinate2i position, Pair<CellContent, Coordinate2i> cellType);

}
