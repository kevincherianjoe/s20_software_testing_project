package chordfusion;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class for chord node operations 
 *
 */
public class ChordNode {

    private BigInteger m_id;
    private InetSocketAddress m_address;
    private InetSocketAddress m_predecessor;
    private FingerTable m_fingerTable;
    private LyricsTable m_lyricsTable;
    private ServerDataTable m_serverDataTable;

    private MessageRouter m_router;
    private StabilizeThread m_stabilizer;
    private FixFingersThread m_fixFingers;
    private PingPredecessorThread m_pingPredecessor;

    //#region Constructor

    public ChordNode(InetSocketAddress address,ServerDataTable serverDataTable) {
        m_address = address;
        m_id = HashUtils.getAddressHash(address);
        m_fingerTable = new FingerTable(HashUtils.HASH_SIZE);
        m_lyricsTable = new LyricsTable();
        m_serverDataTable = serverDataTable;
        m_predecessor = null;

        m_router = new MessageRouter(this);
        m_stabilizer = new StabilizeThread(this);
        m_fixFingers = new FixFingersThread(this);
        m_pingPredecessor = new PingPredecessorThread(this);
    }

    //#endregion


    //#region Getters

    public InetSocketAddress getMyAddress() {
        return m_address;
    }

    public InetSocketAddress getPredecessor() {
        return m_predecessor;
    }

    public BigInteger getId() {
        return m_id;
    }
    
    public LyricsTable getLyricsTable() {
    	return m_lyricsTable;
    }
    
    public ServerDataTable getServerDataTable() {
    	return m_serverDataTable;
    }

    //#endregion


    //#region Setters

    public synchronized void setPredecessor(InetSocketAddress predecessor) {
        m_predecessor = predecessor;
    }

    //#endregion


    //#region Public Methods

    /**
     * Display the status of the node
     */
    public void printStatus() {
        System.out.println();
        System.out.println("Running at: " + MessageConstants.addressToString(m_address));
        System.out.println("My id: " + m_id.toString());
        System.out.println("Successor is: " + MessageConstants.addressToString(getSuccessor()));
        System.out.println("Predecessor is: " + MessageConstants.addressToString(m_predecessor));
        m_fingerTable.printTable();
        System.out.println(m_lyricsTable.printTable());
    }

    /**
     * Entry the Chord ring and initialize the node
     * @param entryAddress
     * @return
     */
    public boolean join(InetSocketAddress entryAddress) {
        System.out.println("Joining ring via " + MessageConstants.addressToString(entryAddress));

        // find successor if not first node
        if (!entryAddress.equals(m_address)) {
            InetSocketAddress successor = sendReceiveAddress(entryAddress,
                MessageConstants.build(MessageConstants.FIND_SUCCESSOR, m_id.toString()));
            if (successor == null) {
                System.out.println("Failed to locate successor node when joining.");
                return false;
            }
            // set the first entry in finger table as successor
            setFinger(0, successor);
        }

        // start background threads
        m_router.start();
        m_stabilizer.start();
        m_fixFingers.start();
        m_pingPredecessor.start();

        // successful join and initialization
        return true;
    }

    /**
     * Get the successor of this node
     * @return
     */
    public InetSocketAddress getSuccessor() {
        return m_fingerTable.getFinger(0);
    }

    /**
     * Locate the successor of a key
     * @param id
     * @return
     */
    public InetSocketAddress findSuccessor(BigInteger id) {
        // System.out.println("in findSuccessor(" + id.toString() + ")");

        // initialize to own successor
        InetSocketAddress successor = getSuccessor();
        // System.out.println(" > got successor: " + MessageConstants.addressToString(successor));

        // find predecessor
        InetSocketAddress predecessor = findPredecessor(id);

        // if predecessor isn't self, get successor of predecessor
        if (!predecessor.equals(m_address)) {
            successor = sendReceiveAddress(predecessor, MessageConstants.GET_SUCCESSOR);
        }

        // final null check
        if (successor == null) {
            return m_address;
        }

        return successor;
    }

