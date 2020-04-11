package chordfusion;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Runs a single node instance of a Chord-based file server.
 */
public class ChordServer {
	
    public static void main (String[] args) {

        // extract the port number and server list file path from the args
        if(args.length < 2) {
			System.out.println("Invalid input to server " + args.toString());
            System.exit(-1);
		}
		
        String serverDetailsFilePath = args[0];
        int myID = Integer.parseInt(args[1]);
        
        ServerDataTable serverDataTable = ServerDataTable.getServerTable(serverDetailsFilePath, myID,ServerDataTable.CHORD_SERVER_TYPE);
        int port = serverDataTable.getMyServer().getPort(); 
        // get IP address of machine
        String ipAddress = serverDataTable.getMyServer().getIpAddress();

        // instantiate a chord node
        ChordNode node = new ChordNode(new InetSocketAddress(ipAddress, port),serverDataTable);

        // determine what where we are joining
        // are we starting a new cluster? or joining an existing one?
        InetSocketAddress entryAddress = null;
        boolean isJoining = false;
        ServerDataTable.ServerData joiningServerDetails = null;
        for (ServerDataTable.ServerData server : serverDataTable.getServerDataTable()) {
            try {
            	
            	if(server.getServerType().equals(ServerDataTable.CHORD_SERVER_TYPE))
            	{
            		String responseFromOtherServer = server.getServerClient().sendReceieve(MessageConstants.PING);

            		System.out.println("responseFromOtherServer : "+ responseFromOtherServer);
                    if (MessageConstants.PONG.equals(responseFromOtherServer.trim())) {
                    	isJoining = true;
                    	joiningServerDetails = server;
                    	break;
                    }
            	}

            } catch (Exception e) {
                continue;
            }
        }

        System.out.println("isJoining:"+isJoining);
        // new cluster
        if (!isJoining) {
        	entryAddress = new InetSocketAddress(ipAddress, port);
        }
        // joining existing
        else  {
            try {
                entryAddress = new InetSocketAddress(joiningServerDetails.getIpAddress(), joiningServerDetails.getPort());
            } catch (NumberFormatException ex) {
                System.out.println("Failed to parse target port number. Exiting...");
                System.exit(0);
            }
        }

        // instantiate the node and join the ring
        boolean success = node.join(entryAddress);
        if (!success) {
            System.out.println("Failed to join the Chord ring. Exiting...");
            System.exit(0);
        }
        
        System.out.println("\nType \"status\" to get information about the node or \"exit\" to leave the cluster: ");
        
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
                node.printStatus();
            }
            // client actions 
            else {
            	System.out.println(node.handleClientMessages(command));
            }
        }
    }
}