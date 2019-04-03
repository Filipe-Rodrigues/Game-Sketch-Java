package engine.application;

public enum GameCommand {
    TOGGLE_GRID(10),
    TOGGLE_FREEZE_STATE(10),
    CHANGE_GAME_STATE(10),
    WALK(15),
    DEBUG_INSERT_TILE(10),
    DEBUG_DELETE_TILE(10),
    DEBUG_WRITE_FIELD_FILE(10),
    DEBUG_INSERT_AI_MOD(10),
    
    COUNT_SCORE(10),
    FRIGHTEN_ALL_GHOSTS(6),
    UNFRIGHTEN_ALL_GHOSTS(6),
    SCATTER_GHOSTS(6),
    ORDER_GHOSTS(6),
    ATTACK_GHOST(6),
    CHANGE_GHOST_MODE(6),
    KILL_PLAYER(5);
    
    public int priority;
    
    private GameCommand(int priority) {
        this.priority = priority;
    }
}
