package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Represents a blob that can be serialized.
 * @author Sandya Wijaya
 */
public class Blobs implements Serializable {

    /** Name of file. **/
    private String _name;

    /** Hash of blob. **/
    private String _hash;

    /** Content of file as a byte array. **/
    private byte[] _content;

    /** Content of file as a string. **/
    private String _contentString;

    public Blobs(String fileName) {
        _name = fileName;
        try {
            File file = new File(fileName);
            _content = Utils.readContents(file);
            _contentString = Utils.readContentsAsString(file);
        } catch (IllegalArgumentException expr) {
            System.out.println(fileName);
            throw Utils.error("File does not exist.");
        }
        List<Object> contents = new ArrayList<>();
        contents.add(_name);
        contents.add(_content);
        contents.add(_contentString);
        _hash = Utils.sha1(contents);
    }
    /** Returns the name of the file. **/
    public String getName() {
        return _name;
    }
    /** Returns the hash of the blob. **/
    public String getHash() {
        return _hash;
    }

    /** Returns the content of the file as a byte array. **/
    public byte[] getContent() {
        return _content;
    }

    /** Returns content of the file as a string. **/
    public String getContentString() {
        return _contentString;
    }
}


