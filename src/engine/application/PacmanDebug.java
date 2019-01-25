package engine.application;

import engine.utils.Coordinate2i;

public class PacmanDebug {

    public Coordinate2i selectedTile;
    public Coordinate2i selectedGridPosStart;
    public Coordinate2i selectedGridPosEnd;
    public boolean debuggerActivated;
    private final Coordinate2i tilesetDivisions;
    private int tile;

    public PacmanDebug(Coordinate2i tilesetDivisions, boolean activated) {
        this.selectedTile = new Coordinate2i(0, 0);
        this.selectedGridPosStart = new Coordinate2i(-1, -1);
        this.selectedGridPosEnd = new Coordinate2i(0, 0);
        this.tilesetDivisions = tilesetDivisions;
        this.debuggerActivated = activated;
        tile = 0;
    }

    public void updateSelectedTile(int tileSelectionShift) {
        tile += tileSelectionShift;
        System.err.println("TILE: " + tile);
        int tileNumber = tilesetDivisions.x * tilesetDivisions.y;
        if (tile >= tileNumber) {
            tile -= tileNumber;
        } else if (tile < 0) {
            tile += tileNumber;
        }
        selectedTile.x = tile % tilesetDivisions.x;
        selectedTile.y = tile / tilesetDivisions.x;
    }

}
