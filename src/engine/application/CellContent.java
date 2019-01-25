package engine.application;

import engine.utils.Coordinate2i;

public enum CellContent {
    EMPTY(0),
    BLOCK(0),
    PELLET(10),
    POWER_PELLET(50),
    CHERRY(100),
    STRAWBERRY(300),
    ORANGE(500),
    APPLE(700),
    MELON(1000),
    GALAXIAN_BOSS(2000),
    BELL(3000),
    KEY(5000);

    public final int bonus;

    private CellContent(int bonus) {
        this.bonus = bonus;
    }

    public static final CellContent getMatchingCellType(Coordinate2i tilesetPosition) {
        if (tilesetPosition.y < 2) {
            return BLOCK;
        } else {
            switch (tilesetPosition.x) {
                case 0:
                    return PELLET;
                case 1:
                    return POWER_PELLET;
                case 2:
                    return CHERRY;
                case 3:
                    return STRAWBERRY;
                case 4:
                    return ORANGE;
                case 5:
                    return APPLE;
                case 6:
                    return MELON;
                case 7:
                    return GALAXIAN_BOSS;
                case 8:
                    return BELL;
                case 9:
                    return KEY;
                default:
                    return BLOCK;
            }
        }
    }
}
