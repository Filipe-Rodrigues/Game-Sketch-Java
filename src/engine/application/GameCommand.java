package engine.application;

public enum GameCommand {
    TOGGLE_GRID(10),
    TOGGLE_FREEZE_STATE(10),
    CHANGE_GAME_STATE(10),
    WALK(15),
    DEBUG_INSERT_TILE(10),
    DEBUG_DELETE_TILE(10),
    DEBUG_WRITE_FIELD_FILE(10);
    
    public int priority;
    
    private GameCommand(int priority) {
        this.priority = priority;
    }
}
