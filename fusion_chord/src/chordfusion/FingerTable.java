package chordfusion;
import java.math.BigInteger;
import java.net.InetSocketAddress;

/**
 * Data Structure for a Chord Node finger table
 */
public class FingerTable {

    /**
     * An entry in the finger table
     */
    private class Finger {
        private BigInteger m_id;
        private InetSocketAddress m_address;

        public Finger() {
            m_id = null;
            m_address = null;
        }

        public Finger(BigInteger id, InetSocketAddress address) {
            m_id = id;
            m_address = address;
        }

        public BigInteger getId() { return m_id; }
        public void setId(BigInteger id) { m_id = id; }
        public InetSocketAddress getAddress() { return m_address; }
        public void setAddress(InetSocketAddress address) { m_address = address; }
    }

    private Finger[] m_table;

    //#region Constructor

    public FingerTable(int size) {
        m_table = new Finger[size];
    }

    //#endregion


    //#region Public Methods

    /**
     * Set finger at a specified address
     * @param index
     * @param address
     */
    public void setFinger(int index, InetSocketAddress address) {
        if (index < HashUtils.HASH_SIZE) {
            m_table[index] = new Finger(HashUtils.getAddressHash(address), address);
        }
    }

    /**
     * Clear finger at a specified address
     */
    public void clearFinger(int index) {
        if (index < HashUtils.HASH_SIZE) {
            m_table[index] = new Finger();
        }
    }

    /**
     * Clear all matching fingers
     * @param address
     */
    public void clearFingers(InetSocketAddress address) {
        for (int i = 0; i < m_table.length; i++) {
            if (m_table[i] != null && m_table[i].getAddress().equals(address)) {
                m_table[i] = new Finger();
            }
        }
    }

    /**
     * Get finger at specified index
     */
    public InetSocketAddress getFinger(int index) {
        if (index < HashUtils.HASH_SIZE && m_table[index] != null) {
            return m_table[index].getAddress();
        }
        return null;
    }

    /**
     * Get the hash value of the finger at the specified address
     * @param index
     * @return
     */
    public BigInteger getFingerId(int index) {
        if (index < HashUtils.HASH_SIZE && m_table[index] != null) {
            return m_table[index].getId();
        }
        return null;
    }

    /**
     * Debug helper to print out the finger table
     */
    public void printTable() {

        // iterate through finger table and print non-null values
        System.out.println("Finger Table:");
        for (int i = 0; i < HashUtils.HASH_SIZE; i++) {
        // for (int i = 0; i < 6; i++) {
            if (m_table[i] != null) {
                InetSocketAddress address = m_table[i].getAddress();
                BigInteger id = m_table[i].getId();
                if (address != null && id != null) {
                    System.out.println(
                        "[" + i + "] \t>> " +
                        MessageConstants.addressToString(address) +
                        " >> " + id.toString());
                }
            } else {
                System.out.println("[" + i + "] \t>> ");
            }
        }
    }

    //#endregion
}