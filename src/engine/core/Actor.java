package engine.core;

import engine.graphics.LWJGLDrawable;
import static engine.utils.Constants.SPRITES_DIR;
import engine.utils.Coordinate2d;
import static engine.utils.Constants.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

public abstract class Actor extends LWJGLDrawable {

    protected SpriteSheet spriteSheet;
    protected Coordinate2d position;
    protected boolean movementBlocked;
    protected Map<String, Sound> soundEffects;

    public Actor(String spriteFileName, int tw, int th) {
        this(spriteFileName, tw, th, 1000);
    }
    
    public Actor(String spriteFileName, int tw, int th, int zOrder) {
        super(zOrder);
        initSpriteSheet(spriteFileName, tw, th);
        position = new Coordinate2d(0, 0);
        soundEffects = new HashMap<>();
    }

    private void initSpriteSheet(String spriteFileName, int tw, int th) {
        try {
            spriteSheet = new SpriteSheet(SPRITES_DIR + spriteFileName, tw, th);
        } catch (SlickException ex) {
            System.err.println("Exception trying to create new sprite sheet: " + spriteFileName);
        }
    }
    
    protected final void loadSound(String soundName, String key) {
        try {
            Sound sound = new Sound(SFX_DIR + soundName);
            soundEffects.put(key, sound);
        } catch (SlickException ex) {
        }
    }
    
    public void toggleMovementBlock() {
        movementBlocked = !movementBlocked;
    }
    
    protected abstract void update(Level level);
    
}
