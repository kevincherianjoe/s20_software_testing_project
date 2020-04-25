package test.powermock;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import chordfusion.FaultRecoveryAgentThread;
import chordfusion.FusedBackUpTable;
import chordfusion.FusionServer;
import chordfusion.ServerDataTable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FusionServer.class,ServerDataTable.class,FaultRecoveryAgentThread.class})
public class FusionServerTest {
	
	
	private ServerDataTable serverDataTable;
	private ServerDataTable serverDataTable2;
	private FaultRecoveryAgentThread faultRecoveryAgentThread;
	private Scanner scanner; 
	@Mock
	private Scanner mockScanner; 
	
	//from https://stefanbirkner.github.io/system-rules/
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void setup() {
		serverDataTable = ServerDataTable.getServerTable("./src/chordfusion/server.txt", 1, ServerDataTable.FUSION_SERVER_TYPE);
		serverDataTable2 = ServerDataTable.getServerTable("./src/chordfusion/server.txt", 2, ServerDataTable.FUSION_SERVER_TYPE);
		faultRecoveryAgentThread = PowerMockito.spy(new FaultRecoveryAgentThread(serverDataTable, 1, new FusedBackUpTable()));
		scanner = new Scanner("exit\nexit");
	}
	
	@Test
	public void  testMainMethod() throws Exception{
		
		PowerMockito.mockStatic(ServerDataTable.class);
		PowerMockito.when(ServerDataTable.getServerTable(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).
									thenReturn(serverDataTable2);
		PowerMockito.whenNew(FaultRecoveryAgentThread.class).withAnyArguments().thenReturn(faultRecoveryAgentThread);
		PowerMockito.doNothing().when(faultRecoveryAgentThread,"performActionForWhenARecoveryAgent");
		
		//sleep to allow the mock scanner to be mapped.
		PowerMockito.whenNew(Scanner.class).withAnyArguments().thenAnswer(new Answer<Scanner>() {
			@Override
			public Scanner answer(InvocationOnMock arg0) throws Throwable {
				Thread.sleep(20);
				return mockScanner;
			}
		});
		
		PowerMockito.when(mockScanner.hasNextLine()).thenReturn(false);
		
		FusionServer.main(new String[] {"filelocation","4"});
		
	}
	
	@Test
	public void  testMainMethodForSystemExit() throws Exception{
		
		PowerMockito.mockStatic(ServerDataTable.class);
		PowerMockito.when(ServerDataTable.getServerTable(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).
									thenReturn(serverDataTable);
		PowerMockito.whenNew(FaultRecoveryAgentThread.class).withAnyArguments().thenReturn(faultRecoveryAgentThread);
		PowerMockito.doNothing().when(faultRecoveryAgentThread,"performActionForWhenARecoveryAgent");
		
		PowerMockito.whenNew(Scanner.class).withAnyArguments().thenReturn(scanner);
		
		exit.expectSystemExit();
		FusionServer.main(new String[] {"filelocation","4"});
		
	}
	

}
