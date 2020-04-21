package test.junit;

import chordfusion.ChordNode;
import chordfusion.FixFingersThread;
import chordfusion.ServerDataTable;
import org.junit.Test;

import java.net.InetSocketAddress;

public class FixFingersThreadTest {

    @Test
    public void fftt0() {

        InetSocketAddress iNetSock;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

        iNetSock = new InetSocketAddress("127.0.0.1", 1025);
        serverDataTable = ServerDataTable.getServerTable(
                "serverTable.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        FixFingersThread fixFingersThread = new FixFingersThread(chordNode);
        fixFingersThread.start();

    }
}
