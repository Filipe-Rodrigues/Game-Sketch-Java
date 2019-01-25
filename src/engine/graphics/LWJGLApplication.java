package engine.graphics;

import engine.application.GameCommand;
import engine.core.Command;
import engine.core.SharedComponents;
import static engine.utils.Constants.*;
import static engine.utils.ThreadUtils.*;

public abstract class LWJGLApplication {
    
    protected MainDisplay display;
    protected SharedComponents sharedComponents;

    public void start() {
        Thread t1 = new Thread(display::start);
        Thread t2 = new Thread(() -> {
            double ti;
            double tf;
            double diff;
            while (sharedComponents.isStillRunning()) {
                ti = System.nanoTime();
                gameLoop();
                tf = System.nanoTime();
                diff = tf - ti;
                if (diff < DELTA) {
                    holdOn(DELTA - diff);
                }
            }
        });
        t1.start();
        t2.start();
    }
    
    public SharedComponents getSharedComponents() {
        return sharedComponents;
    }
    
    public abstract void insertDrawableElements();

    public abstract void sendCommand(Command<GameCommand, Object> command);

    public abstract Object getAttribute(String attributeName);

    public abstract void gameLoop();

}
