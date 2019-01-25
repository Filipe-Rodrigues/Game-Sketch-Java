package engine.graphics;

public class ApplicationSetup {

    public final LWJGLApplication application;
    public final DisplayConfiguration displayConfiguration;
    public final CameraControl camera;

    public ApplicationSetup(LWJGLApplication app, DisplayConfiguration dc, CameraControl camera) {
        this.application = app;
        this.displayConfiguration = dc;
        this.camera = camera;
    }

}
