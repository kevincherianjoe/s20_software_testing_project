package chordfusion;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Message parser and response composer
 */
public class MessageHandler extends Thread {

    private ChordNode m_node;
    private Socket m_socket;
    private LyricsTable m_lyricsTable;
    private ServerDataTable m_serverDataTable;

    //#region Constructor

    public MessageHandler(ChordNode node, Socket socket) {
        m_node = node;
        m_socket = socket;
        m_lyricsTable = node.getLyricsTable();
        m_serverDataTable = node.getServerDataTable();
    }

    //#endregion


    //#region Thread Overrides

    @Override
    public void run() {

        // read incoming message on socket
        Scanner input = null;
        PrintWriter output = null;
        try {
            input = new Scanner(m_socket.getInputStream());
            String message = input.nextLine();
            String response = handleMessage(message);
            
            // write the response back to sender
            if (response != null) {
                output = new PrintWriter(m_socket.getOutputStream());
                output.println(response);
                output.flush();
            }

            // close socket
            m_socket.close();
        } catch (Exception ex) {
            System.out.println("Failed to receive message from socket.");
            ex.printStackTrace();
        }
    }

    //#endregion


    //#region Private Methods

    /**
     * Helper method that parsed and handles all the message types
     * @param message
     * @return
     */
    private String handleMessage(String message) {

        // handle null
        if (message == null) {
            return null;
        }
        
        // tokenize the message
        String[] tokens = message.split("\\" + MessageConstants.DELIM);

        // message: find-successor
        if (tokens.length == 2 && tokens[0].equals(MessageConstants.FIND_SUCCESSOR)) {
            BigInteger id = new BigInteger(tokens[1]);
            InetSocketAddress address = m_node.findSuccessor(id);
            return MessageConstants.build(MessageConstants.FOUND_SUCCESSOR,
                MessageConstants.addressToString(address));
        }
        // message: get-successor
        else if (tokens.length == 1 && tokens[0].equals(MessageConstants.GET_SUCCESSOR)) {
            InetSocketAddress address = m_node.getSuccessor();
            if (address != null) {
                return MessageConstants.build(MessageConstants.MY_SUCCESSOR,
                    MessageConstants.addressToString(address));
            } else {
                return MessageConstants.NO_VALUE;
            }
        }
        // message: get-predecessor
        else if (tokens.length == 1 && tokens[0].equals(MessageConstants.GET_PREDECESSOR)) {
            InetSocketAddress address = m_node.getPredecessor();
            if (address != null) {
                return MessageConstants.build(MessageConstants.MY_PREDECESSOR,
                    MessageConstants.addressToString(address));
            } else {
                return MessageConstants.NO_VALUE;
            }
        }
        // message: ping
        else if (tokens.length == 1 && tokens[0].equals(MessageConstants.PING)) {
            return MessageConstants.PONG;
        }
        // message: closest-preceding
        else if (tokens.length == 2 && tokens[0].equals(MessageConstants.CLOSEST_PRECEDING)) {
            BigInteger id = new BigInteger(tokens[1]);
            InetSocketAddress address = m_node.closestPrecedingFinger(id);
            return MessageConstants.build(MessageConstants.MY_CLOSEST_PRECEDING,
                MessageConstants.addressToString(address));
        }
        // message: notify
        else if (tokens.length == 2 && tokens[0].equals(MessageConstants.NOTIFY)) {
            InetSocketAddress address = MessageConstants.parseAddress(tokens[1]);
            if (address != null) {
                m_node.notified(address);
            }
            return MessageConstants.NOTIFY_ACK;
        }
        // message: addLyrics
        else if (tokens.length == 3 && tokens[0].equals(MessageConstants.ADD_LYRICS)) {
        	return addLyrics(tokens);
        }
        // message: findLyrics
        else if (tokens.length == 2 && tokens[0].equals(MessageConstants.FIND_LYRICS)) {
            
            return m_lyricsTable.getLyrics(tokens[1]);
        }
        // message: get-data
        else if(tokens.length == 2 && tokens[0].equals(MessageConstants.GET_DATA)) {
        	return m_lyricsTable.getDataAtIndex(Integer.parseInt(tokens[1]));
        }
        // message: find-all
        else if (tokens.length == 1 && tokens[0].equals(MessageConstants.FIND_ALL_LYRICS)) {
            return m_lyricsTable.getAllLyrics();
        }
        // invalid message
        else {
            System.out.println("Received an invalid message : " + message);
            return null;
        }
    }

	/**
     * Handle addition of lyrics into the Chord ring
	 * @param tokens
	 * @return
	 */
	private String addLyrics(String[] tokens) {
		Integer index = m_lyricsTable.getLyricsDataList().size();
		
		m_lyricsTable.addLyrics(index,tokens[1], tokens[2]);
		//send data to all fusion backups
		// addData index value clientServerID
		String combinedSongAndLyrics = tokens[1]+ MessageConstants.VALUE_DELIM +tokens[2];
		Integer myServerID = m_serverDataTable.getMyServer().getServerProcessNumber();
		for(ServerDataTable.ServerData server : m_serverDataTable.getServerDataTable()) {
			try {
		    	
		    	if(server.getServerType().equals(ServerDataTable.FUSION_SERVER_TYPE))
		    	{
                    server.getServerClient()
                        .sendReceieve(
                            MessageConstants.constructMessage(
                                MessageConstants.ADD_DATA, index.toString(),combinedSongAndLyrics,myServerID.toString()));
		    	}
		    	
		    } catch (Exception e) {
		    	//must be a non active fusion server
		        continue;
		    }
		}
		return "lyrics uploaded successfuly for song - "+ tokens[1];
	}

    //#endregion
}