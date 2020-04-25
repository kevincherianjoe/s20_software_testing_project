package test.mockito;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import chordfusion.FaultRecoveryAgentThread;
import chordfusion.FusedBackUpTable;
import chordfusion.ServerDataTable;

@RunWith(MockitoJUnitRunner.class)
public class FaultRecoveryAgentThreadTest {
	
	private FaultRecoveryAgentThread faultRecoveryAgentThread;
	@Mock
	private ServerDataTable serverDataTable;
	private List<ServerDataTable.ServerData> serverDataList;
	
	
	@Before
	public void setup() throws Exception {
		
		serverDataTable = Mockito.spy(ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE));
		faultRecoveryAgentThread = new FaultRecoveryAgentThread(serverDataTable, 3,new FusedBackUpTable());
		serverDataList = ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE).getServerDataTable();
		
	}
	
	
	@Test (expected = RuntimeException.class)
	public void testRunMethod() {
		
		//respond with data twice and third time throw an exception  
		Mockito.when(serverDataTable.getServerDataTable()).thenReturn(serverDataList).thenReturn(serverDataList).thenThrow(new RuntimeException());
		faultRecoveryAgentThread.run();
	}
	

}
