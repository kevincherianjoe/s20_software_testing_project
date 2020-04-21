package test.junit;

import chordfusion.ChordNode;
import chordfusion.ServerDataTable;
import chordfusion.StabilizeThread;
import org.junit.Test;

import java.net.InetSocketAddress;

public class StabilizeThreadTest {

    @Test
    public void stt0() {

        InetSocketAddress iNetSock;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

        iNetSock = new InetSocketAddress("127.0.0.1", 1025);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        for (int i = 0; i < 2; i++) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 1025 + i);
            chordNode.join(inetSocketAddress);
        }
        StabilizeThread stabilizeThread = new StabilizeThread(chordNode);
        stabilizeThread.start();
    }
}
