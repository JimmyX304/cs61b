package byow.Core;

/** A class for a Room.
 *  Stores
 *   - The bottom left coordinates
 *   - The width
 *   - The height
 * */
public class Room {
    private int x;
    private int y;
    private int width;
    private int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Returns whether two rooms intersect. */
    public boolean intersect(Room other) {
        if (this.x + this.width <= other.x || other.x + other.width <= this.x) {
            return false;
        }
        if (this.y + this.height <= other.y || other.y + other.height <= this.y) {
            return false;
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
