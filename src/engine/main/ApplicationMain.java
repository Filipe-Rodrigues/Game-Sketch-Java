package engine.main;

import engine.application.PacmanApplication;
import engine.graphics.LWJGLApplication;

public class ApplicationMain {

    public static void main(String[] args) {
        LWJGLApplication app = new PacmanApplication();
        app.start();
    }
}
