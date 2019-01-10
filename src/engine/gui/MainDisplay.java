package engine.gui;

import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import engine.core.ParticlesCore;
import engine.core.SharedComponents;
import engine.core.LWJGLDrawable;
import static engine.utils.ApplicationConstants.*;
import engine.utils.Coordinate2D;
import static engine.utils.Coordinate2D.*;

public class MainDisplay {

    public static final int HUMAN_X_HUMAN_MODE = 0;
    public static final int AI_DEMO_MODE = 1;

    public static boolean DRAW_COLLISION_BOUNDARIES = true;

    private final SharedComponents components;
    private final Coordinate2D mouseNormalizedPosition;
    private final ParticlesCore core;

    private int windowWid = 800;
    private int windowHei = 600;
    private int viewportWid;
    private int viewportDispWid;
    private int viewportHei;
    private int viewportDispHei;
    private long lastFrame;
    private int fps;
    private long lastFPS;
    private boolean vsync;

    public MainDisplay(ParticlesCore core) {
        this.core = core;
        this.components = core.getSharedComponents();
        mouseNormalizedPosition = new Coordinate2D(0, 0);
    }

    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            adjustViewport();
            Display.setResizable(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL(); // init OpenGL
        getDelta(); // call once before loop to initialise lastFrame
        lastFPS = getTime(); // call before loop to initialise fps timer

        while (!Display.isCloseRequested()) {
            pollKeyboard();
            pollMouse();
            renderGL();

            Display.update();
            updateWindowState();
            Display.sync(60); // cap fps to 60fps
        }

        Display.destroy();
        components.stopRunning();
    }

    private void updateWindowState() {
        if (Display.wasResized()) {
            adjustViewport();
        }
    }

    private void adjustViewport() {
        windowWid = Display.getWidth();
        windowHei = Display.getHeight();
        if (windowWid > windowHei * FIELD_WIDTH / FIELD_HEIGHT) {
            viewportDispWid = (windowWid - windowHei * FIELD_WIDTH / FIELD_HEIGHT) / 2;
            viewportWid = windowHei * FIELD_WIDTH / FIELD_HEIGHT;
            viewportDispHei = 0;
            viewportHei = windowHei;
        } else {
            viewportDispWid = 0;
            viewportWid = windowWid;
            viewportDispHei = (windowHei - windowWid * FIELD_HEIGHT / FIELD_WIDTH) / 2;
            viewportHei = windowWid * FIELD_HEIGHT / FIELD_WIDTH;
        }
        glScissor(viewportDispWid, viewportDispHei, viewportWid, viewportHei);
        glViewport(viewportDispWid, viewportDispHei, viewportWid, viewportHei);
    }

    private void pollMouse() {
        mouseNormalizedPosition.x = (double) (Mouse.getX() - viewportDispWid) / (double) viewportWid * FIELD_WIDTH;
        mouseNormalizedPosition.y = (double) (Mouse.getY() - viewportDispHei) / (double) viewportHei * FIELD_HEIGHT;
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                evaluateMouseButtonPressed();
            } else {
                evaluateMouseButtonReleased();
            }
        }
    }

    private void evaluateMouseButtonPressed() {
        if (Mouse.getEventButton() == 0) {

        }
    }

    private void evaluateMouseButtonReleased() {
        if (Mouse.getEventButton() == 0) {

        } else if (Mouse.getEventButton() == 1) {

        }
    }

    public void pollKeyboard() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {

        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {

        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {

        }

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_F:
                        if (Display.isFullscreen()) {
                            setDisplayMode(FIELD_WIDTH, FIELD_HEIGHT, false);
                            adjustViewport();
                        } else {
                            setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
                                    Display.getDesktopDisplayMode().getHeight(), true);
                            adjustViewport();
                        }
                        break;
                    case Keyboard.KEY_V:
                        vsync = !vsync;
                        Display.setVSyncEnabled(vsync);
                        break;
                    case Keyboard.KEY_SPACE:
                        if (!core.wasInitialized()) {
                            core.startPhysicsThread();
                        } else {
                            core.impulse();
                        }
                        break;
                    case Keyboard.KEY_O:
                        DRAW_COLLISION_BOUNDARIES = !DRAW_COLLISION_BOUNDARIES;
                        break;
                    case Keyboard.KEY_LCONTROL:

                        break;
                    default:
                        break;
                }
            } else {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_LCONTROL:

                        break;
                    default:
                        break;
                }
            }
        }
        updateFPS(); // pollKeyboard FPS Counter
    }

    /**
     * Set the display mode to be used
     *
     * @param width The width of the display required
     * @param height The height of the display required
     * @param fullscreen True if we want fullscreen mode
     */
    public void setDisplayMode(int width, int height, boolean fullscreen) {

        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width)
                && (Display.getDisplayMode().getHeight() == height)
                && (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i = 0; i < modes.length; i++) {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        // if we've found a match for bpp and frequence against the 
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
                                && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }

    /**
     * Calculate how many milliseconds have passed since last frame.
     *
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public void initGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, FIELD_WIDTH, 0, FIELD_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    public void renderGL() {
        clearScreen();
        drawElements();
    }

    private void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void drawElements() {
        List<LWJGLDrawable> drawingElements = components.getComponentList();
        for (LWJGLDrawable drawingElement : drawingElements) {
            drawingElement.draw();
        }
    }
}
