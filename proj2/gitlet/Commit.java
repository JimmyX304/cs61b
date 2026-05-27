package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

    /** A List containing all the files updated in this commit. */
    private List<File> fileList;

    /** Blobs of the corresponding files. */
    private List<Blob> blobList;

    public Commit(String message, String par) {
        this.message = message;
        this.date = new Date();
        this.parent = par;
        if (par == null) {
            this.date = new Date(0);
        }
    }

    public void addFile(String fileName) throws IOException {
        File f = join(Repository.CWD, fileName);

        fileList.add(f);
        blobList.add(new Blob(f));
    }
}
