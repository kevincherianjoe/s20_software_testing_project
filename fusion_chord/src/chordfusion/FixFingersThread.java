package chordfusion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Heartbeat thread that corrects the finger table periodically
 */
public class FixFingersThread extends Thread {

    private ChordNode m_node;
    private Random m_random;

    //#region Constructor

    public FixFingersThread(ChordNode node) {
        m_node = node;
        m_random = new Random();
    }

    //#endregion


    //#region Thread Overrides

    @Override
    public void run() {

        // heartbeat
        while (true) {
            int i = m_random.nextInt(HashUtils.HASH_SIZE - 1) + 1;
            BigInteger iOffset = m_node.getId().add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger())
                .mod(BigDecimal.valueOf(Math.pow(2, HashUtils.HASH_SIZE)).toBigInteger());
            InetSocketAddress finger = m_node.findSuccessor(iOffset);
            m_node.setFingerSyncrhonized(i, finger);

            try {
                Thread.sleep(MessageConstants.THREAD_SLEEP_MS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //#endregion
}