package engine.application;

import engine.core.Command;
import java.util.Comparator;

public class GameCommandComparator implements Comparator<Command<GameCommand, Object>>{

    @Override
    public int compare(Command<GameCommand, Object> o1, Command<GameCommand, Object> o2) {
        return o1.getCommand().priority - o2.getCommand().priority;
    }

    @Override
    public Comparator<Command<GameCommand, Object>> reversed() {
        return Comparator.super.reversed();
    }
    
}
