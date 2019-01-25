package engine.graphics;

import engine.core.SharedComponents;
import static engine.utils.Constants.FIELD_HEIGHT;
import static engine.utils.Constants.FIELD_WIDTH;
import engine.utils.Coordinate2d;
import static engine.utils.Coordinate2d.ORIGIN;
import org.lwjgl.input.Mouse;

public abstract class GUIControl extends LWJGLFigure {

    protected final LWJGLApplication app;
    protected final SharedComponents components;
    protected final Coordinate2d mouseNormalizedPosition;
    protected final DisplayConfiguration dc;
    protected final CameraControl camera;
    protected MainDisplay display;

    public GUIControl(ApplicationSetup setup) {
        super(Integer.MAX_VALUE, ORIGIN, ORIGIN, new Coordinate2d(FIELD_WIDTH, FIELD_HEIGHT));
        this.app = setup.application;
        this.dc = setup.displayConfiguration;
        this.camera = setup.camera;
        components = app.getSharedComponents();
        mouseNormalizedPosition = new Coordinate2d(0, 0);
    }

    protected void normalizeMousePosition() {
        mouseNormalizedPosition.x = (double) (Mouse.getX() - dc.viewportDispWid) / (double) dc.viewportWid * FIELD_WIDTH;
        mouseNormalizedPosition.y = (double) (Mouse.getY() - dc.viewportDispHei) / (double) dc.viewportHei * FIELD_HEIGHT;
    }

    public void registerDisplayWindow(MainDisplay display) {
        this.display = display;
    }
    
    public abstract void pollInput();
}
