package engine.graphics;

import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import engine.core.SharedComponents;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2d;
import java.util.LinkedList;
import org.lwjgl.opengl.GL30;

public class MainDisplay {

    public static boolean DRAW_COLLISION_BOUNDARIES = true;

    private final SharedComponents components;
    private final LWJGLApplication app;
    private final List<GUIControl> gc;
    private final DisplayConfiguration dc;
    private final CameraControl camera;

    public MainDisplay(ApplicationSetup setup) {
        this.app = setup.application;
        this.gc = new LinkedList<>();
        this.dc = setup.displayConfiguration;
        this.components = app.getSharedComponents();
        this.camera = setup.camera;
    }

    public void addGUIControl(GUIControl control) {
        gc.add(control);
        control.registerDisplayWindow(this);
    }
    
    public void start() {
        setupDisplay();
        prepareOpenGL();
        loopGL();
        terminate();
    }

    private void setupDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            adjustViewport();
            Display.setResizable(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void prepareOpenGL() {
        initGL(); // init OpenGL
        app.insertDrawableElements();
        getDelta(); // call once before loop to initialise lastFrame
        dc.lastFPS = getTime(); // call before loop to initialise dc.fps timer
    }

    private void loopGL() {
        while (!Display.isCloseRequested()) {
            pollInput();
            renderGL();

            Display.update();
            updateWindowState();
            updateFPS();
            Display.sync(60); // cap dc.fps to 60dc.fps
        }
    }

    private void terminate() {
        Display.destroy();
        components.stopRunning();
    }

    private void pollInput() {
        for (GUIControl control : gc) {
            control.pollInput();
        }
    }
    
    private void updateWindowState() {
        if (Display.wasResized()) {
            adjustViewport();
        }
    }

    public void adjustViewport() {
        dc.windowWid = Display.getWidth();
        dc.windowHei = Display.getHeight();
        if (dc.windowWid > dc.windowHei * FIELD_WIDTH / FIELD_HEIGHT) {
            dc.viewportDispWid = (dc.windowWid - dc.windowHei * FIELD_WIDTH / FIELD_HEIGHT) / 2;
            dc.viewportWid = dc.windowHei * FIELD_WIDTH / FIELD_HEIGHT;
            dc.viewportDispHei = 0;
            dc.viewportHei = dc.windowHei;
        } else {
            dc.viewportDispWid = 0;
            dc.viewportWid = dc.windowWid;
            dc.viewportDispHei = (dc.windowHei - dc.windowWid * FIELD_HEIGHT / FIELD_WIDTH) / 2;
            dc.viewportHei = dc.windowWid * FIELD_HEIGHT / FIELD_WIDTH;
        }
        glScissor(dc.viewportDispWid, dc.viewportDispHei, dc.viewportWid, dc.viewportHei);
        glViewport(dc.viewportDispWid, dc.viewportDispHei, dc.viewportWid, dc.viewportHei);
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
        int delta = (int) (time - dc.lastFrame);
        dc.lastFrame = time;

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
        if (getTime() - dc.lastFPS > 1000) {
            Display.setTitle("FPS: " + dc.fps);
            dc.fps = 0;
            dc.lastFPS += 1000;
        }
        dc.fps++;
    }

    public void initGL() {
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, FIELD_WIDTH, 0, FIELD_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    public void renderGL() {
        clearScreen();
        updateCamera();
        drawElements();
    }

    private void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void updateCamera() {
        glLoadIdentity();
        camera.lookThrough();
    }

    private void drawElements() {
        List<LWJGLDrawable> drawingElements = components.getComponentList();
        for (LWJGLDrawable drawingElement : drawingElements) {
            drawingElement.draw(camera);
        }
    }
}
