package engine.graphics;

import engine.utils.Line;
import java.util.List;
import engine.utils.Coordinate2d;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import static org.lwjgl.opengl.GL11.*;
import static engine.utils.Constants.*;
import static engine.utils.Coordinate2d.*;
import static engine.utils.DrawingUtils.*;
import static java.lang.Math.floor;

public class DisplayGrid extends LWJGLDrawable {

    private List<Line> grid;
    private float alpha;
    private float lineWidth;
    private boolean activated = false;

    public DisplayGrid(float alpha, float lineWidth) {
        super(Integer.MAX_VALUE);
        this.alpha = alpha;
        this.lineWidth = lineWidth;
        grid = new ArrayList<>();
        Coordinate2d hVector = new Coordinate2d(FIELD_WIDTH + 2 * OFFSCREEN_LIMIT, 0);
        Coordinate2d vVector = new Coordinate2d(0, FIELD_HEIGHT + 2 * OFFSCREEN_LIMIT);
        Coordinate2d origin = new Coordinate2d(-OFFSCREEN_LIMIT, -OFFSCREEN_LIMIT);
        for (int i = -OFFSCREEN_LIMIT; i <= FIELD_WIDTH + OFFSCREEN_LIMIT; i += GRID_RESOLUTION) {
            origin.x = i;
            grid.add(new Line(origin, vVector, Color.white));
        }
        origin.x = -OFFSCREEN_LIMIT;
        for (int i = -OFFSCREEN_LIMIT; i <= FIELD_HEIGHT + OFFSCREEN_LIMIT; i += GRID_RESOLUTION) {
            origin.y = i;
            grid.add(new Line(origin, hVector, Color.white));
        }
    }

    @Override
    public void draw(CameraControl camera) {
        if (activated) {
            double xOrigin = floor(camera.position.x / GRID_RESOLUTION) * GRID_RESOLUTION;
            double yOrigin = floor(camera.position.y / GRID_RESOLUTION) * GRID_RESOLUTION;
            Coordinate2d gridOrigin = new Coordinate2d(xOrigin, yOrigin);

            glLineWidth(lineWidth);
            enableTransparency();
            glBegin(GL_LINES);
            for (Line line : grid) {
                Coordinate2d newOrigin = sum(gridOrigin, line.origin);
                glColor4f(line.color.r, line.color.g, line.color.b, alpha);
                glVertex2d(newOrigin.x, newOrigin.y);
                glVertex2d(newOrigin.x + line.vector.x, newOrigin.y + line.vector.y);
            }
            glEnd();
            disableTransparency();
        }
    }

    public void toggleActivated() {
        activated = !activated;
    }
    
    public void setGridActivated(boolean activated) {
        this.activated = activated;
    }
    
}
