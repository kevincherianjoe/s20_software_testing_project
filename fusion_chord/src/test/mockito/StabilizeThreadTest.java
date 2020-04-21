package test.mockito;

import chordfusion.ChordNode;
import chordfusion.ServerDataTable;
import chordfusion.StabilizeThread;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.InetSocketAddress;


public class StabilizeThreadTest {

    @Mock
    private ChordNode m_chordNode;
    @Mock
    private ServerDataTable m_serverDataTable;
    @InjectMocks
    private StabilizeThread m_stabilizeThread;

    @Before
    public void setup() {

        InetSocketAddress iNetSock;

        iNetSock = new InetSocketAddress("127.0.0.1", 1025);
        m_chordNode = new ChordNode(iNetSock, m_serverDataTable);
        m_stabilizeThread = new StabilizeThread(m_chordNode);
    }

    @Test
    public void stt0() {

//        InetSocketAddress successor = new InetSocketAddress();

//        for (int i = 0; i < 2; i++) {
//            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 1025 + i);
//            m_chordNode.join(inetSocketAddress);
//        }

//        Mockito.when(m_chordNode.getSuccessor()).thenReturn(successor);

        m_stabilizeThread.start();
    }

}
