package test.mockito;

import chordfusion.ChordNode;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.InetSocketAddress;

public class PingPredecessorThreadTest {

    @Mock
    private ChordNode m_chordNode;
    @Mock
    private ServerDataTable m_serverDataTable;
    @InjectMocks
    PingPredecessorThread m_pingPredecessorThread;

    @Before
    public void setup() {

        InetSocketAddress iNetSock;

        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        m_chordNode = new ChordNode(iNetSock, m_serverDataTable);
        m_pingPredecessorThread = new PingPredecessorThread(m_chordNode);
    }

    @Test
    public void pptt1() {

        InetSocketAddress iNetSockPred;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        m_chordNode.setPredecessor(iNetSockPred);
//        m_pingPredecessorThread = new PingPredecessorThread(m_chordNode);
        m_pingPredecessorThread.start();
    }

    @Test
    public void pptt2() {
        ChordNode chordNode;

        chordNode = null;
        PingPredecessorThread pingPredecessorThread = new PingPredecessorThread(chordNode);
        pingPredecessorThread.start();
    }

    @Test
    public void pptt3() {
        InetSocketAddress iNetSock;

        m_chordNode.setPredecessor(null);
//        m_pingPredecessorThread = new PingPredecessorThread(m_chordNode);
        m_pingPredecessorThread.start();
    }
}