    /**
     * Find Predecessor and return the address
     * @param id
     * @return
     */
    public InetSocketAddress findPredecessor(BigInteger id) {
        // System.out.println("in findPredecessor(" + id.toString() + ")");

        InetSocketAddress currNode = m_address;
        InetSocketAddress successor = getSuccessor();
        InetSocketAddress lastSeenAlive = m_address;

        BigInteger successorRelativeId = BigInteger.ZERO;
        if (successor != null) {
            successorRelativeId = HashUtils.computeRelativeHash(HashUtils.getAddressHash(successor), m_id);
        }
        BigInteger relativeId = HashUtils.computeRelativeHash(id, m_id);

        // iterate through closest preceding fingers
        while (!(relativeId.signum() == 1 && relativeId.subtract(successorRelativeId).signum() != 0)) {

            // if at self, find closest
            if (currNode.equals(m_address)) {
                currNode = closestPrecedingFinger(id);
            }
            // otherwise at remote node
            else {
                InetSocketAddress closest = sendReceiveAddress(currNode,
                    MessageConstants.build(MessageConstants.CLOSEST_PRECEDING, id.toString()));

                // no response, set to last seen alive
                if (closest == null) {
                    currNode = lastSeenAlive;
                    successor = sendReceiveAddress(currNode, MessageConstants.GET_SUCCESSOR);
                    continue;
                }
                // closest is self
                else if (closest.equals(currNode)) {
                    return closest;
                }
                // closest is some other node
                else {
                    lastSeenAlive = currNode;
                    successor = sendReceiveAddress(closest, MessageConstants.GET_SUCCESSOR);
                    // response means move to next node
                    if (successor != null) {
                        currNode = closest;
                    }
                    // otherwise keep traversing successors
                    else {
                        successor = sendReceiveAddress(currNode, MessageConstants.GET_SUCCESSOR);
                    }
                }

                // next iteration
                successorRelativeId = HashUtils.computeRelativeHash(HashUtils.getAddressHash(successor), m_id);
                relativeId = HashUtils.computeRelativeHash(id, m_id);
            }
        }

        return currNode;
    }

    /**
     * Reset predecessor to null
     */
    public void clearPredecessor() {
        setPredecessor(null);
    }

    /**
     * Return closest finger preceding 'id'
     * @param id
     * @return
     */
    public InetSocketAddress closestPrecedingFinger(BigInteger id) {
        BigInteger relativeId = HashUtils.computeRelativeHash(id, m_id);

        // iterate through the finger table in reverse
        for (int i = HashUtils.HASH_SIZE - 1; i >= 0; i--) {
            InetSocketAddress finger = m_fingerTable.getFinger(i);
            BigInteger fingerId = m_fingerTable.getFingerId(i);

            // skip if finger is null
            if (finger == null || fingerId == null) {
                continue;
            }

            // check if closest and alive
            BigInteger fingerRelativeId = HashUtils.computeRelativeHash(fingerId, m_id);
            if (fingerRelativeId.signum() == 1 && fingerRelativeId.subtract(relativeId).signum() == -1) {
                String response = sendReceiveMessage(finger, MessageConstants.PING);

                // if alive, return
                if (response != null && response.equals(MessageConstants.PONG)) {
                    return finger;
                }
                // else remove from finger table
                else {
                    m_fingerTable.clearFingers(finger);
                }
            }
        }

        // return self if none found
        return m_address;
    }

    /**
     * Let your successor know that you should be its predecessor
     * @param successor
     * @return
     */
    public String notify(InetSocketAddress successor) {

        // check if not null and not self
        if (successor != null && !successor.equals(m_address)) {
            return sendReceiveMessage(successor, MessageConstants.build(MessageConstants.NOTIFY, MessageConstants.addressToString(m_address)));
        } else {
            return null;
        }
    }

