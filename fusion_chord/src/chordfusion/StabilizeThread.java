package chordfusion;
import java.math.BigInteger;
import java.net.InetSocketAddress;

/**
 * Periodically verify node's immediate successor and notify them about your presence.
 */
public class StabilizeThread extends Thread {

    private ChordNode m_node;

    //#region Constructor

    public StabilizeThread(ChordNode node) {
        m_node = node;
    }

    //#endregion


    //#region Thread Overrides

    @Override
    public void run() {

        // periodically stabilize the node
        while (true) {

            // find successors if not yet discovered
            InetSocketAddress successor = m_node.getSuccessor();
            if (successor == null || successor.equals(m_node.getMyAddress())) {
                m_node.populateSuccessors();
            }
            successor = m_node.getSuccessor();

            // if successor is still not self
            if (successor != null && !successor.equals(m_node.getMyAddress())) {

                // attempt to get predecessor of successor
                InetSocketAddress successorPredecessor = m_node.sendReceiveAddress(successor, MessageConstants.GET_PREDECESSOR);

                // couldn't communicate w/ successor
                if (successorPredecessor == null) {
                    m_node.deleteSuccessor();
                }

                // successor's predecessor isn't itself
                else if (!successorPredecessor.equals(successor)) {
                    BigInteger selfId = HashUtils.getAddressHash(m_node.getMyAddress());
                    BigInteger successorId = HashUtils.getAddressHash(successor);
                    BigInteger successorRelativeId = HashUtils.computeRelativeHash(successorId, selfId);
                    BigInteger successorPredecessorId = HashUtils.getAddressHash(successorPredecessor);
                    BigInteger successorPredecessorRelativeId = HashUtils.computeRelativeHash(successorPredecessorId, selfId);

                    if (successorPredecessorRelativeId.signum() == 1 && successorPredecessorRelativeId.subtract(successorRelativeId).signum() == -1) {
                        m_node.setFingerSyncrhonized(0, successorPredecessor);
                    }
                }

                // is self loop
                else {
                    m_node.notify(successor);
                }
            }

            // sleep before stabilizing again
            try {
                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //#endregion
}