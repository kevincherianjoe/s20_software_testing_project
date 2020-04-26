package test.mockito;

import chordfusion.ChordNode;
import chordfusion.MessageRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class MessageRouterTest {


    @Mock
    private ChordNode m_chordNode;
    @Mock
    private ServerSocket m_serverSocket;
    @Mock
    private Socket m_Socket;

    MessageRouter m_messageRouter;

    @Before
    public void setup() {

        m_chordNode = mock(ChordNode.class);
        m_messageRouter = new MessageRouter(m_chordNode);
    }

    @Test
    public void mrt2() {

        InetSocketAddress iNetSockAdd;

        iNetSockAdd = new InetSocketAddress("127.0.0.1", 8040);
        Mockito.when(m_chordNode.getMyAddress()).thenReturn(iNetSockAdd);
        try {
            m_serverSocket = new ServerSocket();
            m_serverSocket.bind(iNetSockAdd);
            m_Socket = new Socket("127.0.0.1", 8040);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_messageRouter.start();
    }

    @Test
    public void mrt3() {

        InetSocketAddress iNetSockAdd;

        iNetSockAdd = new InetSocketAddress("127.0.0.1", 8032);
        Mockito.when(m_chordNode.getMyAddress()).thenReturn(iNetSockAdd);
        MessageRouter messageRouter = new MessageRouter(m_chordNode);
        m_messageRouter.start();
    }
}