    /**
     * Received a 'notify' message from another node that wants to be your predecessor
     * @param proposedPredecessor
     */
    public void notified(InetSocketAddress proposedPredecessor) {

        // take proposed predecessor if none already set
        if (m_predecessor == null || m_predecessor.equals(m_address)) {
            setPredecessor(proposedPredecessor);
        }
        // otherwise check if we should update
        else {
            BigInteger oldPredId = HashUtils.getAddressHash(m_predecessor);
            BigInteger oldPredRelativeId = HashUtils.computeRelativeHash(m_id, oldPredId);

            BigInteger newPredId = HashUtils.getAddressHash(proposedPredecessor);
            BigInteger newPredRelativeId = HashUtils.computeRelativeHash(newPredId, oldPredId);  

            // take proposed if closer to self
            if (newPredRelativeId.signum() == 1 && newPredRelativeId.subtract(oldPredRelativeId).signum() == -1) {
                this.setPredecessor(proposedPredecessor);
            }
        }
    }

    /**
     * 
     * @param index
     * @param value
     */
    public void setFingerSyncrhonized(int index, InetSocketAddress value) {

        synchronized(this) {
            setFinger(index, value);
        }
    }

    /**
     * Attempt to populate successors in finger table
     */
    public void populateSuccessors() {

        synchronized(this) {
            InetSocketAddress successor = getSuccessor();
            if (successor == null || successor.equals(m_address)) {
                // iterate through finger table excluding immediate successor
                for (int i = 1; i < HashUtils.HASH_SIZE; i++) {
                    InetSocketAddress finger = m_fingerTable.getFinger(i);
    
                    // check if finger is defined and not self
                    if (finger != null && !finger.equals(m_address)) {
                        // iterate back to start and update fingers
                        for (int j = i - 1; i >= 0; i--) {
                            setFinger(j, finger);
                        }
                        break;
                    }
                }
            }
    
            // if successor is still self and if a predecessor is defined and not self
            successor = getSuccessor();
            if ((successor == null || successor.equals(m_address)) && m_predecessor != null && !m_predecessor.equals(m_address)) {
                setFinger(0, m_predecessor);
            }
        }
    }

    /**
     * Delete successor and clean up associated fingers
     */
    public void deleteSuccessor() {

        synchronized(this) {

            InetSocketAddress successor = getSuccessor();
            if (successor == null) {
                return;
            }

            // find the latest instance of successor in finger table
            int i = HashUtils.HASH_SIZE - 1;
            for (i = HashUtils.HASH_SIZE - 1; i >= 0; i--) {
                InetSocketAddress finger = m_fingerTable.getFinger(i);
                if (finger != null && finger.equals(successor)) {
                    break;
                }
            }

            // delete all entries preceding the discovered instance
            for (int j = i; j >= 0; j--) {
                m_fingerTable.clearFinger(j);
            }

            // clear predecessor if it is successor
            if (m_predecessor != null && m_predecessor.equals(successor)) {
                setPredecessor(null);
            }

            // populate successors and get updated
            populateSuccessors();
            successor = getSuccessor();

            // iterate through predecessors to find successor
            if ((successor == null || successor.equals(successor)) && m_predecessor!=null && !m_predecessor.equals(m_address)) {
                InetSocketAddress predecessor = m_predecessor;
                InetSocketAddress predecessorsPredecessor = null;
                while (true) {
                    predecessorsPredecessor = sendReceiveAddress(predecessor, MessageConstants.GET_PREDECESSOR);
                    if (predecessorsPredecessor == null) {
                        break;
                    }

                    // if nothing found or back to start break
                    if (predecessorsPredecessor.equals(predecessor) || 
                        predecessorsPredecessor.equals(m_address) ||
                        predecessorsPredecessor.equals(successor)) {
                        break;
                    }

                    // keep going
                    else {
                        predecessor = predecessorsPredecessor;
                    }
                }

                setFinger(0, predecessor);
            }
        }
    }

    /**
     * Send a message to another node and get a response.
     * @param targetNode
     * @param message
     * @return response from targetNode
     */
    public String sendReceiveMessage(InetSocketAddress targetNode, String message) {

        // open a socket to the target node and send the message
        Socket socket = null;
        try {
            socket = new Socket(targetNode.getAddress(), targetNode.getPort());
            PrintStream output = new PrintStream(socket.getOutputStream());
            // System.out.println("\nSending Message: " + message);
            output.println(message);
            output.flush();
        } catch (Exception ex) {
            System.out.println("Failed to send request (" + message + ") to " + targetNode.toString());
            ex.printStackTrace();
            return null;
        }

        // read the response
        Scanner input = null;
        String response = null;
        try {
            input = new Scanner(socket.getInputStream());
            while (input.hasNext()) {
                response = input.nextLine();
                // System.out.println("\nReceived Response: " + response);
            }
        } catch (Exception ex) {
            System.out.println("Failed to read response from " + targetNode.toString());
            ex.printStackTrace();
            return null;
        }

        // try to close the socket
        try {
            socket.close();
        } catch (Exception ex) {
            System.out.println("Failed to close socket after use");
            ex.printStackTrace();
        }

        return response;
    }

