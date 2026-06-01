package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {

        TETile[][] finalWorldFrame = newWorld();

        String inputType = input.substring(0, 1).toLowerCase();
        if (inputType.equals("n")) {

            String seedAsString = "0";
            for (int i = 1; i < input.length(); i++) {
                String cur = input.substring(i, i + 1);
                if (cur.toLowerCase().equals("s")) {
                    break;
                }
                seedAsString += cur;
            }

            long seed = Long.parseLong(seedAsString);

            finalWorldFrame = createWorldWithSeed(seed);

        } else if (inputType.equals("l")) {

        } else if (inputType.equals("q")) {

        }

        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(finalWorldFrame);

        return finalWorldFrame;
    }

    /** Generates a random world with the given seed. */
    /* Idea:
     *   Randomly generate rooms using the seed
     *    - Use the seed to get the bottom-left corner
     *    - Use the seed to get side lengths
     *    - Check if there are any overlaps - if there are, then don't use this room
     *   Then, connect these rooms using hallways.
     * */
    private TETile[][] createWorldWithSeed(long seed) {
        TETile[][] world = newWorld();
        Random RANDOM = new Random(seed);

        List<Room> rooms = new ArrayList<>();
        for (int roomID = 1; roomID <= 40; roomID++) {

            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            int width = RANDOM.nextInt(8) + 2;
            int height = RANDOM.nextInt(8) + 1;

            if (x + width + 1 >= WIDTH || y + height + 1 >= HEIGHT) {
                continue;
            }

            Room newRoom = new Room(x, y, width, height);

            boolean canAdd = true;
            for (Room other : rooms) {
                if (newRoom.intersect(other)) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                addRoom(newRoom, world);
                rooms.add(newRoom);
            }
        }

        for (int i = 0; i + 1 < rooms.size(); i++) {
            connectRooms(rooms.get(i), rooms.get(i + 1), world);
        }

        addWalls(world);

        return world;
    }

    /** Connects two rooms. */
    private void connectRooms(Room a, Room b, TETile[][] world) {
        int x1 = a.x + a.width / 2 + 1;
        int y1 = a.y + a.height / 2 + 1;

        int x2 = b.x + b.width / 2 + 1;
        int y2 = b.y + b.height / 2 + 1;

        for (int x = Math.min(x1, x2); x <= Math.max(x2, x2); x++) {
            world[x][y1] = Tileset.FLOOR;
        }

        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            world[x2][y] = Tileset.FLOOR;
        }

    }

    /** Adds a room to the existing world. */
    private void addRoom(Room r, TETile[][] world) {
        for (int i = r.x + 1; i <= r.x + r.width; i++) {
            for (int j = r.y + 1; j <= r.y + r.height; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
    }

    /** Adds walls to a world. */
    private void addWalls(TETile[][] world) {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.FLOOR)) {
                    for (int cx = -1; cx <= 1; cx++) {
                        for (int cy = -1; cy <= 1; cy++) {
                            if (world[i + cx][j + cy].equals(Tileset.NOTHING)) {
                                world[i + cx][j + cy] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    /** Makes a new world of nothing. */
    private TETile[][] newWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        return world;
    }
}
