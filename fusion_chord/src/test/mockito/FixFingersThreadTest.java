package test.mockito;

import chordfusion.ChordNode;
import chordfusion.FixFingersThread;
import chordfusion.ServerDataTable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.InetSocketAddress;

public class FixFingersThreadTest {

    @Mock
    private ChordNode m_chordNode;
    @Mock
    private ServerDataTable m_serverDataTable;
    @InjectMocks
    FixFingersThread m_fixFingersThread;

    @Before
    public void setup() {

        InetSocketAddress iNetSock;

        iNetSock = new InetSocketAddress("127.0.0.1", 1026);
        m_chordNode = new ChordNode(iNetSock, m_serverDataTable);
        m_fixFingersThread = new FixFingersThread(m_chordNode);
    }

    @Test
    public void fftt0() {

        m_fixFingersThread.start();

    }
}
