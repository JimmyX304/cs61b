package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

/** The Blob class stores the text of a file. It is used to store
 * the contents of a file.
 * */
public class Blob implements Serializable {

    /** The contents of a file at a point in time. */
    private String contents;

    public Blob(File f) throws IOException {
        contents = readContentsAsString(f);

        File newBlobFile = join(Repository.BLOB_DIR, sha1(contents));
        newBlobFile.createNewFile();

        writeObject(newBlobFile, this);
    }

    /** Returns the contents of the Blob */
    public String getContents() {
        return contents;
    }
}
