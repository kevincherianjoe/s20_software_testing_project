package chordfusion;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.backblaze.erasure.ReedSolomon;


/**
 * Used to setup fused back nodes to accept messages from primaries
 *
 */
public class FusionServerThread extends Thread{
	
	private Socket clientSocket;
    private FusedBackUpTable fusedBackUpTable;
    private int myID;
    private int totalNumberOfBackUps;
    private ServerDataTable serverDataTable;
	
	
	public FusionServerThread(Socket scoket,FusedBackUpTable fusedBackUpTable,int myID, int totalNumberOfBackUps, ServerDataTable serverDataTable) {
		this.clientSocket = scoket;
		this.fusedBackUpTable = fusedBackUpTable;
		this.myID = myID;
		this.totalNumberOfBackUps = totalNumberOfBackUps;
		this.serverDataTable = serverDataTable;
	}
	
	@Override
	public void run() {
		super.run();
		try {
            String inputCommand = null;

            inputCommand = socketInput();
            String output = performAction(inputCommand);
            if (!output.equals("")) {
                PrintWriter pout = new PrintWriter(clientSocket.getOutputStream());
                pout.println(output);
                pout.flush();
            }
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception Occured " + e.getMessage());
        }
		
	}
	
	// Gets the received message from the TCP connection
    private String socketInput() {
        String input = null;

        Scanner sc;
        try {
            sc = new Scanner(clientSocket.getInputStream());
            input = sc.nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }
    
    // Parses received message and performs requested operation
    private String performAction(String input) {
        String output = null;

        input = input.trim();
        String[] tokens = input.split("\\" + MessageConstants.DELIM);

        // Validate that input isn't empty
        if (tokens.length < 1) {
            System.err.println("Invalid Input to FusionServerThread");
        }else {
        	
        	// addData index value clientServerID
        	if(MessageConstants.ADD_DATA.equals(tokens[0]) && tokens.length == 4 ) {
        		
        		int index = Integer.parseInt(tokens[1]);
        		String value = MessageConstants.padInputvalue(tokens[2].trim());
        		int clientServerID = Integer.parseInt(tokens[3].trim());
        		System.out.println("Index : "+index+" $$$$$"+value+"####"+value.length()+"####"+clientServerID);
        		
        		
        		
        			List<Integer> clientServersWithFusedDataAtAnIndex = fusedBackUpTable.getClientServersWithFusedDataAtAnIndex(index);
        			System.out.println("clientServersWithFusedDataAtAnIndex" + clientServersWithFusedDataAtAnIndex.toString());
        			List<DataTuple> clientDataToBeUpdated = new ArrayList<DataTuple>();
        			clientDataToBeUpdated.add(new DataTuple(value.getBytes(),clientServerID));
        			
        				//get data from all clients and fuse with new value
        				for(ServerDataTable.ServerData serverData: serverDataTable.getServerDataTable()) {
        					
        					if(ServerDataTable.CHORD_SERVER_TYPE.equals(serverData.getServerType()) 
        							&& clientServerID != serverData.getPort() 
        							&& clientServersWithFusedDataAtAnIndex.contains(serverData.getServerProcessNumber())) {
        						try {
        							System.out.println("getting data");
									String clientData = serverData.getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.GET_DATA , (new Integer(index)).toString()));
									clientDataToBeUpdated.add(new DataTuple(MessageConstants.padInputvalue(clientData.trim()).getBytes(),serverData.getServerProcessNumber()));
								} catch (IOException e) {
									e.printStackTrace();
								}
        					}
        				}
        			//}
        			updateFusedBackUp(index, clientDataToBeUpdated, clientServerID);
        		//}
        		return "added Data";
        		
        	}
        	else if(tokens.length == 2 && tokens[0].equals(MessageConstants.GET_DATA)) {
            	return Base64.getEncoder().encodeToString(fusedBackUpTable.getFusedData(Integer.parseInt(tokens[1])));
            }
        	
        	else if(tokens.length == 1 && tokens[0].equals(MessageConstants.GET_ALL_DATA)) {
        		
        		StringBuilder printBuilder  = new StringBuilder();
        		boolean firstData = true;
        		for(byte[] data: fusedBackUpTable.getFusedDataList()) {
        			if(!firstData) {
        				printBuilder.append(MessageConstants.DELIM);
        			}else
        			{
        				firstData = false;
        			}
        			printBuilder.append(new String(data));
        		}
            	return printBuilder.toString();
            }
        	// message: ping
            else if (tokens.length == 1 && tokens[0].equals(MessageConstants.PING)) {
                return MessageConstants.PONG;
            }
        	// invalid message
            else {
                System.out.println("Received an invalid message : " + input);
                return null;
            }
        }
        
        return output;
    }

	/**
	 * @param index
	 * @param value
	 * @param clientServerID
	 * @param totalNumberOfDataAtIndex
	 * @param totalNumberOfDataPlusFusedBkps
	 */
	private void updateFusedBackUp(int index, List<DataTuple> clientDataToBeUpdated, int clientServerID) {
		
		//sort data based on server process id
		Collections.sort(clientDataToBeUpdated, new Comparator<DataTuple>() {
            public int compare(DataTuple p1, DataTuple p2) {
                return (new Integer(p1.getClientServerId())).compareTo(new Integer(p2.getClientServerId()));
            }
        });
		
		int totalNumberOfDataAtIndex = clientDataToBeUpdated.size();
		int totalNumberOfDataPlusFusedBkps = totalNumberOfBackUps + totalNumberOfDataAtIndex;
		
		byte [] [] listOfDataAtAnIndex = new byte [totalNumberOfDataPlusFusedBkps] [MessageConstants.MAX_LYRICS_LENGTH];
		
		for (int i = 0; i < totalNumberOfDataAtIndex; i++) {
			System.out.println("Client Data #"+i+" is "+clientDataToBeUpdated.get(i).getData()+"and length is "+clientDataToBeUpdated.get(i).getData().length);
			listOfDataAtAnIndex[i] = clientDataToBeUpdated.get(i).getData();
		}
		
		ReedSolomon reedSolomon = ReedSolomon.create(totalNumberOfDataAtIndex, totalNumberOfBackUps);
		reedSolomon.encodeParity(listOfDataAtAnIndex, 0, MessageConstants.MAX_LYRICS_LENGTH);
		
		byte[] myFusedData = null;
		int fusedDataIndex = 1;
		for (int i = totalNumberOfDataAtIndex; i < totalNumberOfDataPlusFusedBkps; i++) {
			if(fusedDataIndex == myID) {
				myFusedData = listOfDataAtAnIndex[i];
				System.out.println("fused data len"+myFusedData.length);
				System.out.println("fused data"+ new String(myFusedData));
				fusedBackUpTable.addOrReplaceFusedData(myFusedData, index, clientServerID);
				break;
			}
			fusedDataIndex = fusedDataIndex +1;
		}
	}
    
}
