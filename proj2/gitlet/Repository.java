package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Hanyuan Xu
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static File COMMIT_DIR;
    /** A set used to keep track of files */
    public static TreeSet<String> fileSet = new TreeSet<>();

    /* TODO: fill in the rest of this class. */

    /** Sets up persistence */
    public static void setUpPersistence() throws IOException {
        GITLET_DIR.mkdir();
        COMMIT_DIR = join(GITLET_DIR, "commits");
        COMMIT_DIR.mkdir();
        addInitCommit();
    }

    /** Adds the initial commit message */
    public static void addInitCommit() {
        Commit commitToAdd = new Commit("initial commit");

    }

    /** Checks if a file exists */
    public static boolean fileExists(String fileName) {
        return fileSet.contains(fileName);
    }

    /** Adds a file to the staging area for commits
      * Returns false if the file doesn't exist
      */
    public static boolean add(String fileName) {
        if (!fileExists(fileName)) {
            return false;
        }

        return true;
    }

}
