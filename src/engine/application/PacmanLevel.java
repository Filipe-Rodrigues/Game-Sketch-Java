package engine.application;

import engine.core.Level;
import engine.graphics.CameraControl;
import engine.utils.Coordinate2i;
import static engine.utils.DrawingUtils.*;
import static engine.utils.Constants.*;
import static engine.application.CellContent.*;
import engine.utils.Coordinate2d;
import engine.utils.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PacmanLevel extends Level {

    private Pair<CellContent, Coordinate2i>[][] field;
    private PacmanDebug debugger;
    private int powerPelletBlinkCount = 0;

    public PacmanLevel() {
        super(new Coordinate2i(28, 36), "tileset_b_32.png", 32, 32);
        initializeField();
        loadDrawingCoords();
    }

    @Override
    public void draw(CameraControl camera) {
        enableTexture();
        enableTransparency();
        tileset.startUse();

        for (int i = 0; i < fieldSize.x; i++) {
            for (int j = 0; j < fieldSize.y; j++) {
                if (field[i][j].getRight() != null) {
                    if (field[i][j].getLeft() != POWER_PELLET) {
                        tileset.getSubImage(field[i][j].getRight().x, field[i][j].getRight().y)
                            .drawEmbedded(32 * i, 32 * j, 32, 32);
                    } else {
                        if (powerPelletBlinkCount % 40 < 20) {
                            tileset.getSubImage(field[i][j].getRight().x, field[i][j].getRight().y)
                                    .drawEmbedded(32 * i, 32 * j, 32, 32);
                        } else {
                            tileset.endUse();
                            tileset.getSubImage(field[i][j].getRight().x, field[i][j].getRight().y)
                                    .drawFlash(32 * i, 32 * j, 32, 32);
                            tileset.startUse();
                        }
                        powerPelletBlinkCount++;
                        if (powerPelletBlinkCount > 40) {
                            powerPelletBlinkCount = 0;
                        }
                    }
                }
            }
        }
        if (debugger.isDebugActivated()) {
            tileset.getSubImage(debugger.selectedTile.x, debugger.selectedTile.y)
                    .drawEmbedded(debugger.selectedGridPosEnd.x, debugger.selectedGridPosEnd.y, 32, 32);
        }
        tileset.endUse();
        disableTransparency();
        disableTexture();
    }

    private void initializeField() {
        field = new Pair[fieldSize.x][fieldSize.y];
        for (int i = 0; i < fieldSize.x; i++) {
            for (int j = 0; j < fieldSize.y; j++) {
                field[i][j] = new Pair<>(EMPTY, null);
            }
        }
    }

    public void registerDebugger(PacmanDebug debugger) {
        this.debugger = debugger;
    }
    
    private void loadDrawingCoords() {
        try {
            File f = new File(CONFIG_DIR + "levelLayout");
            FileReader fr = new FileReader(f);
            try (BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("")) {
                        continue;
                    }
                    String[] params = line.split(" ");
                    int fx = Integer.parseInt(params[0]);
                    int fy = Integer.parseInt(params[1]);
                    int tx = Integer.parseInt(params[2]);
                    int ty = Integer.parseInt(params[3]);
                    Coordinate2i texturePosition = new Coordinate2i(tx, ty);
                    field[fx][fy] = new Pair<>(CellContent.getMatchingCellType(texturePosition), texturePosition);
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File for field tileset not found.");
        } catch (IOException ex) {
            System.err.println("I/O error.");
        }
    }

    public Pair<CellContent, Coordinate2i>[][] getFieldConfiguration() {
        return field;
    }

    public void saveFieldConfiguration() {
        try {
            File f = new File(CONFIG_DIR + "newLayout");
            FileWriter fw = new FileWriter(f);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                String line;
                for (int i = 0; i < fieldSize.x; i++) {
                    for (int j = 0; j < fieldSize.y; j++) {
                        if (field[i][j].getLeft() != EMPTY) {
                            line = i + " " + j + " " + field[i][j].getRight().x + " " + field[i][j].getRight().y;
                            bw.write(line);
                            bw.newLine();
                        }
                    }
                }
                bw.flush();
            }
        } catch (IOException ex) {
            System.err.println("FAILED WRITING OUTPUT");
        }
    }
    
    @Override
    public void update() {
    }

    @Override
    public Coordinate2d getGridPosition(Coordinate2d absolutePosition) {
        Coordinate2d gridPosition = new Coordinate2d(absolutePosition.getScaled(1d / (double) GRID_RESOLUTION));

        return gridPosition;
    }

    @Override
    public void setGridPosition(Coordinate2i position, Pair<CellContent, Coordinate2i> cellType) {
        if (position.x >= 0 && position.x < fieldSize.x
                && position.y >= 0 && position.y < fieldSize.y) {
            field[position.x][position.y] = cellType;
        }
    }

}
