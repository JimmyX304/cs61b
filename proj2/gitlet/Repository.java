package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Hanyuan Xu
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The staging area. This is where all files staged for addition or removal are put. */
    public static final File STAGING_DIR = join(GITLET_DIR, "stage");;

    /** The commit directory. Folder that stores all the commits. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    /** The blob directory. This is where the text written in files is stored. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");


    /** The head pointer. It should be the SHA1 hash of the latest commit. */
    public static String head = null;





    /** Sets up persistence. */
    public static void init() throws IOException {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();

        addInitCommit();
    }



    /** Adds the initial commit message. */
    public static void addInitCommit() throws IOException {
        makeCommit("initial commit");
    }

    /** Checks if a file exists. */
    public static boolean fileExists(String fileName) {
        File fileToCheck = join(CWD, fileName);
        return fileToCheck.exists();
    }

    /** Adds a file to the staging area for commits.
      * Aborts and returns false if the file doesn't exist.
      */
    public static boolean add(String fileName) throws IOException {
        if (!fileExists(fileName)) {
            return false;
        }
        // TODO: fill in this function

        File newFile = join(STAGING_DIR, fileName);
        newFile.createNewFile();

        writeObject(newFile, readContentsAsString(join(CWD, fileName)));

        return true;
    }

    /** Adds a commit into the commit directory. */
    // TODO: Mabye delete this function?
    public static void addCommit(Commit c) {
        File outfile = join(Repository.COMMIT_DIR, sha1(c));
        writeObject(outfile, c);
    }


    /** Makes a commit with the given message and adds it to the commit directory.
     * Here, all files staged for addition or removal should be saved.
     * This where the head pointer should be updated.
     */
    public static void makeCommit(String msg) throws IOException {
        Commit c = new Commit(msg, head);

        List<String> filesToChange = plainFilenamesIn(STAGING_DIR);
        if (filesToChange != null) {
            for (String fileName : filesToChange) {
                c.addFile(fileName);
                join(STAGING_DIR, fileName).delete();
            }
        }

        // TODO: make head equal to the current Commit
        head = null;
    }
}
