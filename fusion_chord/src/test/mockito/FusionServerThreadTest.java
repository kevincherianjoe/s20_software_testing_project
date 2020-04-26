package test.mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import chordfusion.FusedBackUpTable;
import chordfusion.FusionServerThread;
import chordfusion.MessageConstants;
import chordfusion.ServerDataTable;

@RunWith(MockitoJUnitRunner.class)
public class FusionServerThreadTest {
	
	@Mock
	private Socket clientSocket;
	@Mock
	private Socket clientSocketForGetData;
	@Mock
	private Socket clientSocketForGetAllData;
	@Mock
    private FusedBackUpTable fusedBackUpTable;
	@Mock
    private FusedBackUpTable fusedBackUpTableForGetData;
	@Mock
    private FusedBackUpTable fusedBackUpTableForGetAllData;
	
	
	@Mock
    private ServerDataTable serverDataTable;
	@Mock
	private OutputStream outputStream;
	
	private FusionServerThread fusionServerThread; 
	private FusionServerThread fusionServerThreadForGetData;
	private FusionServerThread fusionServerThreadForGetAllData;
	
	@Before
	public void setup() {
		
		serverDataTable = Mockito.spy(ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE));
		fusionServerThread = new FusionServerThread(clientSocket, fusedBackUpTable, 1, 3, serverDataTable);
		
		FusedBackUpTable fusedBackUpTableForGetDataLocal = new FusedBackUpTable();
		fusedBackUpTableForGetDataLocal.addOrReplaceFusedData("dummy".getBytes(), 0, 0);
		fusedBackUpTableForGetData = Mockito.spy(fusedBackUpTableForGetDataLocal);
		
		fusionServerThreadForGetData = new FusionServerThread(clientSocketForGetData, fusedBackUpTableForGetData, 1, 3, serverDataTable);
		
		FusedBackUpTable fusedBackUpTableForGetAllDataLocal = new FusedBackUpTable();
		fusedBackUpTableForGetAllDataLocal.addOrReplaceFusedData("dummy".getBytes(), 0, 0);
		fusedBackUpTableForGetAllData = Mockito.spy(fusedBackUpTableForGetAllDataLocal);
		
		fusionServerThreadForGetAllData = new FusionServerThread(clientSocketForGetAllData, fusedBackUpTableForGetAllData, 1, 3, serverDataTable);
		
	}
	
	@Test
	public void testRunMethodAddData() throws IOException {
		
		byte[] inputbytes = new String(MessageConstants.constructMessage(MessageConstants.ADD_DATA, "1","2","3")
										+"\n").getBytes();
		InputStream inputStream = new ByteArrayInputStream(inputbytes);
	    System.setIn(inputStream);
	    
		Mockito.when(clientSocket.getInputStream()).thenReturn(inputStream);
		Mockito.when(clientSocket.getOutputStream()).thenReturn(outputStream);
		
		fusionServerThread.run();
		
		Mockito.verify(clientSocket).close();
	}
	
	@Test
	public void testRunMethodGetData() throws IOException {
		
		byte[] inputbytes = new String(MessageConstants.constructMessage(MessageConstants.GET_DATA, "0")
										+"\n").getBytes();
		InputStream inputStream = new ByteArrayInputStream(inputbytes);
	    System.setIn(inputStream);
	    
		Mockito.when(clientSocketForGetData.getInputStream()).thenReturn(inputStream);
		Mockito.when(clientSocketForGetData.getOutputStream()).thenReturn(outputStream);
		
		fusionServerThreadForGetData.run();
		
		Mockito.verify(clientSocketForGetData).close();
	}
	
	@Test
	public void testRunMethodGetAllData() throws IOException {
		
		byte[] inputbytes = new String(MessageConstants.constructMessage(MessageConstants.GET_ALL_DATA)
										+"\n").getBytes();
		InputStream inputStream = new ByteArrayInputStream(inputbytes);
	    System.setIn(inputStream);
	    
		Mockito.when(clientSocketForGetAllData.getInputStream()).thenReturn(inputStream);
		Mockito.when(clientSocketForGetAllData.getOutputStream()).thenReturn(outputStream);
		
		fusionServerThreadForGetAllData.run();
		
		Mockito.verify(clientSocketForGetAllData).close();
	}

}