    /**
     * Send a message to another node expecting an address response
     * @param targetNode
     * @param message
     * @return address from targetNode
     */
    public InetSocketAddress sendReceiveAddress(InetSocketAddress targetNode, String message) {

        try {
        	// send the message and get the response
            String response = sendReceiveMessage(targetNode, message);
            
            // parse the response as an address
            String[] msgTokens = response.split("\\" + MessageConstants.DELIM);

            // got a response
            if (msgTokens.length == 2) {
                InetSocketAddress address = MessageConstants.parseAddress(msgTokens[1]);
                if (address == null) {
                    System.out.println("Failed to parse address from response");
                    return null;
                }
                return address;
            }
            // target didn't have desired link
            else if (msgTokens.length == 1 && msgTokens[0].equals(MessageConstants.NO_VALUE)) {
                return targetNode;
            }

            // invalid response
            System.out.println("Invalid format of address in response: " + response);
            return null;
        } catch (Exception ex) {
            System.out.println("Failed to parse address from response");
            ex.printStackTrace();
            return null;
        }
    }

    //#endregion


    //#region Private Methods

    /**
     * Wrapper for 'setFinger' that handles the special case where updated
     *  entry is the successor.
     * @param index
     * @param address
     */
    private void setFinger(int index, InetSocketAddress address) {
        m_fingerTable.setFinger(index, address);

        // if successor, send notification
        if (index == 0 && !address.equals(m_address)) {
            notify(address);
        }
    }

    //#endregion
    
    //#client region
    
    /**
     * Handles messages from P2P user
     * @param inputMessage
     * @return
     */
    public String handleClientMessages(String inputMessage) {
    	String output = null;
    	inputMessage = inputMessage.trim();
        String[] splitInput = inputMessage.split("\\s+");

        // Validate that input isn't empty
        if (splitInput.length < 1) {
            System.err.println("Invalid Input to Server");
        } else {
            // Handle request from client
            if (splitInput[0].equals(MessageConstants.ADD_LYRICS) ) {
                if ((splitInput.length >= 3)) {
                	
                	BigInteger keyHash = HashUtils.getHash(splitInput[1]);
                	
                	InetSocketAddress nodeLocation = findSuccessor(keyHash);
                	
                	//combine space characters for a song lyrics
                	String[] splitInputForMessage = new String[3];
                	splitInputForMessage[0] = splitInput[0];
                	splitInputForMessage[1] = splitInput[1];
                	StringBuilder lyrics = new StringBuilder();
                	for(int i=2; i<splitInput.length; i++) {
                		lyrics.append(splitInput[i]);
                		lyrics.append(MessageConstants.SPACE);
                	}
                	splitInputForMessage[2] = lyrics.toString();
                	
					output = sendReceiveMessage(nodeLocation, MessageConstants.constructMessage(splitInputForMessage));

                } else {
                    System.err.println("Invalid Input to Server for client " + splitInput.toString());
                }
                
            } else if (splitInput[0].equals(MessageConstants.FIND_LYRICS) ){
            	
            	if ((splitInput.length == 2)) {
                	
                	BigInteger keyHash = HashUtils.getHash(splitInput[1]);
                	
                	InetSocketAddress nodeLocation = findSuccessor(keyHash);
					output = sendReceiveMessage(nodeLocation, MessageConstants.constructMessage(splitInput));

                } else {
                    System.err.println("Invalid Input to Server for client " + splitInput.toString());
                }
            	
            	
            } else {
                System.err.println("Invalid Input to Server" + splitInput.toString());
            }
        }

        return output;
    }
    //#end client region
}