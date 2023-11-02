package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import java.io.IOException;

/**
 * The suite of all JUnit tests for the gitlet package.
 *
 * @author Sandya Wijaya
 */
public class UnitTest {

    /**
     * Run the JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * A dummy test to avoid complaint.
     */
    @Test
    public void placeholderTest() throws IOException {
        String[] args = {"init"};
        Main.main(args);
        String[] args2 = {"add", "f.txt"};
        Main.main(args2);
        String[] args3 = {"add", "g.txt"};
        Main.main(args3);
    }

}



