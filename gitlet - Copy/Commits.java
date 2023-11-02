package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/** Represents a commit that can be serialized.
 * @author Sandya Wijaya
 */
public class Commits implements Serializable {

    /** Message of commit. */
    private String _message;

    /** Timestamp the commit was done. */
    private String _time;

    /** SHA1 of the previous commit right before it. */
    private String _parent;

    /** Treemap with key file (Object data type, represents the file changed during said commit) and value SHA1 (String data type, unique identifier of a commit) */
    private TreeMap<String, String> _files;

    /**
     * Creates a commit object with the specified parameters.
     * @param message Message of commit
     * @param parent Parent of commit to load
     * @param files Files of commit to load
     */
    public Commits(String message, String parent, TreeMap<String, String> files) {
        this._message = message;
        this._parent = parent;
        this._files = files;
        Date currently = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        this._time = formatter.format(currently);
    }

    /** Create the default commit.
     * @return*/
    public Commits() {
        this._message = "initial commit";
        this._parent = null;
        this._files = new TreeMap<String, String>();
        this._time = "Wed Dec 31 16:00:00 1969 -0800";
    }

    public String calcHash() {
        return Utils.sha1(Utils.serialize(this));
    }

    public String getParent() {
        return _parent;
    }

    public String getMessage() {
        return _message;
    }

    public String getDatetime() {
        return _time;
    }

    public TreeMap<String, String> getFiles() {
        return _files;
    }


}


