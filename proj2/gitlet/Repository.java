package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
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
    public static final File HEAD_BRANCH = join(GITLET_DIR, "headBranch");


    /** Sets up persistence. */
    public static void init() throws IOException {
        GITLET_DIR.mkdir();
        STAGEADD.mkdir();
        STAGERM.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();
        HEAD_BRANCH.createNewFile();

        setHeadToBranch("master");

        addInitBranch();

        addInitCommit();
    }

    /** Adds the initial commit message. */
    public static void addInitCommit() throws IOException {
        Commit c = new Commit("initial commit", getHead());

        String fileName = sha1(serialize(c));

        File commitFile = join(COMMIT_DIR, fileName);
        writeObject(commitFile, c);

        setHeadOfBranch(fileName);
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

        File rmFile = join(STAGERM, fileName);
        if (rmFile.exists()) {
            rmFile.delete();
            return true;
        }

        File newFile = join(STAGEADD, fileName);
        File currentFile = join(CWD, fileName);

        if (newFile.exists()) {
            if (readContentsAsString(newFile).equals(readContentsAsString(currentFile))) {
                newFile.delete();
            }
        }

        Commit c = getHeadCommit();
        if (c.isTracked(fileName)) {
            if (c.getBlob(fileName).getContents().equals(readContentsAsString(currentFile))) {
                return true;
            }
        }

        newFile.createNewFile();
        writeContents(newFile, readContentsAsString(currentFile));

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

        if (!didAnyOps) {
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

        if (!filesToAdd.isEmpty()) {
            for (String fileName : filesToAdd) {
                c.addFile(fileName);
                join(STAGEADD, fileName).delete();
            }
        } else {
            if (filesToRm.isEmpty()) {
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
    public static void checkout(String commitID, String fileName) throws IOException {

        File commitFile = join(COMMIT_DIR, commitID);

        if (commitID.length() < 40) {
            for (String comID : plainFilenamesIn(COMMIT_DIR)) {
                if (comID.substring(0, commitID.length()).equals(commitID)) {
                    commitFile = join(COMMIT_DIR, comID);
                    break;
                }
            }
        }

        if (commitFile.exists()) {
            Commit c = readObject(commitFile, Commit.class);
            Blob b = c.getBlob(fileName);
            if (b != null) {
                File origFile = join(CWD, fileName);
                if (!origFile.exists()) {
                    origFile.createNewFile();
                }
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

    /** Checkouts a commit. */
    public static void checkoutCommit(String commitID) throws IOException {
        File commitFile = join(COMMIT_DIR, commitID);
        if (commitFile.exists()) {

            if (untrackedFileError(commitID)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }

            Commit c = readObject(commitFile, Commit.class);
            Map<String, String> trackedFiles = c.getTrackedFiles();
            Map<String, String> rmFiles = getHeadCommit().getTrackedFiles();

            removeFilesInDir(STAGEADD);
            removeFilesInDir(STAGERM);

            for (String fileName : rmFiles.keySet()) {
                if (!trackedFiles.containsKey(fileName)) {
                    File rmFile = join(CWD, fileName);
                    if (rmFile.exists()) {
                        rmFile.delete();
                    }
                }
            }

            for (Map.Entry<String, String> fileStored : trackedFiles.entrySet()) {
                checkout(commitID, fileStored.getKey());
            }

            writeContents(join(BRANCH_DIR, getHeadBranch()), commitID);
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    /** Checkouts the given branch. */
    public static void checkoutBranch(String branchName) throws IOException {
        File branch = join(BRANCH_DIR, branchName);
        if (branch.exists()) {
            if (!branchName.equals(getHeadBranch())) {
                Commit c = getHeadCommitOfBranch(branchName);
                Map<String, String> trackedFiles = c.getTrackedFiles();

                Map<String, String> rmFiles = getHeadCommit().getTrackedFiles();

                List<String> allFiles = plainFilenamesIn(CWD);

                for (String fileName : allFiles) {

                    boolean trackedInCurrent = rmFiles.containsKey(fileName);
                    boolean trackedInTarget = trackedFiles.containsKey(fileName);

                    if (!trackedInCurrent && trackedInTarget) {
                        System.out.println("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }

                removeFilesInDir(STAGEADD);
                removeFilesInDir(STAGERM);

                for (String fileName : rmFiles.keySet()) {
                    if (!trackedFiles.containsKey(fileName)) {
                        File rmFile = join(CWD, fileName);
                        if (rmFile.exists()) {
                            rmFile.delete();
                        }
                    }
                }

                for (Map.Entry<String, String> fileStored : trackedFiles.entrySet()) {
                    File updateFile = join(CWD, fileStored.getKey());
                    if (!updateFile.exists()) {
                        updateFile.createNewFile();
                    }
                    Blob b = readObject(join(BLOB_DIR, fileStored.getValue()), Blob.class);
                    writeContents(updateFile, b.getContents());
                }

                setHeadToBranch(branchName);
            } else {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
        } else {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }

    /** Checks if untracked files produce any errors. */
    public static boolean untrackedFileError(String commitID) {
        List<String> untrackedFiles = getUntrackedFiles();
        Commit c = readObject(join(COMMIT_DIR, commitID), Commit.class);

        for (String fileName : untrackedFiles) {
            if (c.isTracked(fileName)) {
                Blob blobID = c.getBlob(fileName);
                if (!readContentsAsString(join(CWD, fileName)).equals(blobID.getContents())) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Returns a list of untracked files in the current working directory
     * relative to the HEAD commit. */
    public static List<String> getUntrackedFiles() {
        Commit c = getHeadCommit();

        List<String> stagedAddList = plainFilenamesIn(STAGEADD);
        HashSet<String> addFiles = new HashSet<>();
        if (stagedAddList != null) {
            addFiles.addAll(stagedAddList);
        }

        List<String> stagedRemoveList = plainFilenamesIn(STAGERM);
        HashSet<String> removeFiles = new HashSet<>();
        if (stagedRemoveList != null) {
            removeFiles.addAll(stagedRemoveList);
        }

        Map<String, String> commitFiles = c.getTrackedFiles();
        List<String> allFiles = plainFilenamesIn(CWD);
        List<String> untrackedFiles = new LinkedList<>();

        if (allFiles != null) {
            for (String fileName : allFiles) {
                boolean trackedInAdd = addFiles.contains(fileName);
                boolean trackedInCommit = commitFiles.containsKey(fileName);
                boolean trackedInRemove = removeFiles.contains(fileName);

                if ((!trackedInAdd && !trackedInCommit) || trackedInRemove) {
                    untrackedFiles.add(fileName);
                }
            }
        }

        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }

    /** Gets the latest common ancestor of two branches. */
    public static String getLCA(String branch1, String branch2) {
        HashSet<String> commits = new HashSet<>();

        Queue<String> q = new ArrayDeque<>();
        q.add(getHeadOfBranch(branch1));
        while (!q.isEmpty()) {
            commits.add(q.peek());
            String ptr = readObject(join(COMMIT_DIR, q.poll()), Commit.class).getParent();
            if (!ptr.isEmpty()) {
                q.add(ptr);
            }
        }

        Queue<String> q2 = new ArrayDeque<>();
        q2.add(getHeadOfBranch(branch2));

        while (!q2.isEmpty()) {
            if (commits.contains(q.peek())) {
                return q.peek();
            }
            String nxt = readObject(join(COMMIT_DIR, q2.poll()), Commit.class).getParent();
            if (!nxt.isEmpty()) {
                q2.add(nxt);
            }
        }

        return null;
    }

    /** Merge steps 1, 6. */
    public static void mergeSteps16(String branchName, String splitName) throws IOException {
        Commit splitPointCommit = readObject(join(COMMIT_DIR, splitName), Commit.class);
        Map<String, String> currentBranchFiles = getHeadCommit().getTrackedFiles();
        Map<String, String> givenBranchFiles = getHeadCommitOfBranch(branchName).getTrackedFiles();
        Map<String, String> splitPointFiles = splitPointCommit.getTrackedFiles();

        for (Map.Entry<String, String> cur : splitPointFiles.entrySet()) {
            String fileName = cur.getKey();
            String blobID = cur.getValue();
            boolean b1 = false, b2 = false;
            if (currentBranchFiles.containsKey(fileName)) {
                if (currentBranchFiles.get(fileName).equals(blobID)) {
                    b1 = true;
                }
            }

            if (givenBranchFiles.containsKey(fileName)) {
                if (givenBranchFiles.get(fileName).equals(blobID)) {
                    b2 = true;
                }
            }

            if (b1) {
                if (givenBranchFiles.containsKey(fileName)) {
                    if (!b2) {
                        checkout(getHeadOfBranch(branchName), fileName);
                        addToStage(fileName);
                    }
                } else {
                    removeFile(fileName);
                }
            }
        }
    }

    /** Merge steps 5. */
    public static void mergeSteps5(String branchName, String splitName) throws IOException {
        Commit splitPointCommit = readObject(join(COMMIT_DIR, splitName), Commit.class);
        Map<String, String> currentBranchFiles = getHeadCommit().getTrackedFiles();
        Map<String, String> givenBranchFiles = getHeadCommitOfBranch(branchName).getTrackedFiles();
        Map<String, String> splitPointFiles = splitPointCommit.getTrackedFiles();

        for (Map.Entry<String, String> cur : givenBranchFiles.entrySet()) {
            String fileName = cur.getKey();
            boolean b1 = false, b2 = false;
            if (splitPointFiles.containsKey(fileName)) {
                b1 = true;
            }
            if (currentBranchFiles.containsKey(fileName)) {
                b2 = true;
            }
            if (!b1 && !b2) {
                checkout(getHeadOfBranch(branchName), fileName);
                addToStage(fileName);
            }
        }
    }

    /** Merge steps 8. */
    public static boolean mergeSteps8(String branchName, String splitName) throws IOException {
        Commit splitPointCommit = readObject(join(COMMIT_DIR, splitName), Commit.class);
        Map<String, String> currentBranchFiles = getHeadCommit().getTrackedFiles();
        Map<String, String> givenBranchFiles = getHeadCommitOfBranch(branchName).getTrackedFiles();
        Map<String, String> splitPointFiles = splitPointCommit.getTrackedFiles();

        List<String> filesToChange =
                getFilesForMerge(currentBranchFiles, givenBranchFiles, splitPointFiles);

        for (String fileName : filesToChange) {
            String contents1 = getContentsOfFile(fileName, currentBranchFiles);
            String contents2 = getContentsOfFile(fileName, givenBranchFiles);
            File f = join(CWD, fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            writeContents(f,
                    "<<<<<<< HEAD\n"
                            + contents1
                            + "=======\n"
                            + contents2
                            + ">>>>>>>\n");
            addToStage(fileName);
        }

        return !filesToChange.isEmpty();
    }

    /** Merges the current branch with the one given. */
    public static void mergeBranch(String branchName) throws IOException {
        checkBaseCasesForMerge(branchName);

        String splitName = getLCA(getHeadBranch(), branchName);
        checkLCA(splitName, branchName);

        mergeSteps16(branchName, splitName);
        mergeSteps5(branchName, splitName);
        boolean b = mergeSteps8(branchName, splitName);

        makeCommit("Merged " + branchName + " into " + getHeadBranch() + ".");
        if (b) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Checks LCA requirements. */
    public static void checkLCA(String splitName, String branchName) throws IOException {
        if (splitName.equals(getHeadOfBranch(branchName))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        if (splitName.equals(getHead())) {
            checkoutCommit(getHeadOfBranch(branchName));
            writeContents(join(BRANCH_DIR, getHeadBranch()), getHeadOfBranch(branchName));
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    /** Checks base cases for merge. */
    public static void checkBaseCasesForMerge(String branchName) {
        if (!join(BRANCH_DIR, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if (untrackedFileError(getHeadOfBranch(branchName))) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }

        if (!plainFilenamesIn(STAGEADD).isEmpty() || !plainFilenamesIn(STAGERM).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        if (branchName.equals(getHeadBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /** Gets files modified in different ways in current and given branches. */
    public static List<String> getFilesForMerge(
            Map<String, String> currentBranchFiles,
            Map<String, String> givenBranchFiles,
            Map<String, String> splitPointFiles) {

        List<String> validFiles = new LinkedList<>();

        Set<String> allFileNames = new TreeSet<>();
        allFileNames.addAll(currentBranchFiles.keySet());
        allFileNames.addAll(givenBranchFiles.keySet());
        allFileNames.addAll(splitPointFiles.keySet());

        for (String fileName : allFileNames) {

            String current = currentBranchFiles.get(fileName);
            String given = givenBranchFiles.get(fileName);
            String split = splitPointFiles.get(fileName);

            boolean currentModified = !Objects.equals(current, split);
            boolean givenModified = !Objects.equals(given, split);
            if (currentModified && givenModified) {
                if (!Objects.equals(current, given)) {
                    validFiles.add(fileName);
                }
            }
        }

        return validFiles;
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
        if (c.getSecondParent() != null) {
            System.out.println("Merge: " + c.getParent().substring(0, 7) + " "
                                        + c.getSecondParent().substring(0, 7));
        }
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
        // EC: fill in this function
        System.out.println("=" + "== Modifications Not Staged For Commit ===");

        Set<String> validFiles = new TreeSet<>();
        HashSet<String> stagedAddFiles = new HashSet<>(plainFilenamesIn(STAGEADD));
        HashSet<String> stagedRemoveFiles = new HashSet<>(plainFilenamesIn(STAGERM));
        Map<String, String> filesInHeadCommit = getHeadCommit().getTrackedFiles();

        for (String fileName : stagedAddFiles) {
            File f = join(CWD, fileName);
            File stageFile = join(STAGEADD, fileName);
            if (f.exists()) {
                if (!readContentsAsString(f).equals(readContentsAsString(stageFile))) {
                    validFiles.add(fileName + " (modified)");
                }
            } else {
                validFiles.add(fileName + " (deleted)");
            }
        }

        for (Map.Entry<String, String> entry : filesInHeadCommit.entrySet()) {
            String fileName = entry.getKey();
            String blobID = entry.getValue();
            Blob b = readObject(join(BLOB_DIR, blobID), Blob.class);
            File f = join(CWD, fileName);
            if (f.exists()) {
                if (!readContentsAsString(f).equals(b.getContents())) {
                    if (!stagedAddFiles.contains(fileName)) {
                        validFiles.add(fileName + " (modified)");
                    }
                }
            } else {
                if (!stagedRemoveFiles.contains(fileName)) {
                    validFiles.add(fileName + " (deleted)");
                }
            }
        }

        for (String fileName : validFiles) {
            System.out.println(fileName);
        }

        System.out.println();
    }

    /** Outputs untracked files. */
    public static void outputUntrackedFiles() {
        // EC: fill in this function
        System.out.println("=== Untracked Files ===");

        List<String> untracked = getUntrackedFiles();
        Collections.sort(untracked);

        for (String f : untracked) {
            System.out.println(f);
        }

        System.out.println();
    }

    /** Removes files in a given directory. */
    public static void removeFilesInDir(File dir) {
        List<String> files = plainFilenamesIn(dir);
        if (files != null) {
            for (String f : files) {
                join(dir, f).delete();
            }
        }
    }

    /** Gets the contents of a file. */
    public static String getContentsOfFile(String fileName, Map<String, String> mp) {
        if (mp.containsKey(fileName)) {
            return readObject(join(BLOB_DIR, mp.get(fileName)), Blob.class).getContents();
        } else {
            return "";
        }
    }

    /** Gets the current head. */
    public static String getHead() {
        return readContentsAsString(join(BRANCH_DIR, getHeadBranch()));
    }
    /** Gets the current branch. */
    public static String getHeadBranch() {
        return readContentsAsString(HEAD_BRANCH);
    }

    public static String getHeadOfBranch(String branchName) {
        return readContentsAsString(join(BRANCH_DIR, branchName));
    }

    /** Gets the head commit of a branch. */
    public static Commit getHeadCommitOfBranch(String branchName) {
        return readObject(join(COMMIT_DIR, getHeadOfBranch(branchName)), Commit.class);
    }

    /** Gets the current head commit. */
    public static Commit getHeadCommit() {
        return readObject(join(COMMIT_DIR, getHead()), Commit.class);
    }

    /** Sets the head pointer to branchName. */
    public static void setHeadToBranch(String branchName) {
        writeContents(HEAD_BRANCH, branchName);
    }

    /** Sets the head of a branch to a commit. */
    public static void setHeadOfBranch(String hash) {
        writeContents(join(BRANCH_DIR, readContentsAsString(HEAD_BRANCH)), hash);
    }
}
