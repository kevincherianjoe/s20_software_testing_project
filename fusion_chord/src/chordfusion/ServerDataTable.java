package chordfusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class contains list of servers provided
 *
 */
public class ServerDataTable {

    private List<ServerData> listOfServerData = new ArrayList<ServerData>();
    private int numberOfFusionServers = 0;
    private ServerData myServerData;
    public static final String CHORD_SERVER_TYPE = "C";
    public static final String FUSION_SERVER_TYPE = "F";
    
    public class ServerData {
        private ServerTCPClient serverClient;
        private String ipAddress;
        // 0 active
        // 1 not active
        private int status;
        private int port;
        private int serverProcessNumber;
        //C - Chord
        //F - Fusion Bkp
        private String serverType;

        protected ServerData(String ipAddressIP, int portIP, int serverProcessNumberIP,String serverType) {
            this.ipAddress = ipAddressIP;
            this.port = portIP;
            this.status = 1; // Initialize status as not active
            this.serverClient = new ServerTCPClient(ipAddressIP, portIP);
            this.serverProcessNumber = serverProcessNumberIP;
            this.serverType = serverType;
        }

        public ServerTCPClient getServerClient() {
            return serverClient;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getStatus() {
            return status;
        }

        private void updateStatus(int statusIP) {
            this.status = statusIP;
        }

        public int getPort() {
            return port;
        }

        public int getServerProcessNumber() {
            return serverProcessNumber;
        }
        
		public String getServerType() {
			return serverType;
		}
    }

    // add a server to the table
    public synchronized void addServer(String ipAddressIP, int portIP, int serverProcessNumberIP,String serverType) {
        listOfServerData.add(new ServerData(ipAddressIP, portIP, serverProcessNumberIP,serverType));
    }

    // updates the status of a server to active or inactive
    public synchronized void updateServerStatus(int serverProcessNumberIP, int statusIP) {
        for (ServerData serverData : listOfServerData) {
            if (serverData.getServerProcessNumber() == serverProcessNumberIP) {
                serverData.updateStatus(statusIP);
            }
        }
    }

    // returns a list of server data
    public List<ServerData> getServerDataTable() {
        return listOfServerData;
    }

    // provides the current active server count (not including self)
    public int getLiveServerCount() {
        int liveCount = 0;
        for (ServerData serverData : listOfServerData) {
            if (serverData.status == 0) {
                liveCount++;
            }
        }

        return liveCount;
    }
    
    public synchronized void updateMyServerDetails(String ipAddressIP, int portIP, int serverProcessNumberIP,String serverType) {
    	myServerData = new ServerData(ipAddressIP, portIP, serverProcessNumberIP,serverType);
    }
    
    public synchronized ServerData getMyServer() {
    	return myServerData;
    }
    
    public synchronized void incrementNumberOfFusionServers() {
    	numberOfFusionServers = numberOfFusionServers + 1;
    }
    
    public int getNumberOfFusionServers() {
    	return numberOfFusionServers;
    }
    
    // Instantiates the ServerDataTable from the specified input file
 	public static ServerDataTable getServerTable(String fileName,int myId, String myServerType) {
 		Path filePath = Paths.get(fileName);
 		ServerDataTable serverDataTable = new ServerDataTable();

 		if (Files.exists(filePath)) {
 			try {
 				BufferedReader br = Files.newBufferedReader(Paths.get(fileName));

 				// Read line by line
 				String line;
 				while ((line = br.readLine()) != null) {
 					String[] inputLineSplit = line.split("\\s+");
 					if (inputLineSplit != null && inputLineSplit.length == 4 && inputLineSplit[0] != null
 							&& !inputLineSplit[0].isEmpty() && inputLineSplit[1] != null && !inputLineSplit[1].isEmpty()
 							&& inputLineSplit[2] != null && !inputLineSplit[2].isEmpty() && !inputLineSplit[3].isEmpty()) {
 						String ipAddress = inputLineSplit[0].trim();
 						int port = Integer.parseInt(inputLineSplit[1].trim());
 						int serverId = Integer.parseInt(inputLineSplit[2].trim());
 						String serverType = inputLineSplit[3].trim();
						
 						if (serverType.equals(ServerDataTable.CHORD_SERVER_TYPE)){
							serverType = ServerDataTable.CHORD_SERVER_TYPE;
						} else {
							serverType = ServerDataTable.FUSION_SERVER_TYPE;
							serverDataTable.incrementNumberOfFusionServers();
						}
 						
 						if(myId == serverId && serverType.equals(myServerType)){
 							serverDataTable.updateMyServerDetails(ipAddress, port,serverId,serverType);
 						} else {
 							serverDataTable.addServer(ipAddress, port,serverId,serverType);
 						}
 					}
 				}

 			} catch (Exception e) {
 				e.printStackTrace();
 				System.err.println("Exception occured while reading server file");
 			}
 		} else {
 			System.err.println("no such file available:" + fileName);
 		}

 		return serverDataTable;
 	}
 	
 	public class ServerTCPClient {
 	    PrintStream pout;
 	    Scanner din;
 	    Socket server;
 	    String hostAddress;
 	    int tcpPort;

 	    // Constructor
 	    public ServerTCPClient(String hostAddress, int tcpPort) {
 	        this.hostAddress = hostAddress;
 	        this.tcpPort = tcpPort;
 	    }

 	    // Creates the TCP Socket for communication
 	    private void getSocket() throws IOException {
 	        try {
 	            server = new Socket(InetAddress.getByName(hostAddress), tcpPort);
 	            din = new Scanner(server.getInputStream());
 	            pout = new PrintStream(server.getOutputStream());
 	        } catch (IOException e) {
 	            throw e;
 	        }
 	    }

 	    // Sends message and close connection
 	    public void send(String input) throws IOException {
 	        getSocket();
 	        pout.println(input);
 	        pout.flush();

 	        try {
 	            pout.close();
 	            server.close();
 	        } catch (IOException e) {
 	            e.printStackTrace();
 	            throw e;
 	        }
 	    }

 	    // Sends message and waits for a response
 	    public String sendReceieve(String input) throws IOException {
 	        getSocket();
 	        pout.println(input);
 	        pout.flush();

 	        StringBuilder responseBuilder = new StringBuilder();
 	        while (din.hasNext()) {
 	            responseBuilder.append(din.nextLine());
 	            responseBuilder.append("\n");
 	        }
 	        try {
 	            pout.close();
 	            server.close();
 	        } catch (IOException e) {
 	            e.printStackTrace();
 	            throw e;
 	        }
 	        return responseBuilder.toString();
 	    }
 	}

}