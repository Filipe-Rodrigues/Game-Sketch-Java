package engine.core;

import java.util.Random;
import engine.utils.Coordinate2D;
import org.lwjgl.util.Color;
import static org.lwjgl.opengl.GL11.*;
import static engine.utils.ApplicationConstants.*;
import static engine.utils.Coordinate2D.*;

public class Particle implements LWJGLDrawable {
    private float particleSize;
    private Coordinate2D position;
    private Coordinate2D velocity;
    private Coordinate2D cumulativeForce;
    private double mass;
    private Color color;
    private boolean mobile;
    private boolean solid;
    private char particleClass;

    public Particle(char particleClass, float particleSize, 
            Coordinate2D position, Coordinate2D velocity, double mass, 
            Color color, boolean mobile, boolean solid) {
        this.particleClass = particleClass;
        this.particleSize = particleSize;
        this.position = new Coordinate2D(position);
        this.velocity = new Coordinate2D(velocity);
        this.mass = mass;
        this.color = color;
        this.mobile = mobile;
        this.solid = solid;
        cumulativeForce = new Coordinate2D(ORIGIN);
    }
    
    public Particle(char particleClass, float particleSize, Coordinate2D position, 
            double mass, Color color, boolean mobile, boolean solid) {
        this.particleClass = particleClass;
        this.particleSize = particleSize;
        this.position = new Coordinate2D(position);
        this.velocity = new Coordinate2D(0, 0);
        this.mass = mass;
        this.color = color;
        this.mobile = mobile;
        this.solid = solid;
        cumulativeForce = new Coordinate2D(ORIGIN);
    }

    public Particle(char particleClass, float particleSize, double mass, 
            boolean mobile, boolean solid) {
        Random rnd = new Random();
        this.particleClass = particleClass;
        this.particleSize = particleSize;
        this.position = new Coordinate2D(rnd.nextDouble() * FIELD_WIDTH, 
                                         rnd.nextDouble() * FIELD_HEIGHT);
        this.velocity = new Coordinate2D(rnd.nextDouble() * 10, 0);
        this.mass = mass;
        this.color = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        this.mobile = mobile;
        this.solid = solid;
        cumulativeForce = new Coordinate2D(ORIGIN);
    }

    @Override
    public void draw() {
        glPointSize(particleSize);
        glColor3ub(color.getRedByte(), color.getGreenByte(), color.getBlueByte());
        glBegin(GL_POINTS);
        glVertex2d(position.x, position.y);
        glEnd();
    }
    
    private Coordinate2D getGravitationalForce(Particle other) {
        Coordinate2D direction = getVector(other.position, position);
        double distance = direction.getMagnitude();
        double force = (G * mass * other.mass) / (distance * distance);
        if (force > FORCE_THRESHOLD) force = FORCE_THRESHOLD;
        return direction.getUnitVector().getScaled(force);
    } 
    
    private void resetCumulativeForce() {
        cumulativeForce.copy(ORIGIN);
    }
    
    public void interact(Particle other) {
        if (this.particleClass != other.particleClass) {
            Coordinate2D force = getGravitationalForce(other);
            if (mobile) {
                cumulativeForce.sum(force);
            }
            if (other.mobile) {
                other.cumulativeForce.sum(force);
            }
        }
    }
    
    public void applyVelocity(Coordinate2D additional) {
        velocity.copy(additional);
    }
    
    public void updatePosition() {
        Coordinate2D acceleration = cumulativeForce.getScaled(1 / mass);
        position.sum(velocity.getScaled(DELTA_IN_SECONDS / FIELD_LENGHT_UNIT_MULTIPLIER));
        position.sum(acceleration.getScaled(DELTA_IN_SECONDS / (2d * FIELD_LENGHT_UNIT_MULTIPLIER)));
        velocity.sum(acceleration);
        resetCumulativeForce();
    }
}
