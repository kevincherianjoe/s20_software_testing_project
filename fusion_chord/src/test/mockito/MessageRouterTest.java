package test.mockito;

import chordfusion.ChordNode;
import chordfusion.MessageRouter;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.InetSocketAddress;

public class MessageRouterTest {

    @Mock
    private ChordNode m_chordNode;
    @Mock
    private ServerDataTable m_serverDataTable;
    @InjectMocks
    MessageRouter m_messageRouter;

    @Before
    public void setup() {

        InetSocketAddress iNetSock;

        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        m_chordNode = new ChordNode(iNetSock, m_serverDataTable);
        m_messageRouter = new MessageRouter(m_chordNode);
    }

    @Test
    public void mrt0() {

        InetSocketAddress iNetSockPred;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        m_chordNode.setPredecessor(iNetSockPred);
//        MessageRouter messageRouter = new MessageRouter(m_chordNode);
        m_messageRouter.start();

    }

    @Test
    public void mrt1() {

        m_chordNode.setPredecessor(null);
//        m_messageRouter = new MessageRouter(m_chordNode);
        m_messageRouter.start();

    }
}
