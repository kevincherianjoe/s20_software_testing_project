package test.junit;

import chordfusion.ChordServer;
import org.junit.Test;

public class ChordServerTest {

    @Test
    public void cst0() {
        ChordServer.main(new String[]{"No Agruements"});
    }

    @Test
    public void cst1() {
//        ChordServer.main(new String[]{"serverTable.txt", "1"});
    }
}
