package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 35;
    private static final Random RANDOM = new Random();

    /** Function to get a random tile. */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.SAND;
            case 4: return Tileset.MOUNTAIN;
            case 5: return Tileset.WATER;
        }
        return null;
    }

    /** Initializes all tiles to null. */
    public static void initializeTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Adds a hexagon.
     * s is the side length of the hexagon
     * (X, Y) should be the lower-left point of the hexagon
     * */
    public static void addHexagon(int s, int X, int Y, TETile[][] world) {
        TETile tile = randomTile();

        for (int l = 0; l < s; l++) {
            int y = Y - l;
            for (int x = X + l; x < X + 3 * s - l - 2; x++) {
                world[x][y] = tile;
            }
        }

        for (int l = 0; l < s; l++) {
            int y = Y + l + 1;
            for (int x = X + l; x < X + 3 * s - l - 2; x++) {
                world[x][y] = tile;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        initializeTiles(randomTiles);

        addHexagon(3, 0, 8, randomTiles);
        addHexagon(3, 0, 14, randomTiles);
        addHexagon(3, 0, 20, randomTiles);

        addHexagon(3, 5, 5, randomTiles);
        addHexagon(3, 5, 11, randomTiles);
        addHexagon(3, 5, 17, randomTiles);
        addHexagon(3, 5, 23, randomTiles);

        addHexagon(3, 10, 2, randomTiles);
        addHexagon(3, 10, 8, randomTiles);
        addHexagon(3, 10, 14, randomTiles);
        addHexagon(3, 10, 20, randomTiles);
        addHexagon(3, 10, 26, randomTiles);

        addHexagon(3, 15, 5, randomTiles);
        addHexagon(3, 15, 11, randomTiles);
        addHexagon(3, 15, 17, randomTiles);
        addHexagon(3, 15, 23, randomTiles);

        addHexagon(3, 20, 8, randomTiles);
        addHexagon(3, 20, 14, randomTiles);
        addHexagon(3, 20, 20, randomTiles);

        ter.renderFrame(randomTiles);
    }
}
