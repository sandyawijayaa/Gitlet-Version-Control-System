package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/** Represents a commit that can be serialized.
 * @author Sandya Wijaya
 */
public class StagingArea implements Serializable {

//    /** Folder that the staging area live in. */
//    static final File STAGING_FOLDER = new File(".gitlet/staging");

    private TreeMap<String, String> trackedFiles;
    private ArrayList<String> untrackedFiles;

    public StagingArea() {
        trackedFiles = new TreeMap<>();
        untrackedFiles = new ArrayList<>();
    }

    /** Add a file to the staging area. */
    public void addToTracked(String fileName, String sha1) {
        trackedFiles.put(fileName, sha1);
    }

    /** Remove a file to the staging area. */
    public void addToUntracked(String fileName) {
        untrackedFiles.add(fileName);
    }

    public void clear() {
        trackedFiles = new TreeMap<>();
    }

    public TreeMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    public ArrayList<String> getUntrackedFiles() {
        return untrackedFiles;
    }

}


