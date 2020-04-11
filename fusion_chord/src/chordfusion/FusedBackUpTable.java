package chordfusion;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * Table for fused data and auxiliary pointer with index location at primary
 *
 */
public class FusedBackUpTable {
	
	List<byte[]> fusedData = new ArrayList<byte[]>();
	List<ClientServerFusedAuxPointer> clientServerFusedAuxPointerList = new ArrayList<ClientServerFusedAuxPointer>();
	
	public class ClientServerFusedAuxPointer {
		private int clientServerID;
		private List<FusedAuxPointer> fusedAuxPointerList;
		
		public int getClientServerID() {
			return clientServerID;
		}
		public void setClientServerID(int clientServerID) {
			this.clientServerID = clientServerID;
		}
		public synchronized void addFusedAuxPointer(int indexLocationInFusedBkp,int index) {
			if(fusedAuxPointerList == null) {
				fusedAuxPointerList = new ArrayList<FusedAuxPointer>();
			} 
			fusedAuxPointerList.add(new FusedAuxPointer(indexLocationInFusedBkp, index));
		}
		public List<FusedAuxPointer> getFusedAuxPointerList() {
			return fusedAuxPointerList;
		}
		
	}
	
	public class FusedAuxPointer {
		private int indexLocationInFusedBkp;
		private int index;
		
		public FusedAuxPointer(int indexLocationInFusedBkp,int index) {
			this.index = index;
			this.indexLocationInFusedBkp = indexLocationInFusedBkp;
		}
		
		public int getIndexLocationInFusedBkp() {
			return indexLocationInFusedBkp;
		}
		public void setIndexLocationInFusedBkp(int indexLocationInFusedBkp) {
			this.indexLocationInFusedBkp = indexLocationInFusedBkp;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		
	}
	
	public synchronized void addOrReplaceFusedData(byte[] data, int index,int clientServerID) {
		
		ClientServerFusedAuxPointer relatedClientServerFusedAuxPointer = null;
		for(ClientServerFusedAuxPointer clientServerFusedAuxPointer:clientServerFusedAuxPointerList) {
			
			if(clientServerFusedAuxPointer.getClientServerID() == clientServerID) {
				relatedClientServerFusedAuxPointer = clientServerFusedAuxPointer;
				break;
			}
		}
		
		//first value from client
		if(relatedClientServerFusedAuxPointer == null) {
			relatedClientServerFusedAuxPointer = new ClientServerFusedAuxPointer();
			relatedClientServerFusedAuxPointer.setClientServerID(clientServerID);
			clientServerFusedAuxPointerList.add(relatedClientServerFusedAuxPointer);
		}
		
		relatedClientServerFusedAuxPointer.addFusedAuxPointer(index, index);
		if(fusedData.size() > index) {
			fusedData.set(index, data); 
		} else {
			fusedData.add(index, data); 
		}
		
	}
	
	public synchronized byte[] getFusedData(int index) {
		return fusedData.get(index);
	}
	
	public synchronized List<byte[]> getFusedDataList() {
		return fusedData;
	}
	
	public synchronized List<ClientServerFusedAuxPointer> getClientServerFusedAuxPointerList (){
		return clientServerFusedAuxPointerList;
	}
	
	public synchronized List<Integer> getClientServersWithFusedDataAtAnIndex (int index){
		List<Integer> clientServersWithFusedDataAtGivenIndex = new ArrayList<Integer>();
		for (ClientServerFusedAuxPointer clientServerFusedAuxPointer : clientServerFusedAuxPointerList) {
			for(FusedAuxPointer fusedAuxPointer:clientServerFusedAuxPointer.getFusedAuxPointerList()) {
				if(fusedAuxPointer.getIndex() == index) {
					clientServersWithFusedDataAtGivenIndex.add(clientServerFusedAuxPointer.clientServerID);
					break;
				}
			}
		}
		
		return clientServersWithFusedDataAtGivenIndex;
	}
	
	public synchronized ClientServerFusedAuxPointer getClientServerFusedAuxPointer(int clientServerID) {
		for (ClientServerFusedAuxPointer clientServerFusedAuxPointer : clientServerFusedAuxPointerList) {
				if(clientServerFusedAuxPointer.getClientServerID() == clientServerID) {
					return clientServerFusedAuxPointer;
				}
			}
		
		return null;
	}
	
	/**
	 * prints fused backup table
	 * @return
	 */
	public synchronized String print() {
		StringBuilder printBuilder  = new StringBuilder();
		
		printBuilder.append("Fused Bkp Table: \n");
		
		for(byte[] data: fusedData) {
			printBuilder.append(new String(data));
			printBuilder.append("\n");
		}
		
		printBuilder.append("ClientServerFusedAuxPointerList: \n");
		for(ClientServerFusedAuxPointer clientServerFusedAuxPointer: clientServerFusedAuxPointerList) {
			printBuilder.append("Client ID : "+clientServerFusedAuxPointer.getClientServerID());
			printBuilder.append("\n");
			printBuilder.append("FusedAuxPointer : ");
			printBuilder.append("\n");
			for(FusedAuxPointer fusedAuxPointer:clientServerFusedAuxPointer.getFusedAuxPointerList()) {
				printBuilder.append("Index : "+fusedAuxPointer.getIndex());
				printBuilder.append("IndexLocationInFusedBkp : "+fusedAuxPointer.getIndexLocationInFusedBkp());
				printBuilder.append("\n");
			}
			
		}
		return printBuilder.toString();
	}
	

}
