package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/** Represents a commit that can be serialized.
 * @author Sandya Wijaya
 */
public class StagingArea implements Serializable {

    /** All tracked files. */
    private TreeMap<String, String> trackedFiles;
    /** All untracked files. */
    private ArrayList<String> untrackedFiles;

    public StagingArea() {
        trackedFiles = new TreeMap<>();
        untrackedFiles = new ArrayList<>();
    }

    /** Add a file to the staging area.
     * @param fileName name of file
     * @param sha1 hash of file */
    public void addToStaging(String fileName, String sha1) {
        trackedFiles.put(fileName, sha1);
    }

    /** Remove a file to the staging area.
     * @param fileName name of file. */
    public void removeFromStaging(String fileName) {
        untrackedFiles.add(fileName);
    }

    public void clear() {
        trackedFiles = new TreeMap<>();
    }

    public TreeMap<String, String> getStagedAddFiles() {
        return trackedFiles;
    }

    public ArrayList<String> getStagedRemoveFiles() {
        return untrackedFiles;
    }

}


