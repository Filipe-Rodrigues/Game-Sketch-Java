package engine.utils;

import static org.lwjgl.opengl.GL11.*;

public class DrawingUtils {

    public static void enableTransparency() {
        glEnable(GL_BLEND);
    }

    public static void disableTransparency() {
        glDisable(GL_BLEND);
    }

    public static void enableTexture() {
        glEnable(GL_TEXTURE_2D);
    }

    public static void disableTexture() {
        glDisable(GL_TEXTURE_2D);
    }
    
}
