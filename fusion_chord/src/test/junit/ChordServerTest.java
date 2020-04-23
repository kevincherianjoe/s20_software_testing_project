package test.junit;

import static org.junit.Assert.*;
import org.junit.Test;

import chordfusion.ChordServer;

public class ChordServerTest {

    // test no args
    @Test(timeout = 1) public void cst0() {
        // ChordServer.main(new String[]{});

        // untestable w/ JUnit only
        assertTrue(false);
    }

    // test one arg
    @Test(timeout = 1) public void cst1() {
        // ChordServer.main(new String[]{ "test-input" });

        // untestable w/ JUnit only
        assertTrue(false);
    }

    // test two args - invalid file path
    @Test(expected = NullPointerException.class) public void cst2() {
        ChordServer.main(new String[]{ "invalid-path", "0" });
        assertTrue(false);
    }

    // test two args - invalid node id
    @Test(expected = NumberFormatException.class) public void cst3() {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        ChordServer.main(new String[]{ path, "not-an-int" });
        assertTrue(false);
    }

    // test two args - valid input
    @Test(timeout = 1) public void cst4() {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        ChordServer.main(new String[]{ path, "1" });

        // untestable w/ JUnit only
        assertTrue(false);
    }
}
