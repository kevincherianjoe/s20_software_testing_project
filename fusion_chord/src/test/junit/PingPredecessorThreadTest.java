package test.junit;

import chordfusion.ChordNode;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import org.junit.Test;

import java.net.InetSocketAddress;

public class PingPredecessorThreadTest {

    @Test
    public void pptt1() {
        InetSocketAddress iNetSock;
        InetSocketAddress iNetSockPred;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        serverDataTable = ServerDataTable.getServerTable(
                "serverTable.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        chordNode.setPredecessor(iNetSockPred);
        PingPredecessorThread pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

    @Test
    public void pptt2() {
        InetSocketAddress iNetSock;
        InetSocketAddress iNetSockPred;
        ChordNode chordNode;
        ServerDataTable serverDataTable;
        chordNode = null;
        PingPredecessorThread pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

    @Test
    public void pptt3() {
        InetSocketAddress iNetSock;
        InetSocketAddress iNetSockPred;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

//        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        serverDataTable = ServerDataTable.getServerTable(
                "serverTable.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        chordNode.setPredecessor(null);
        PingPredecessorThread pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

}
