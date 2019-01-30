package engine.application;

import static engine.application.CellContent.EMPTY;
import engine.core.Command;
import engine.core.SharedComponents;
import engine.graphics.ApplicationSetup;
import engine.graphics.CameraControl;
import engine.graphics.DisplayConfiguration;
import engine.graphics.DisplayGrid;
import engine.graphics.LWJGLApplication;
import engine.graphics.LWJGLDrawable;
import engine.graphics.MainDisplay;
import engine.utils.Coordinate2d;
import java.util.ArrayList;
import java.util.List;
import static engine.application.GameCommand.*;
import static engine.application.GameState.*;
import static engine.application.PacmanGhost.*;
import static engine.utils.Constants.*;
import engine.utils.Coordinate2i;
import engine.utils.Pair;
import java.util.PriorityQueue;
import java.util.Queue;
import static java.lang.Math.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PacmanApplication extends LWJGLApplication {

    private GameState gameState;
    private GameState previousGameState;
    private PacmanPlayer player;
    private Map<Integer, PacmanGhost> ghosts;
    private PacmanLevel level;
    private boolean frozenState = true;
    private Queue<Command<GameCommand, Object>> commandQueue;
    private List<Coordinate2i> collectedPellets;
    private PacmanDebug debugger;
    private boolean changeDirectionRequired = false;
    private int score;

    public PacmanApplication() {
        List<LWJGLDrawable> drawableElements = new ArrayList<>();
        debugger = new PacmanDebug(new Coordinate2i(16, 3), false);
        debugger.debugGrid = new DisplayGrid(0.1f, 2);
        drawableElements.add(debugger.debugGrid);
        sharedComponents = new SharedComponents(drawableElements);
        ApplicationSetup setup = new ApplicationSetup(this, new DisplayConfiguration(), new CameraControl(Coordinate2d.ORIGIN));
        HUD gc = new HUD(setup);
        gc.registerDebugger(debugger);
        sharedComponents.addComponent(gc);
        display = new MainDisplay(setup);
        display.addGUIControl(gc);
        commandQueue = new PriorityQueue<>(new GameCommandComparator());
        collectedPellets = new LinkedList<>();
        gameState = INITIALIZING;
    }

    private void processQueue() {
        Command<GameCommand, Object> command;
        command = commandQueue.peek();
        if (command != null && command.getCommand() != null) {
            switch (command.getCommand()) {
                case CHANGE_GAME_STATE:
                    GameState newGameState = (GameState) command.getParameters();
                    if (gameState == newGameState) {
                        gameState = previousGameState;
                    } else {
                        previousGameState = gameState;
                        gameState = newGameState;
                    }
                    debugger.setDebugModeActivated(gameState == DEBUG_MODE);
                    commandQueue.remove();
                    break;
                case TOGGLE_GRID:
                    debugger.toggleGridState();
                    commandQueue.remove();
                    break;
                case WALK:
                    if (player.changeWalkDirection((Integer) command.getParameters(), level)) {
                        commandQueue.remove();
                        changeDirectionRequired = false;
                    }
                    break;
                case TOGGLE_FREEZE_STATE:
                    frozenState = !frozenState;
                    commandQueue.remove();
                    break;
                case DEBUG_INSERT_TILE:
                    chainRedefineCells(false);
                    commandQueue.remove();
                    break;
                case DEBUG_DELETE_TILE:
                    chainRedefineCells(true);
                    commandQueue.remove();
                    break;
                case DEBUG_WRITE_FIELD_FILE:
                    if (debugger.isDebugActivated()) {
                        level.saveFieldConfiguration();
                    }
                    commandQueue.remove();
                    break;
                case COUNT_SCORE:
                    countScore((Pair<CellContent, Coordinate2i>)command.getParameters());
                    commandQueue.remove();
                    break;
                case CHANGE_GHOST_MODE:
                    
                    commandQueue.remove();
                    break;
                default:
                    
                    commandQueue.remove();
                    break;
            }
        }
    }

    private void countScore(Pair<CellContent, Coordinate2i> cell) {
        if (!collectedPellets.contains(cell.getRight())) {
            score += (Integer)cell.getLeft().bonus;
            collectedPellets.add(cell.getRight());
        }
    }
    
    private void chainRedefineCells(boolean erase) {
        CellContent content;
        Coordinate2i tilePos;
        if (!erase) {
            content = CellContent.getMatchingCellType(debugger.selectedTile);
            tilePos = new Coordinate2i(debugger.selectedTile);
        } else {
            content = EMPTY;
            tilePos = null;
        }
        if (debugger.selectedGridPosStart.equals(debugger.selectedGridPosEnd)) {
            level.setGridPosition(debugger.selectedGridPosStart.getScaled(32),
                    new Pair<>(content, tilePos));
        } else {
            int cellCount;
            int incrementSignal;
            boolean horizontal;
            if (debugger.selectedGridPosStart.x != debugger.selectedGridPosEnd.x) {
                incrementSignal = debugger.selectedGridPosEnd.x - debugger.selectedGridPosStart.x;
                horizontal = true;
            } else {
                incrementSignal = debugger.selectedGridPosEnd.y - debugger.selectedGridPosStart.y;
                horizontal = false;
            }
            cellCount = abs(incrementSignal);
            incrementSignal /= cellCount;
            cellCount = cellCount / 32;
            for (int i = 1; i <= cellCount + 1; i++) {
                level.setGridPosition(debugger.selectedGridPosStart.getScaled(32),
                        new Pair<>(content, tilePos));
                if (horizontal) {
                    debugger.selectedGridPosStart.x += incrementSignal * 32;
                } else {
                    debugger.selectedGridPosStart.y += incrementSignal * 32;
                }
            }
        }
        debugger.selectedGridPosStart.x = -1;
        debugger.selectedGridPosStart.y = -1;
    }

    @Override
    public void insertDrawableElements() {
        player = new PacmanPlayer(this);
        level = new PacmanLevel();
        level.registerDebugger(debugger);
        ghosts = new HashMap<>();
        PacmanGhost blinky = new PacmanGhost(this, PacmanGhost.BLINKY, new Coordinate2d(14 * 32, 14.5 * 32), new Coordinate2i(26, 0), false);
        PacmanGhost pinky = new PacmanGhost(this, PacmanGhost.PINKY, new Coordinate2d(12 * 32, 17.5 * 32), new Coordinate2i(0, 0), false);
        PacmanGhost inky = new PacmanGhost(this, PacmanGhost.INKY, new Coordinate2d(14 * 32, 17.5 * 32), new Coordinate2i(26, 36), false);
        PacmanGhost clyde = new PacmanGhost(this, PacmanGhost.CLYDE, new Coordinate2d(16 * 32, 17.5 * 32), new Coordinate2i(0, 36), false);
        ghosts.put(BLINKY, blinky);
        ghosts.put(PINKY, pinky);
        ghosts.put(INKY, inky);
        ghosts.put(CLYDE, clyde);
        sharedComponents.addComponent(player);
        sharedComponents.addComponent(blinky);
        sharedComponents.addComponent(pinky);
        sharedComponents.addComponent(inky);
        sharedComponents.addComponent(clyde);
        sharedComponents.addComponent(level);
        gameState = INGAME;
    }

    @Override
    public void sendCommand(Command<GameCommand, Object> command) {
        if (command.getCommand() == WALK) {
            if (!changeDirectionRequired) {
                commandQueue.add(command);
                changeDirectionRequired = true;
            } else {
                Queue<Command<GameCommand, Object>> aux = new PriorityQueue<>(new GameCommandComparator());
                Command<GameCommand, Object> enqueuedCommand;
                while (!commandQueue.isEmpty()) {
                    enqueuedCommand = commandQueue.remove();
                    if (enqueuedCommand.getCommand() != WALK) {
                        aux.add(enqueuedCommand);
                    }
                }
                commandQueue.addAll(aux);
                commandQueue.add(command);
            }
        } else {
            commandQueue.add(command);
        }
    }

    @Override
    public Object getAttribute(String attributeName) {
        if (attributeName.contains("GameState")) {
            return gameState;
        } else if (attributeName.contains("Score")) {
            return score;
        } else if (attributeName.contains("PlayerPosition")) {
            return player.getFieldPosition(level);
        } else if (attributeName.contains("PlayerDirection")) {
            return player.getActorDirection();
        } else if (attributeName.contains("FieldGridDimension")) {
            return level.getFieldSize();
        } else if (attributeName.contains("BlinkyPosition")) {
            return ghosts.get(BLINKY).getFieldPosition(level);
        }
        return null;
    }

    @Override
    public void gameLoop() {
        if (!commandQueue.isEmpty()) {
            //System.err.println("QUEUE: " + commandQueue);
        }
        if (gameState != INITIALIZING) {
            processQueue();
            if (!frozenState) {
                player.update(level);
            }
        }
    }

}
