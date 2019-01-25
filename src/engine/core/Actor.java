package engine.core;

import engine.graphics.LWJGLDrawable;
import static engine.utils.Constants.SPRITES_DIR;
import engine.utils.Coordinate2d;
import engine.utils.Coordinate2i;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public abstract class Actor extends LWJGLDrawable {

    protected SpriteSheet spriteSheet;
    protected Coordinate2d position;
    protected boolean movementBlocked;

    public Actor(String spriteFileName, int tw, int th) {
        super();
        initSpriteSheet(spriteFileName, tw, th);
        position = new Coordinate2d(0, 0);
    }
    
    public Actor(String spriteFileName, int tw, int th, int zOrder) {
        super(zOrder);
        initSpriteSheet(spriteFileName, tw, th);
        position = new Coordinate2d(0, 0);
    }

    private void initSpriteSheet(String spriteFileName, int tw, int th) {
        try {
            spriteSheet = new SpriteSheet(SPRITES_DIR + spriteFileName, tw, th);
        } catch (SlickException ex) {
            System.err.println("Exception trying to create new sprite sheet: " + spriteFileName);
        }
    }
    
    public void toggleMovementBlock() {
        movementBlocked = !movementBlocked;
    }
    
    protected abstract void update(Level level);
    
}
