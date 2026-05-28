package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Hanyuan Xu
 */
public class Commit implements Serializable {
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
    /** The parent of this commit, stored as a SHA1 hash. */
    private String parent;

    /** All files tracked by this commit. */
    private HashMap<String, String> trackedFiles = new HashMap<>();

    /** All files updated in this commit. */
    private HashMap<String, String> updatedFiles = new HashMap<>();

    public Commit(String message, String par) {
        this.message = message;
        this.date = new Date();
        this.parent = par;
        if (par.equals("")) {
            this.date = new Date(0);
        } else {
            Commit parentCommit = Repository.readCommitFromHash(par);
            trackedFiles.putAll(parentCommit.getTrackedFiles());
        }
    }

    /** Adds a file into the commit. */
    public void addFile(String fileName) throws IOException {
        Blob b = new Blob(join(Repository.CWD, fileName));
        updatedFiles.put(fileName, sha1(b.getContents()));
        trackedFiles.put(fileName, sha1(b.getContents()));
    }

    /** Removes a file from the commit. */
    public void rmFile(String fileName) {
        updatedFiles.remove(fileName);
        trackedFiles.remove(fileName);
    }

    /** Gets the message of the Commit. */
    public String getMessage() {
        return message;
    }

    /** Gets the date the Commit was created. */
    public Date getDate() {
        return date;
    }

    /** Gets the parent of the Commit. */
    public String getParent() {
        return parent;
    }

    /** Gets the tracked files of the Commit. */
    public HashMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    /** Checks if a file is tracked. */
    public boolean isTracked(String fileName) {
        return trackedFiles.containsKey(fileName);
    }

    /** Gets the Blob of a file. Returns null if file doesn't exist. */
    public Blob getBlob(String fileName) {
        if (isTracked(fileName)) {
            return readObject(join(Repository.BLOB_DIR, trackedFiles.get(fileName)), Blob.class);
        }
        return null;
    }
}
