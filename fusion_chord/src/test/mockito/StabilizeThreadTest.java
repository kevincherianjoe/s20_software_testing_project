package test.mockito;

import chordfusion.ChordNode;
import chordfusion.MessageConstants;
import chordfusion.ServerDataTable;
import chordfusion.StabilizeThread;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class StabilizeThreadTest {

    @Mock
    private ChordNode m_chordNode;
    @InjectMocks
    private ServerDataTable m_serverDataTable;

    private StabilizeThread m_stabilizeThread;

    @Before
    public void setup() {

        m_chordNode = mock(ChordNode.class);
        m_stabilizeThread = new StabilizeThread(m_chordNode);
    }

    @Test
    public void stt1() {

        InetSocketAddress iNetSockSucc;

        iNetSockSucc = new InetSocketAddress("127.0.0.1", 1027);
        Mockito.when(m_chordNode.getSuccessor()).thenReturn(iNetSockSucc);
        m_stabilizeThread.start();
    }

    @Test
    public void stt2() {
        //catches else & line 62

        InetSocketAddress iNetSockSucc;

        iNetSockSucc = new InetSocketAddress("127.0.0.1", 1028);
        Mockito.when(m_chordNode.getSuccessor()).thenReturn(iNetSockSucc);
        Mockito.when(m_chordNode.sendReceiveAddress(iNetSockSucc, MessageConstants.GET_PREDECESSOR)).thenReturn(iNetSockSucc);
        m_stabilizeThread.start();
    }

    @Test
    public void stt3() {

        InetSocketAddress iNetSockPred;
        InetSocketAddress inetSocketCurr;
        InetSocketAddress iNetSockSucc;

        iNetSockSucc = new InetSocketAddress("127.0.0.1", 8030);
        Mockito.when(m_chordNode.getSuccessor()).thenReturn(iNetSockSucc);
        iNetSockPred = new InetSocketAddress("127.0.0.1", 8031);
        inetSocketCurr = new InetSocketAddress("127.0.0.1", 8040);
        Mockito.when(m_chordNode.sendReceiveAddress(iNetSockSucc, MessageConstants.GET_PREDECESSOR)).thenReturn(iNetSockPred);
        Mockito.when(m_chordNode.getMyAddress()).thenReturn(inetSocketCurr);
        m_stabilizeThread.start();
//        m_stabilizeThread.run();
    }
}
