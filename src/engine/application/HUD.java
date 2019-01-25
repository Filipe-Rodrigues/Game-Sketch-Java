package engine.application;

import engine.core.Command;
import engine.graphics.ApplicationSetup;
import engine.graphics.CameraControl;
import engine.graphics.GUIControl;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static engine.utils.Constants.*;
import static engine.utils.DrawingUtils.*;
import static engine.application.GameCommand.*;
import static engine.application.GameState.*;
import engine.utils.Coordinate2i;
import org.newdawn.slick.Color;

public class HUD extends GUIControl {

    private PacmanDebug debugger;

    public HUD(ApplicationSetup setup) {
        super(setup);
    }

    public void registerDebugger(PacmanDebug debugger) {
        this.debugger = debugger;
    }

    private void updateMouseHover() {
        if ((GameState) app.getAttribute("GameState") == DEBUG_MODE) {
            debugger.selectedGridPosEnd.update(mouseNormalizedPosition, GRID_RESOLUTION, GRID_RESOLUTION);
            int mdw = Mouse.getDWheel();
            if (mdw != 0) {
                debugger.updateSelectedTile(mdw / 120);
            }
        }
    }

    private void evaluateMouseButtonPressed() {
        if (Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1) {
            debugger.selectedGridPosStart.update(mouseNormalizedPosition, GRID_RESOLUTION, GRID_RESOLUTION);
        }
    }

    private void evaluateMouseButtonReleased() {
        if (Mouse.getEventButton() == 0) {
            if ((GameState) app.getAttribute("GameState") == DEBUG_MODE) {
                app.sendCommand(new Command<>(DEBUG_INSERT_TILE, null));
            }
        } else if (Mouse.getEventButton() == 1) {
            if ((GameState) app.getAttribute("GameState") == DEBUG_MODE) {
                app.sendCommand(new Command<>(DEBUG_DELETE_TILE, null));
            }
        }
    }

    private void switchFullscreenState() {
        if (Display.isFullscreen()) {
            display.setDisplayMode(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, false);
            display.adjustViewport();
        } else {
            display.setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
                    Display.getDesktopDisplayMode().getHeight(), true);
            display.adjustViewport();
        }
    }

    private void evaluateKeyboardKeyHolding() {

    }

    private void evaluateKeyboardFiredKeys() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_F:
                        switchFullscreenState();
                        break;
                    case Keyboard.KEY_V:
                        dc.vsync = !dc.vsync;
                        Display.setVSyncEnabled(dc.vsync);
                        break;
                    case Keyboard.KEY_G:
                        app.sendCommand(new Command<>(TOGGLE_GRID, null));
                        break;
                    case Keyboard.KEY_S:
                        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                            app.sendCommand(new Command<>(DEBUG_WRITE_FIELD_FILE, null));
                        }
                        break;
                    case Keyboard.KEY_APOSTROPHE:
                    case Keyboard.KEY_TAB:
                        app.sendCommand(new Command<>(CHANGE_GAME_STATE, DEBUG_MODE));
                    case Keyboard.KEY_UP:
                        app.sendCommand(new Command<>(WALK, PacmanPlayer.FACING_UP));
                        break;
                    case Keyboard.KEY_DOWN:
                        app.sendCommand(new Command<>(WALK, PacmanPlayer.FACING_DOWN));
                        break;
                    case Keyboard.KEY_RIGHT:
                        app.sendCommand(new Command<>(WALK, PacmanPlayer.FACING_RIGHT));
                        break;
                    case Keyboard.KEY_LEFT:
                        app.sendCommand(new Command<>(WALK, PacmanPlayer.FACING_LEFT));
                        break;
                    case Keyboard.KEY_RETURN:
                        app.sendCommand(new Command<>(TOGGLE_FREEZE_STATE, null));
                    default:
                        break;
                }
            } else {
                switch (Keyboard.getEventKey()) {

                    default:
                        break;
                }
            }
        }
    }

    private void pollKeyboard() {
        evaluateKeyboardKeyHolding();
        evaluateKeyboardFiredKeys();
    }

    private void pollMouse() {
        normalizeMousePosition();
        updateMouseHover();
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                evaluateMouseButtonPressed();
            } else {
                evaluateMouseButtonReleased();
            }
        }
    }

    @Override
    public void pollInput() {
        pollKeyboard();
        pollMouse();
    }

    @Override
    public void draw(CameraControl camera) {
        if ((GameState) app.getAttribute("GameState") == DEBUG_MODE) {
            enableTransparency();
            glLineWidth(2f);
            if (debugger.selectedGridPosStart.x > -1) {
                drawCellOutline(debugger.selectedGridPosStart, Color.red);
            }
            drawCellOutline(debugger.selectedGridPosEnd, new Color(1f, 0.5f, 0f, 1f));
        }
    }

    private void drawCellOutline(Coordinate2i gridPos, Color color) {
        glColor4f(color.r, color.g, color.b, color.a);
        glBegin(GL_LINE_LOOP);
        {
            glVertex2i(gridPos.x, gridPos.y);
            glVertex2i(gridPos.x + GRID_RESOLUTION, gridPos.y);
            glVertex2i(gridPos.x + GRID_RESOLUTION, gridPos.y + GRID_RESOLUTION);
            glVertex2i(gridPos.x, gridPos.y + GRID_RESOLUTION);
        }
        glEnd();
    }

}
