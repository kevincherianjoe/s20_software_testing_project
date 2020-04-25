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
        FixFingersThread fixFingersThread;

        iNetSock = new InetSocketAddress("127.0.0.1", 1025);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/chordfusion/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        fixFingersThread = new FixFingersThread(chordNode);
        fixFingersThread.start();

    }
}
