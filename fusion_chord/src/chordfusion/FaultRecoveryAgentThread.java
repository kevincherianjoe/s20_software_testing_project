package chordfusion;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backblaze.erasure.ReedSolomon;

/**
 * Used to monitor all P2P clients and initiate a recovery when a fault happens.
 *
 */
public class FaultRecoveryAgentThread extends Thread{
	
	 private ServerDataTable serverDataTable;
	 private int myID;
	 private FusedBackUpTable fusedBackUpTable;
	 
	 public FaultRecoveryAgentThread(ServerDataTable serverDataTable,int myID, FusedBackUpTable fusedBackUpTable ) {
		this.serverDataTable = serverDataTable;
		this.myID = myID;
		this.fusedBackUpTable = fusedBackUpTable;
	}
	
	@Override
	public void run() {
		super.run();
		
		boolean isRecoveryAgent = isRecoveryAgent();
        
        if(isRecoveryAgent) {
        	performActionForWhenARecoveryAgent();
        } else {
        	perfromActionsForWhenNotARecoveryAgent();
        }
		
	}

	/**
	 * Check if a fusion needs to be a recovery agent
	 * @return
	 */
	private boolean isRecoveryAgent() {
		//check if I am the active Recovery Agent
		boolean isRecoveryAgent = false;
		boolean isRecoveryAgentFound = false;
		
        for (ServerDataTable.ServerData server : serverDataTable.getServerDataTable()) {
            try {
            	
        		String responseFromOtherServer = server.getServerClient().sendReceieve(MessageConstants.PING);

                if (MessageConstants.PONG.equals(responseFromOtherServer.trim())) {
                	serverDataTable.updateServerStatus(server.getServerProcessNumber(), 0);
                	//fusion server with the least process id, becomes the default recovery agent
                	if(server.getServerType().equals(ServerDataTable.FUSION_SERVER_TYPE ) && myID > server.getServerProcessNumber()) {
                		isRecoveryAgentFound = true;
                	}
                } 

            } catch (Exception e) {
                continue;
            }
        }
        
        //none found so its me
        if(!isRecoveryAgentFound) {
        	isRecoveryAgent = true;
        }
		return isRecoveryAgent;
	}
	
