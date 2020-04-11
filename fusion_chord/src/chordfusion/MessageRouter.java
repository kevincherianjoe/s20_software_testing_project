package chordfusion;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handle incoming messages to Chord Server process
 */
public class MessageRouter extends Thread {

    private ChordNode m_node;
    private ServerSocket m_socket;

    //#region Constructor

    public MessageRouter(ChordNode node) {
        m_node = node;
    }

    //#endregion


    //#region Thread Overrides

    @Override
    public void run() {
    	super.run();
    	
    	// find which port we are using
        int port = m_node.getMyAddress().getPort();

        // open socket
        try {
            m_socket = new ServerSocket(port,1500);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to open socket on port" + port, ex);
        }
    	
        // keep listening for incoming requests
    	Socket msgInput = null;
    	 try {
	        while ((msgInput = m_socket.accept()) != null) {
	            
	                // handle the request
	                Thread t = new MessageHandler(m_node, msgInput);
	                t.start();
	        }
    	 } catch (Exception ex) {
             System.out.println("Error accepting incoming message on socket.");
             ex.printStackTrace();
         }
    }

    //#endregion
}