package test.mockito;

import chordfusion.ChordNode;
import chordfusion.MessageConstants;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.InetSocketAddress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class PingPredecessorThreadTest {

    private ChordNode m_chordNode;

    @InjectMocks
    private ServerDataTable m_serverDataTable;

    PingPredecessorThread m_pingPredecessorThread;

    @Before
    public void setup() {

        m_chordNode = mock(ChordNode.class);
        m_pingPredecessorThread = new PingPredecessorThread(m_chordNode);
    }

    @Test
    public void pptt3() {

        InetSocketAddress iNetSockPred;

        iNetSockPred = new InetSocketAddress("127.0.0.1", 1025);
        Mockito.when(m_chordNode.getPredecessor()).thenReturn(iNetSockPred);
        Mockito.when(m_chordNode.sendReceiveMessage(iNetSockPred, "ping")).thenReturn(null);
        m_pingPredecessorThread.start();
    }
}
