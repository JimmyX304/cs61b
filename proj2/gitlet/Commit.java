package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Hanyuan Xu
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The date this Commit was made. */
    private Date date;

    /* TODO: fill in the rest of this class. */

    public Commit(String message) {
        this.message = message;
        this.date = new Date(1970, 1, 1, 0, 0, 0);
    }

    public Commit(String message, Date dt) {
        this.message = message;
        this.date = dt;
    }
}
