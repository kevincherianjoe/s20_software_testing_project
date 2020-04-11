package chordfusion;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FusionServer {
	
	public static void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("Invalid input to server " + args.toString());
            System.exit(-1);
		}
		
        String serverDetailsFilePath = args[0];
        int myID = Integer.parseInt(args[1]);
        
        ServerDataTable serverDataTable = ServerDataTable.getServerTable(serverDetailsFilePath, myID,ServerDataTable.FUSION_SERVER_TYPE);
        int myPort = serverDataTable.getMyServer().getPort(); 
        int totalNumberOfBackUps = serverDataTable.getNumberOfFusionServers();
        
        // intialize backup table
        FusedBackUpTable fusedBackUpTable = new FusedBackUpTable();
        
        // Create & start thread to handle client and other server communication
        Thread fusionThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try (ServerSocket listener = new ServerSocket(myPort)) {
                    Socket s;
                    // When a message arrives, create a thread to handle the request
                    while ((s = listener.accept()) != null) {
                        Thread t = new FusionServerThread(s, fusedBackUpTable, myID,totalNumberOfBackUps,serverDataTable);
                        t.start();
                    }
                } catch (IOException e) {
                    System.err.println("Server aborted:" + e);
                }

            }
        };
        fusionThread.start();
        
        // Create & start thread to handle client and other server communication
        FaultRecoveryAgentThread recoveryAgentThread =  new FaultRecoveryAgentThread(serverDataTable, myID,fusedBackUpTable);
        recoveryAgentThread.start();
        
        // read user input to shut down the node or view status
        Scanner userInput = new Scanner(System.in);
        while(userInput.hasNextLine()) {
            
            String command = null;
            command = userInput.nextLine();

            if (command.startsWith("exit")) {
                //node.stopAllThreads();
                System.out.println("Leaving the cluster...");
                System.exit(0);
            }
            else if (command.startsWith("status")) {
            	System.out.println(fusedBackUpTable.print());
            }
        }

	}

}
