package test.mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import chordfusion.ChordNode;
import chordfusion.HashUtils;
import chordfusion.LyricsTable;
import chordfusion.MessageConstants;
import chordfusion.MessageHandler;
import chordfusion.ServerDataTable;

@RunWith(MockitoJUnitRunner.class)
public class MessageHandlerTest {
	
	@Mock
    private Socket m_socket;
	@InjectMocks
    private LyricsTable m_lyricsTable;
	@InjectMocks
    private ServerDataTable m_serverDataTable;
	@InjectMocks
	private HashUtils hashUtils;
	@Mock
	private OutputStream outputStream;
	
	private MessageHandler messageHandler;
	
	@Before
	public void setup() throws Exception {
		
		ChordNode chordNode = new ChordNode(new InetSocketAddress("localhost", 7890), new ServerDataTable());
		messageHandler = new MessageHandler(chordNode, m_socket);
	}
	
	
	@Test
	public void testRunMethod() throws IOException {
		
		byte[] inputbytes = new String(MessageConstants.build(MessageConstants.FIND_SUCCESSOR, "34")+"\n").getBytes();
		InputStream inputStream = new ByteArrayInputStream(inputbytes);
	    System.setIn(inputStream);
	    
		Mockito.when(m_socket.getInputStream()).thenReturn(inputStream);
		Mockito.when(m_socket.getOutputStream()).thenReturn(outputStream);
			
			
		messageHandler.run();
		
		Mockito.verify(m_socket).close();
	}
	
	

}
