package chordfusion;
import java.net.InetSocketAddress;

/**
 * Send heartbeat to predecessor
 */
public class PingPredecessorThread extends Thread {

    private ChordNode m_node;

    //#region Constructor

    public PingPredecessorThread(ChordNode node) {
        m_node = node;
    }

    //#endregion


    //#region Thread Overrides

    @Override
    public void run() {

        // heartbeat
        while (true) {
            try {
                InetSocketAddress predecessor = m_node.getPredecessor();
                if (predecessor != null) {
                    String response = m_node.sendReceiveMessage(predecessor, MessageConstants.PING);
                    if (response == null || !response.equals(MessageConstants.PONG)) {
                        m_node.clearPredecessor();
                    }
                }
                // sleep before checking again
                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //#endregion
}