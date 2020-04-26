package test.mockito;

import java.io.IOException;
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
	private ServerDataTable serverDataTable = new ServerDataTable();
	private List<ServerDataTable.ServerData> serverDataList;
	private List<ServerDataTable.ServerData> serverDataListForRecovery;
	private ServerDataTable serverDataTableForRecovery;
	
	@Mock
	private FaultRecoveryAgentThread faultRecoveryAgentThreadForNotRecovery;
	
	
	@Before
	public void setup() throws Exception {
		
		serverDataTable = Mockito.spy(ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE));
		FusedBackUpTable fusedBackUpTable = new FusedBackUpTable();
		fusedBackUpTable.addOrReplaceFusedData("dummy".getBytes(), 0, 2);
		faultRecoveryAgentThread = new FaultRecoveryAgentThread(serverDataTable, 3,fusedBackUpTable);
		serverDataList = ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE).getServerDataTable();
		
		serverDataTableForRecovery = ServerDataTable.getServerTable("./src/chordfusion/server.txt", 3, ServerDataTable.FUSION_SERVER_TYPE);
		serverDataTableForRecovery.updateServerStatus(2, 0);
		serverDataListForRecovery = serverDataTableForRecovery.getServerDataTable();
		
	}

	@Test (expected = RuntimeException.class)
	public void testRunMethodForRecoveryAgent() throws IOException {
		
		//respond with data twice and third time throw an exception  
		Mockito.when(serverDataTable.getServerDataTable()).thenReturn(serverDataList).thenReturn(serverDataListForRecovery).
										thenReturn(serverDataListForRecovery).thenThrow(new RuntimeException());
		
		faultRecoveryAgentThread.run();
	}
	

}
