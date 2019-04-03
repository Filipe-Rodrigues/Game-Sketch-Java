package engine.application;

import engine.application.ai.PacmanGhost;
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
import engine.application.ai.Blinky;
import engine.application.ai.Clyde;
import static engine.application.ai.GhostAIMode.*;
import engine.application.ai.Inky;
import static engine.application.ai.PacmanGhost.*;
import engine.application.ai.Pinky;
import engine.core.EventQueue;
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
    private EventQueue<Command<GameCommand, Object>> autoTriggeredEvents;
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
        autoTriggeredEvents = new EventQueue<>();
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
                    if (player.changeWalkDirection((Coordinate2i) command.getParameters(), level)) {
                        commandQueue.remove();
                        changeDirectionRequired = false;
                    }
                    break;
                case TOGGLE_FREEZE_STATE:
                    frozenState = !frozenState;
                    commandQueue.remove();
                    level.changeSoundLoop("NORMAL", 1f);
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
                case DEBUG_INSERT_AI_MOD:

                    break;
                case COUNT_SCORE:
                    countScore((Pair<CellContent, Coordinate2i>) command.getParameters());
                    commandQueue.remove();
                    break;
                case FRIGHTEN_ALL_GHOSTS:
                    for (PacmanGhost ghost : ghosts.values()) {
                        ghost.changeMode(FLEE);
                    }
                    autoTriggeredEvents.enqueueEvent(new Command<>(UNFRIGHTEN_ALL_GHOSTS, null), 6000);
                    commandQueue.remove();
                    break;
                case UNFRIGHTEN_ALL_GHOSTS:
                    for (PacmanGhost ghost : ghosts.values()) {
                        ghost.changeMode(PURSUE);
                    }
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
            score += (Integer) cell.getLeft().bonus;
            collectedPellets.add(cell.getRight());
        }
    }

    private void processAutoTriggeredEvents() {
        Command<GameCommand, Object> triggeredCommand = autoTriggeredEvents.tick();
        if (triggeredCommand != null) {
            commandQueue.add(triggeredCommand);
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
            level.setGridPosition(debugger.selectedGridPosStart.getDividedByScalar(32),
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
                level.setGridPosition(debugger.selectedGridPosStart.getDividedByScalar(32),
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
        level = new PacmanLevel(0.5);
        level.registerDebugger(debugger);
        ghosts = new HashMap<>();
        PacmanGhost blinky = new Blinky(this);
        PacmanGhost pinky = new Pinky(this);
        PacmanGhost inky = new Inky(this);
        PacmanGhost clyde = new Clyde(this);
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
        level.registerActor(blinky);
        level.registerActor(pinky);
        level.registerActor(inky);
        level.registerActor(clyde);
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
        } else if (attributeName.contains("PinkyPosition")) {
            return ghosts.get(PINKY).getFieldPosition(level);
        } else if (attributeName.contains("InkyPosition")) {
            return ghosts.get(INKY).getFieldPosition(level);
        } else if (attributeName.contains("ClydePosition")) {
            return ghosts.get(CLYDE).getFieldPosition(level);
        } else if (attributeName.contains("CurrentLevel")) {
            return level;
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
            processAutoTriggeredEvents();
            if (!frozenState) {
                player.update(level);
                for (PacmanGhost ghost : ghosts.values()) {
                    ghost.update(level);
                }
            }
        }
    }

}
