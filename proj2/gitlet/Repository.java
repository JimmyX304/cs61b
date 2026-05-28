package gitlet;

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

    /** The add staging area. This is where all files staged for addition are put. */
    public static final File STAGEADD = join(GITLET_DIR, "stageadd");

    /** The rm staging area. This is where all files staged for removal are put. */
    public static final File STAGERM = join(GITLET_DIR, "stagerm");

    /** The commit directory. Folder that stores all the commits. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    /** The blob directory. This is where the text written in files is stored. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    /** The branch directory. All branches are stored here. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");


    /** The head file. The contents should be the filename of the most recent
     * commit in the current branch. */
    public static final File headBranch = join(GITLET_DIR, "headBranch");


    /** Sets up persistence. */
    public static void init() throws IOException {
        GITLET_DIR.mkdir();
        STAGEADD.mkdir();
        STAGERM.mkdir();
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

        File newFile = join(STAGEADD, fileName);
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

    /** Removes a file from STAGEADD only if it exists there. */
    public static boolean removeFromStageAdd(String fileName) {
        if (plainFilenamesIn(STAGEADD).contains(fileName)) {
            File fileToRemove = join(STAGEADD, fileName);
            fileToRemove.delete();
            return true;
        }
        return false;
    }

    /** Stages a file for removal. */
    public static boolean stageForRemoval(String fileName) throws IOException {
        Commit c = readObject(join(COMMIT_DIR, getHead()), Commit.class);
        if (c.isTracked(fileName)) {
            File newFile = join(STAGERM, fileName);
            newFile.createNewFile();

            File rmFile = join(CWD, fileName);
            if (rmFile.exists()) {
                rmFile.delete();
            }
            return true;
        }
        return false;
    }

    /** Removes a file based on the rm command. */
    public static void removeFile(String fileName) throws IOException {
        boolean didAnyOps = false;
        didAnyOps |= removeFromStageAdd(fileName);
        didAnyOps |= stageForRemoval(fileName);

        if (didAnyOps == false) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** Makes a commit with the given message and adds it to the commit directory.
     * All files staged for addition or removal are saved in the new commit, and the
     * staging are is cleared.
     */
    public static void makeCommit(String msg) throws IOException {
        Commit c = new Commit(msg, getHead());

        List<String> filesToAdd = plainFilenamesIn(STAGEADD);
        List<String> filesToRm = plainFilenamesIn(STAGERM);

        if (filesToAdd != null) {
            for (String fileName : filesToAdd) {
                c.addFile(fileName);
                join(STAGEADD, fileName).delete();
            }
        } else {
            if (filesToRm == null) {
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
        }

        if (filesToRm != null) {
            for (String fileName : filesToRm) {
                c.rmFile(fileName);
                join(STAGERM, fileName).delete();
            }
        }

        String fileName = sha1(serialize(c));

        File commitFile = join(COMMIT_DIR, fileName);
        writeObject(commitFile, c);

        setHeadOfBranch(fileName);
    }

    /** Returns the commit with the given hash. */
    public static Commit readCommitFromHash(String hash) {
        return readObject(join(COMMIT_DIR, hash), Commit.class);
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

    /** Removes a branch. */
    public static void removeBranch(String branchName) {
        File currentBranch = join(BRANCH_DIR, branchName);
        if (currentBranch.exists()) {
            if (!getHeadBranch().equals(branchName)) {
                currentBranch.delete();
            } else {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            }
        } else {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
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

    public static void getCommitsWithMessage(String commitMessage) {
        List<String> commitIDs = plainFilenamesIn(COMMIT_DIR);
        List<String> validCommits = new LinkedList<>();
        for (String commitID : commitIDs) {
            Commit c = readObject(join(COMMIT_DIR, commitID), Commit.class);
            if (c.getMessage().equals(commitMessage)) {
                validCommits.add(commitID);
            }
        }

        if (!validCommits.isEmpty()) {
            for (String commit : validCommits) {
                System.out.println(commit);
            }
        } else {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Outputs a log of commits in the current branch. */
    public static void outputLog() {
        String pointer = getHead();
        while (!pointer.equals("")) {
            Commit c = readObject(join(COMMIT_DIR, pointer), Commit.class);
            outputCommitLog(c, pointer);
            pointer = c.getParent();
        }
    }

    /** Outputs a log of all commits. */
    public static void outputGlobalLog() {
        List<String> commitIDs = plainFilenamesIn(COMMIT_DIR);
        for (String commitID : commitIDs) {
            Commit c = readObject(join(COMMIT_DIR, commitID), Commit.class);
            outputCommitLog(c, commitID);
        }
    }

    /** Outputs the commit c as a log. */
    public static void outputCommitLog(Commit c, String cID) {
        Date d = c.getDate();

        System.out.println("===");
        System.out.println("commit " + cID);
        System.out.println("Date: " + String.format(Locale.ENGLISH,
                "%ta %tb %te %tT %tY %tz", d, d, d, d, d, d));
        System.out.println(c.getMessage());
        System.out.println();
    }

    /** Outputs the status. */
    public static void outputStatus() {
        outputBranches();
        outputStagedFiles();
        outputRemovedFiles();
        outputModificationsNotStagedForCommit();
        outputUntrackedFiles();
    }

    /** Outputs the branches. */
    public static void outputBranches() {
        System.out.println("=== Branches ===");

        String headbranch = getHeadBranch();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        if (branches != null) {
            for (String curBranch : branches) {
                if (curBranch.equals(headbranch)) {
                    System.out.print("*");
                }
                System.out.println(curBranch);
            }
        }
        System.out.println();
    }

    /** Outputs staged files. */
    public static void outputStagedFiles() {
        System.out.println("=== Staged Files ===");
        List<String> addFiles = plainFilenamesIn(STAGEADD);
        if (addFiles != null) {
            for (String fileName : addFiles) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    /** Outputs removed files. */
    public static void outputRemovedFiles() {
        System.out.println("=== Removed Files ===");
        List<String> rmFiles = plainFilenamesIn(STAGERM);
        if (rmFiles != null) {
            for (String fileName : rmFiles) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    /** Outputs modifications not staged for commit. */
    public static void outputModificationsNotStagedForCommit() {
        // TODO: fill in this function
        System.out.println("=" + "== Modifications Not Staged For Commit ===");


        System.out.println();
    }

    /** Outputs untracked files. */
    public static void outputUntrackedFiles() {
        // TODO: fill in this function
        System.out.println("=== Untracked Files ===");


        System.out.println();
    }

    /** Gets the current head. */
    public static String getHead() {
        return readContentsAsString(join(BRANCH_DIR, getHeadBranch()));
    }
    /** Gets the current branch. */
    public static String getHeadBranch() {
        return readContentsAsString(headBranch);
    }

    /** Sets the head pointer to branchName. */
    public static void setHeadToBranch(String branchName) {
        writeContents(headBranch, branchName);
    }

    /** Sets the head of a branch to a commit. */
    public static void setHeadOfBranch(String hash) {
        writeContents(join(BRANCH_DIR, readContentsAsString(headBranch)), hash);
    }
}
