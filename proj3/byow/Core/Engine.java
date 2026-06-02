package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private String loadString = "";
    private static final File saveFile = new File(".cs61bBYOWgamesave.txt");

    private int avatarX, avatarY;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

        showHomeScreen();

        TETile[][] world = null;

        boolean preCharIsColon = false;

        while (true) {

            char c = getNextChar();

            if (c == ':') {
                preCharIsColon = true;
            } else if (c != 'Q') {
                preCharIsColon = false;
            }

            if (c == 'N') {
                if (world != null) {
                    continue;
                }

                loadString = "N";

                long seed = askForSeed();
                loadString += seed;
                loadString += 'S';

                world = createWorldWithSeed(seed);

                ter.initialize(WIDTH, HEIGHT);
                ter.renderFrame(world);
            } else if (c == 'L') {

                try {
                    loadString = Files.readString(saveFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                world = interactWithInputString(loadString);

                ter.initialize(WIDTH, HEIGHT);
                ter.renderFrame(world);
            } else if (c == 'Q') {
                if (preCharIsColon) {
                    try {
                        Files.writeString(saveFile.toPath(), this.loadString);
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (world == null) {
                        try {
                            Files.writeString(saveFile.toPath(), this.loadString);
                            return;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } else if (c == 'W') {
                if (world == null) {
                    continue;
                }
                loadString += 'W';
                moveAvatar(0, 1, world);
                ter.renderFrame(world);
            } else if (c == 'A') {
                if (world == null) {
                    continue;
                }
                loadString += 'A';
                moveAvatar(-1, 0, world);
                ter.renderFrame(world);
            } else if (c == 'S') {
                if (world == null) {
                    continue;
                }
                loadString += 'S';
                moveAvatar(0, -1, world);
                ter.renderFrame(world);
            } else if (c == 'D') {
                if (world == null) {
                    continue;
                }
                loadString += 'D';
                moveAvatar(1, 0, world);
                ter.renderFrame(world);
            }
        }
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

        TETile[][] finalWorldFrame = null;

        for (int pos = 0; pos < input.length(); pos++) {

            String inputType = input.substring(pos, pos + 1).toUpperCase();

            if (inputType.equals("N")) {
                String seedAsString = "0";
                pos++;
                while (pos < input.length()) {
                    String cur = input.substring(pos, pos + 1);
                    if (cur.toUpperCase().equals("S")) {
                        pos++;
                        break;
                    }
                    seedAsString += cur;
                    pos++;
                }

                loadString += "N" + seedAsString + "S";

                long seed = Long.parseLong(seedAsString);

                finalWorldFrame = createWorldWithSeed(seed);

            } else if (inputType.equals("L")) {
                try {
                    finalWorldFrame = interactWithInputString(Files.readString(saveFile.toPath()) + input.substring(1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return finalWorldFrame;
            } else if (inputType.equals("Q")) {
                if (pos > 0 && input.substring(pos - 1, pos).equals(":")) {
                    try {
                        Files.writeString(saveFile.toPath(), this.loadString);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return finalWorldFrame;
                }
            } else if (inputType.equals("W")) {
                if (finalWorldFrame == null) {
                    continue;
                }
                loadString += 'W';
                moveAvatar(0, 1, finalWorldFrame);

            } else if (inputType.equals("A")) {
                if (finalWorldFrame == null) {
                    continue;
                }
                loadString += 'A';
                moveAvatar(-1, 0, finalWorldFrame);
            } else if (inputType.equals("S")) {
                if (finalWorldFrame == null) {
                    continue;
                }
                loadString += 'S';
                moveAvatar(0, -1, finalWorldFrame);
            } else if (inputType.equals("D")) {
                if (finalWorldFrame == null) {
                    continue;
                }
                loadString += 'D';
                moveAvatar(1, 0, finalWorldFrame);
            }
        }

//        ter.initialize(WIDTH, HEIGHT);
//        ter.renderFrame(finalWorldFrame);

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
        addLockedDoorAndAvatar(world);

        return world;
    }

    /** Connects two rooms. */
    private void connectRooms(Room a, Room b, TETile[][] world) {
        int x1 = a.x + a.width / 2 + 1;
        int y1 = a.y + a.height / 2 + 1;

        int x2 = b.x + b.width / 2 + 1;
        int y2 = b.y + b.height / 2 + 1;

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
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

    /** Moves an avatar in the direction specified. */
    private void moveAvatar(int cx, int cy, TETile[][] world) {
        int nx = avatarX + cx;
        int ny = avatarY + cy;
        if (world[nx][ny].equals(Tileset.FLOOR)) {
            world[nx][ny] = Tileset.AVATAR;
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX = nx;
            avatarY = ny;
        }
    }


    /** Adds a locked door. */
    private void addLockedDoorAndAvatar(TETile[][] world) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (world[x][y].equals(Tileset.WALL)) {
                    for (int cx = -1; cx <= 1; cx++) {
                        for (int cy = -1; cy <= 1; cy++) {
                            int cntIsZero = 0;
                            if (cx == 0) {
                                cntIsZero++;
                            }
                            if (cy == 0) {
                                cntIsZero++;
                            }
                            if (cntIsZero == 1) {
                                if (world[x + cx][y + cy].equals(Tileset.FLOOR)) {
                                    world[x + cx][y + cy] = Tileset.AVATAR;
                                    world[x][y] = Tileset.LOCKED_DOOR;
                                    avatarX = x + cx;
                                    avatarY = y + cy;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** Makes a new world of nothing. */
    private static TETile[][] newWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        return world;
    }

    /** Shows the home screen. */
    private void showHomeScreen() {
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
        StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 + 6, "The Very Random Game");

        StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
        StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 - 3, "New Game (N)");
        StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 - 6, "Load Game (L)");
        StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 - 9, "Quit (Q)");

        StdDraw.show();
    }

    private char getNextChar() {
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(10);
        }
        return Character.toUpperCase(StdDraw.nextKeyTyped());
    }

    private long askForSeed() {

        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
        StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 + 6, "Please enter a seed");
        StdDraw.show();

        StringBuilder seedString = new StringBuilder();
        while (true) {
            char c = getNextChar();
            if (c == 'S') {
                break;
            }
            seedString.append(c);
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 + 6, "Please enter a seed");
            StdDraw.text(this.WIDTH / 2.0, this.HEIGHT / 2.0 + 3, seedString.toString());
            StdDraw.show();
        }
        return Long.parseLong(seedString.toString());
    }
}
