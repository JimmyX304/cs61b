package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  Stores:
 *   A commit message
 *   A date this commit was created
 *   The parent of the commit
 *   Any files tracked by this commit
 *   Any files updated by this commit
 *
 *  @author Hanyuan Xu
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;
    /** The date this Commit was made. */
    private Date date;
    /** The parent of this commit, stored as a SHA1 hash. */
    private String parent;
    /** The second parent of this commit. Null if there is no second parent. */
    private String parent2;

    /** All files tracked by this commit. */
    private HashMap<String, String> trackedFiles = new HashMap<>();

    /** All files updated in this commit. */
    private HashMap<String, String> updatedFiles = new HashMap<>();

    public Commit(String message, String par) {
        this.message = message;
        this.date = new Date();
        this.parent = par;
        this.parent2 = "";
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

    /** Gets the second parent of the Commit. */
    public String getSecondParent() {
        return parent2;
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
