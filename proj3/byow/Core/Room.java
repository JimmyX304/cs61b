package byow.Core;

/** A class for a Room.
 *  Stores
 *   - The bottom left coordinates
 *   - The width
 *   - The height
 * */
public class Room {
    public int x;
    public int y;
    public int width;
    public int height;

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
}
