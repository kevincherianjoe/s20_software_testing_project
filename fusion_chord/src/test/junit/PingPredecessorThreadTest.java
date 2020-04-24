package test.junit;

import chordfusion.ChordNode;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import org.junit.Test;

import java.net.InetSocketAddress;

public class PingPredecessorThreadTest {

    @Test
    public void pptt0() {
        InetSocketAddress iNetSock;
        InetSocketAddress iNetSockPred;
        ChordNode chordNode;
        ServerDataTable serverDataTable;
        PingPredecessorThread pingPredecessorThread;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/chordfusion/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        chordNode.setPredecessor(iNetSockPred);
        pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

    @Test
    public void pptt1() {

        PingPredecessorThread pingPredecessorThread;

        ChordNode chordNode;
        chordNode = null;
        pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

    @Test
    public void pptt2() {
        InetSocketAddress iNetSock;
        ChordNode chordNode;
        ServerDataTable serverDataTable;
        PingPredecessorThread pingPredecessorThread;

//        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/chordfusion/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        chordNode.setPredecessor(null);
        pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

}
