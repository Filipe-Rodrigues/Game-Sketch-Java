package engine.application.ai;

import static engine.application.ai.GhostAIModifier.*;
import engine.utils.Coordinate2i;
import java.util.HashMap;
import java.util.Map;

public class AIFieldConstraints {

    private static final Map<Coordinate2i, GhostAIModifier> AI_MODIFIERS = new HashMap<>();

    private double speedPenaultyMult;
    private final Coordinate2i restrictedDirection;
    
    static {
        AI_MODIFIERS.put(new Coordinate2i(0, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(1, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(2, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(3, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(4, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(5, 17), SPEED_MULT);

        AI_MODIFIERS.put(new Coordinate2i(22, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(23, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(24, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(25, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(26, 17), SPEED_MULT);
        AI_MODIFIERS.put(new Coordinate2i(27, 17), SPEED_MULT);

        AI_MODIFIERS.put(new Coordinate2i(11, 8), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(12, 8), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(13, 8), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(14, 8), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(15, 8), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(16, 8), MOVEMENT_RESTRICTION);

        AI_MODIFIERS.put(new Coordinate2i(11, 26), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(12, 26), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(13, 26), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(14, 26), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(15, 26), MOVEMENT_RESTRICTION);
        AI_MODIFIERS.put(new Coordinate2i(16, 26), MOVEMENT_RESTRICTION);
    }
    
    public AIFieldConstraints(double speedPenaulty, Coordinate2i restrictedDir) {
        speedPenaultyMult = speedPenaulty;
        restrictedDirection = new Coordinate2i(restrictedDir);
    }
    
    public void setSpeedPenaultyMult(double multiplier) {
        speedPenaultyMult = multiplier;
    }
    
    public GhostAIModifier getAIMod(Coordinate2i gridPos) {
        return AI_MODIFIERS.get(gridPos);
    }

    public double getSpeedPenaultyMult() {
        return speedPenaultyMult;
    }

    public Coordinate2i getRestrictedDirection() {
        return restrictedDirection;
    }
    
}
