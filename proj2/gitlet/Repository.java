package gitlet;

import edu.princeton.cs.algs4.Heap;
import edu.princeton.cs.algs4.ST;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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

    /** The branch directory. All branches are stored here. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");


    /** The head file. The contents should be the filename of the most recent commit in the current branch. */
    public static final File headBranch = join(GITLET_DIR, "headBranch");


    /** Sets up persistence. */
    public static void init() throws IOException {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();
        headBranch.createNewFile();

        setHeadToBranch("master");

        addInitBranch();
        addInitCommit();
    }

    /** Adds the initial commit message. */
    public static void addInitCommit() throws IOException {
        makeCommit("initial commit");
    }

    /** Adds the initial branch. */
    public static void addInitBranch() throws IOException {
        addBranch("master");
    }

    /** Checks if a file exists. */
    public static boolean fileExists(String fileName) {
        File fileToCheck = join(CWD, fileName);
        return fileToCheck.exists();
    }

    /** Adds a file to the staging area for commits.
      * Aborts and returns false if the file doesn't exist.
      */
    public static boolean addToStage(String fileName) throws IOException {
        if (!fileExists(fileName)) {
            return false;
        }

        File newFile = join(STAGING_DIR, fileName);
        File currentFile = join(CWD, fileName);

        if (newFile.exists()) {
            if (Arrays.equals(readContents(newFile), readContents(currentFile))) {
                newFile.delete();
            }
        } else {
            newFile.createNewFile();
            writeObject(newFile, readContentsAsString(currentFile));
        }

        return true;
    }

    /** Makes a commit with the given message and adds it to the commit directory.
     * All files staged for addition or removal are saved in the new commit, and the staging are is cleared.
     */
    public static void makeCommit(String msg) throws IOException {
        Commit c = new Commit(msg, getHead());

        List<String> filesToChange = plainFilenamesIn(STAGING_DIR);
        if (filesToChange != null) {
            for (String fileName : filesToChange) {
                c.addFile(fileName);
                join(STAGING_DIR, fileName).delete();
            }
        } else {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        String fileName = sha1(serialize(c));

        File commitFile = join(COMMIT_DIR, fileName);
        writeObject(commitFile, c);

        setHeadOfBranch(fileName);
    }

    /** Adds a branch. */
    public static void addBranch(String branchName) throws IOException {
        File newBranch = join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            newBranch.createNewFile();
            writeContents(newBranch, getHead());
        }
    }

    /** Restores a file in a given commit. */
    public static void checkout(String commitID, String fileName) {
        File commitFile = join(COMMIT_DIR, commitID);
        if (commitFile.exists()) {
            Commit c = readObject(commitFile, Commit.class);
            Blob b = c.getBlob(fileName);
            if (b != null) {
                File origFile = join(CWD, fileName);
                writeContents(origFile, b.getContents());
            } else {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    /** Sets the head to the given branch. */
    public static void setBranch(String branchName) throws IOException {
        File branch = join(BRANCH_DIR, branchName);
        if (!branch.exists()) {
            branch.createNewFile();
        }
        setHeadToBranch(branchName);
    }

    /** Outputs a log of commits. */
    public static void outputLog() {
        String pointer = getHead();
        while (!pointer.equals("null")) {
            Commit c = readObject(join(COMMIT_DIR, pointer), Commit.class);

            Date d = c.getDate();

            System.out.println("===");
            System.out.println("commit " + pointer);
            System.out.println("Date: " + String.format(Locale.ENGLISH, "%ta %tb %te %tT %tY %tz", d, d, d, d, d, d));
            System.out.println(c.getMessage());
            System.out.println();

            pointer = c.getParent();
        }
    }


    /** Gets the current head. */
    public static String getHead() {
        return readContentsAsString(join(BRANCH_DIR, readContentsAsString(headBranch)));
    }

    /** Sets the head pointer to branchName. */
    public static void setHeadToBranch(String branchName) {
        writeContents(headBranch, branchName);
    }

    public static void setHeadOfBranch(String hash) {
        writeContents(join(BRANCH_DIR, readContentsAsString(headBranch)), hash);
    }
}