	/**
	 * Keep checking for current recovery agent status and take up the recovery role if needed
	 */
	private void perfromActionsForWhenNotARecoveryAgent() {
		//check the other recovery agent status
		System.out.println("I am not a RecoveryAgent");
		while(!isRecoveryAgent()) {
			try {
                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
		}
		performActionForWhenARecoveryAgent();
	}
	
	/**
	 * Ping Pong all chord and fusion server and start recovery process 
	 */
	private void performActionForWhenARecoveryAgent() {
		 //remain recovery agent until death
		System.out.println("I am a RecoveryAgent");
		while(true) {
			
			for (ServerDataTable.ServerData server : serverDataTable.getServerDataTable()) {
	           
            	int currentStatus = server.getStatus();
            	
            	if(currentStatus == 0) {
            		 try {
            			 server.getServerClient().sendReceieve(MessageConstants.PING);
            		 } catch (Exception e) {
     	            	recoverLostData(server);
     	            	serverDataTable.updateServerStatus(server.getServerProcessNumber(), 1);
     	                continue;
     	            }
            	} else {
            		
            		try {
            			 server.getServerClient().sendReceieve(MessageConstants.PING);
            			 serverDataTable.updateServerStatus(server.getServerProcessNumber(), 0);
            		 } catch (Exception e) {
     	            	
     	                continue;
     	            }
            		
            	}
	        }
			
			try {
                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
			
		}
	}
	
	/**
	 * Kick off recovery process for the lost server
	 * @param lostServer
	 */
	private void recoverLostData(ServerDataTable.ServerData lostServer) {
		
		//TODO remove
		System.out.println("A server is lost");
		if(ServerDataTable.CHORD_SERVER_TYPE.equals(lostServer.getServerType())) {
				FusedBackUpTable.ClientServerFusedAuxPointer clientServerAuxPointer= fusedBackUpTable.getClientServerFusedAuxPointer(lostServer.getServerProcessNumber());
				if (clientServerAuxPointer == null) {
					//nothing to recover
					
				} else { 
					//recover data and send to next active successor in chord 
					List<FusedBackUpTable.FusedAuxPointer> auxPonintersOfAClient = clientServerAuxPointer.getFusedAuxPointerList();
					Map<Integer,String> recoveredDataAtIndex = new HashMap<Integer,String>();
					ServerDataTable.ServerData stableChordServer = null;
					//Step 1 - recover data
					//==================================================
						for(FusedBackUpTable.FusedAuxPointer fusedAuxPointer:auxPonintersOfAClient) {
							Integer indexToRecover = fusedAuxPointer.getIndex();
							List<DataTuple> primaryDataAtIndex = new ArrayList<DataTuple>();
							List<DataTuple> fusedDataAtIndex = new ArrayList<DataTuple>();
							List<DataTuple> combinedDataAtIndex = new ArrayList<DataTuple>();
							DataTuple aPrimaryDataToCleanUpFusedData = null;
							
							//add place holder for lost data
							primaryDataAtIndex.add(new DataTuple(new byte[MessageConstants.MAX_LYRICS_LENGTH], lostServer.getServerProcessNumber(),false));
							
							List<Integer> clientServersWithFusedDataAtAnIndex = fusedBackUpTable.getClientServersWithFusedDataAtAnIndex(indexToRecover);
							
							//add my data
							fusedDataAtIndex.add(new DataTuple(fusedBackUpTable.getFusedData(indexToRecover),serverDataTable.getMyServer().getServerProcessNumber()));
							
							//get data from all others
	        				for(ServerDataTable.ServerData serverData: serverDataTable.getServerDataTable()) {
	        					try {
		        					
	        						if(ServerDataTable.FUSION_SERVER_TYPE.equals(serverData.getServerType())
		        							&& serverData.getStatus() == 0) {
	        								String response = serverData.getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.GET_DATA, indexToRecover.toString()));
	        								response = new String(Base64.getDecoder().decode(response.trim().getBytes()));
	        								fusedDataAtIndex.add(new DataTuple(MessageConstants.padInputvalue(response.trim()).getBytes(),serverData.getServerProcessNumber()));
		        						
		        					} else if(serverData.getServerProcessNumber() != lostServer.getServerProcessNumber()
		        								&& clientServersWithFusedDataAtAnIndex.contains(serverData.getServerProcessNumber())) {
											String clientData = serverData.getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.GET_DATA, indexToRecover.toString()));
											System.out.println("data from primary"+clientData.trim());
											String clientDataPadded = MessageConstants.padInputvalue(clientData.trim());
											DataTuple data = new DataTuple(clientDataPadded.getBytes(),serverData.getServerProcessNumber());
											primaryDataAtIndex.add(data);
											if(aPrimaryDataToCleanUpFusedData == null) {
												aPrimaryDataToCleanUpFusedData = data;
											}
											if(stableChordServer == null) {
												stableChordServer = serverData;
											}
		        					}
	        					} catch (IOException e) {
									e.printStackTrace();
								}
	        				}
	        				
	        				
	        				//decode and recover data
	        				
	        				//sort data based on server process id
	        				Collections.sort(primaryDataAtIndex, new Comparator<DataTuple>() {
	        		            public int compare(DataTuple p1, DataTuple p2) {
	        		                return (new Integer(p1.getClientServerId())).compareTo(new Integer(p2.getClientServerId()));
	        		            }
	        		        });
	        				
	        				Collections.sort(fusedDataAtIndex, new Comparator<DataTuple>() {
	        		            public int compare(DataTuple p1, DataTuple p2) {
	        		                return (new Integer(p1.getClientServerId())).compareTo(new Integer(p2.getClientServerId()));
	        		            }
	        		        });
	        				combinedDataAtIndex.addAll(primaryDataAtIndex);
	        				combinedDataAtIndex.addAll(fusedDataAtIndex);
	        				
	        				int totalNumberOfDataAtIndex = primaryDataAtIndex.size();
	        				int totalNumberOfFusedDataAtIndex = fusedDataAtIndex.size();
	        				int totalNumberOfDataPlusFusedBkps = totalNumberOfFusedDataAtIndex + totalNumberOfDataAtIndex;
	        				
	        				byte [] [] listOfDataAtAnIndex = new byte [totalNumberOfDataPlusFusedBkps] [MessageConstants.MAX_LYRICS_LENGTH];
	        				boolean [] isDataPresentAtIndex = new boolean [totalNumberOfDataPlusFusedBkps];
	        				int locationOfLostData = 0;
	        				int locationOfDataForFuseCleanUp = 0;
	        				
	        				for (int i = 0; i < totalNumberOfDataPlusFusedBkps; i++) {
	        					System.out.println("Recover Data List - Index - "+i+ " = "+new String(combinedDataAtIndex.get(i).getData()));
	        					System.out.println("Recover Data Length List - Index - "+i+ " = "+combinedDataAtIndex.get(i).getData().length);
	        					listOfDataAtAnIndex[i] = combinedDataAtIndex.get(i).getData();
	        					if(combinedDataAtIndex.get(i).isDataAvailable()) {
	        						isDataPresentAtIndex[i] = true;
	        						if(aPrimaryDataToCleanUpFusedData != null
	        								&& aPrimaryDataToCleanUpFusedData.getClientServerId() == combinedDataAtIndex.get(i).getClientServerId()
	        								&& locationOfDataForFuseCleanUp != 0) {
	        							locationOfDataForFuseCleanUp = i;
	        						}
	        					} else {
	        						isDataPresentAtIndex[i] = false;
	        						locationOfLostData= i;
	        					}
	        				}
	        				
	        				ReedSolomon reedSolomon = ReedSolomon.create(totalNumberOfDataAtIndex, totalNumberOfFusedDataAtIndex);
	        				reedSolomon.decodeMissing(listOfDataAtAnIndex, isDataPresentAtIndex, 0, MessageConstants.MAX_LYRICS_LENGTH);
	        				
	        				//add to recovered data list
	        				String recoveredData = new String(listOfDataAtAnIndex[locationOfLostData]);
	        				System.out.println("recovered data"+recoveredData);
	        				recoveredDataAtIndex.put(indexToRecover, recoveredData.trim());
	        				
	        				//Step 2 - clean up fused data
	        				//===========================================================================================
	        				//if there no primary data then the fused data is replica of the primary and will be cleaned up in the next step
	        				if(aPrimaryDataToCleanUpFusedData != null) {
	        					
	        					//clean up my data
	        					try {
									serverDataTable.getMyServer().getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.ADD_DATA, 
											(new Integer(indexToRecover)).toString(),
											new String(listOfDataAtAnIndex[locationOfDataForFuseCleanUp]).trim(),
											(new Integer(aPrimaryDataToCleanUpFusedData.getClientServerId())).toString()));
								} catch (IOException e1) {
									e1.printStackTrace();
								}
	        					
	        					for(ServerDataTable.ServerData server : serverDataTable.getServerDataTable()) {
		        	    			try {
		        	                	
		        	                	if(server.getServerType().equals(ServerDataTable.FUSION_SERVER_TYPE))
		        	                	{
		        	                		server.getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.ADD_DATA, 
		        	                				(new Integer(indexToRecover)).toString(),
		        	                				new String(listOfDataAtAnIndex[locationOfDataForFuseCleanUp]).trim(),
		        	                				(new Integer(aPrimaryDataToCleanUpFusedData.getClientServerId())).toString()));
		        	                	}
		        	                	
		        	                } catch (Exception e) {
		        	                	//must be a non active fusion server
		        	                    continue;
		        	                }
		        	        	}
	        				}
	        				
						}
						
						//Step 3 - add all the recovered data to successor node
						//==========================================================================
						for(Integer index:recoveredDataAtIndex.keySet()) {
							
							addRecoveredDataToSuccessorNode(recoveredDataAtIndex, stableChordServer, index,lostServer);
							
						}
				}
		} else {
			
			//TODO recover a fusion server
			
		}
		
	}

	/**
	 * Add the recovered data to the respective chord successor nodes
	 * @param recoveredDataAtIndex
	 * @param stableChordServer
	 * @param index
	 * @param lostServer
	 */
	private void addRecoveredDataToSuccessorNode(Map<Integer, String> recoveredDataAtIndex,
			ServerDataTable.ServerData stableChordServer, Integer index,ServerDataTable.ServerData lostServer) {
		String data = recoveredDataAtIndex.get(index); 
		BigInteger keyHash = HashUtils.getHash(data);
		String successorAddress;
		try {
			successorAddress = stableChordServer.getServerClient().sendReceieve(MessageConstants.build(MessageConstants.FIND_SUCCESSOR, keyHash.toString()));
			String[] msgTokens = successorAddress.trim().split("\\" + MessageConstants.DELIM);
			String[] addressToken = msgTokens[1].split(MessageConstants.IP_DELIM);
			String address = addressToken[0];
		    int port = Integer.parseInt(addressToken[1]);
		    //keep trying until we get a good successor 
		    if(port == lostServer.getPort()) {
		    	try {
	                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
		    	addRecoveredDataToSuccessorNode(recoveredDataAtIndex, stableChordServer, index,lostServer);
		    } else {
		    	 for(ServerDataTable.ServerData server : serverDataTable.getServerDataTable()) {
				    	if(address.equals(server.getIpAddress())&& port == server.getPort()) {
				    		String[] songAndLyricsData = data.split(MessageConstants.VALUE_DELIM);
				    		server.getServerClient().sendReceieve(MessageConstants.constructMessage(MessageConstants.ADD_LYRICS ,songAndLyricsData[0] 
				    															, songAndLyricsData[1]));
				    		break;
				    	}
				    }
		    }
		   
         
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to store recovered data, could not find successor node"+e.getMessage());
		}
	}
	
	
}
