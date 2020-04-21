package test.junit;

import chordfusion.ChordNode;
import chordfusion.MessageRouter;
import chordfusion.ServerDataTable;
import org.junit.Test;

import java.net.InetSocketAddress;

public class MessageRouterTest {

    @Test
    public void mrt0() {

        InetSocketAddress iNetSock;
        InetSocketAddress iNetSockPred;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        chordNode.setPredecessor(iNetSockPred);
        MessageRouter messageRouter = new MessageRouter(chordNode);
        messageRouter.start();

    }

    @Test
    public void mrt1() {

        InetSocketAddress iNetSock;
        ChordNode chordNode;
        ServerDataTable serverDataTable;

        iNetSock = new InetSocketAddress("127.0.0.1", 1025);
        serverDataTable = ServerDataTable.getServerTable(
                "fusion_chord/src/server.txt", 1, "C");
        chordNode = new ChordNode(iNetSock, serverDataTable);
        MessageRouter messageRouter = new MessageRouter(chordNode);
        messageRouter.start();

    }
}
