package engine.core;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.Color;
import engine.gui.MainDisplay;
import engine.utils.Coordinate2D;
import static engine.utils.ApplicationConstants.*;
import static engine.utils.ThreadUtils.*;

public class ParticlesCore {
    private SharedComponents sharedComponents;
    private final Particle blackHole;
    private final List<Particle> particles;
    private boolean initialized;

    public ParticlesCore(int particleNum) {
        initialized = false;
        blackHole = new Particle('1', 8, new Coordinate2D(FIELD_WIDTH / 2d, FIELD_HEIGHT / 2d),
                1E+40, new Color(255, 255, 255), false, false);
        particles = new ArrayList<>();
        particles.add(blackHole);
        for (int i = 0; i < particleNum; i++) {
            particles.add(new Particle('2', 1, 10, true, false));
        }
        sharedComponents = new SharedComponents(new ArrayList<>(particles));
        startDisplayThread();
        //startPhysicsThread();
    }

    public boolean wasInitialized() {
        return initialized;
    }
    
    private void startDisplayThread() {
        MainDisplay display = new MainDisplay(this);
        Thread thread = new Thread(display::start);
        thread.start();
    }
    
    public void startPhysicsThread() {
        Thread thread = new Thread(this::computePhysics);
        thread.start();
        initialized = true;
    }
    
    public SharedComponents getSharedComponents() {
        return sharedComponents;
    }
    
    public void impulse() {
        Coordinate2D velocity = new Coordinate2D(1000, 0);
        for (Particle particle : particles) {
            particle.applyVelocity(velocity);
        }
    }
    
    private void computePhysics() {
        double ti;
        double tf;
        double drift;
        while (sharedComponents.isStillRunning()) {
            ti = System.nanoTime();
            for (int i = 1; i < particles.size(); i++) {
                blackHole.interact(particles.get(i));
            }
            tf = System.nanoTime();
            for (int i = 1; i < particles.size(); i++) {
                particles.get(i).updatePosition();
            }
            drift = DELTA - (tf - ti);
            if (drift > 0) {
                holdOn(drift);
            }
            //System.err.println("nanos: " + (tf - ti));
        }
    }
}
