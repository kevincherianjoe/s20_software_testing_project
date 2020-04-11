package chordfusion;
/**
 * DataTuple for the fused data and server id 
 *
 */
public class DataTuple {
		private byte[] data;
		private int clientServerId;
		private boolean isDataAvailable;
		
		public DataTuple(byte[] primaryData,int clientServerId) {
			this.clientServerId = clientServerId;
			this.data = primaryData;
			this.isDataAvailable = true;
		}
		
		public DataTuple(byte[] primaryData,int clientServerId,boolean isDataAvailable) {
			this.clientServerId = clientServerId;
			this.data = primaryData;
			this.isDataAvailable = isDataAvailable;
		}
		
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
		public int getClientServerId() {
			return clientServerId;
		}
		public void setClientServerId(int clientServerId) {
			this.clientServerId = clientServerId;
		}

		public boolean isDataAvailable() {
			return isDataAvailable;
		}

		public void setDataAvailable(boolean isDataAvailable) {
			this.isDataAvailable = isDataAvailable;
		} 
		
	}