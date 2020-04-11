package test;

import org.junit.Test;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import chordfusion.FingerTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class FingerTableTest {

    private FingerTable fingTable;
    private int tableSize = 10;

    private void setupTest() {

        fingTable = new FingerTable(tableSize);
        InetSocketAddress inetSocketAddress;
        for (int i = 0; i < tableSize; i++) {
            inetSocketAddress = new InetSocketAddress("127.0.0.1", 1025 + i);
            fingTable.setFinger(i, inetSocketAddress);
        }
    }

    @Test
    public void ftt0() {

        InetSocketAddress inetSocketAddress;
        setupTest();
        fingTable.printTable();
        assertEquals(fingTable.getFinger(4), new InetSocketAddress("127.0.0.1", 1029));
        fingTable.clearFinger(4);
        assertNull(fingTable.getFinger(4));
    }

    @Test
    public void ftt1() {

        InetSocketAddress inetSocketAddress;
        setupTest();
        BigInteger fingerId = fingTable.getFingerId(0);
        assertEquals(fingerId, new BigInteger(String.valueOf(2)));
        fingTable.clearFingers(new InetSocketAddress("127.0.0.1", 1025));
    }

    @Test
    public void ftt2() {

        InetSocketAddress inetSocketAddress;
        setupTest();
        fingTable.clearFinger(9);
        fingTable.getFinger(10);
        fingTable.getFingerId(10);
    }

    @Test
    public void ftt3() {

        InetSocketAddress inetSocketAddress;
        fingTable = new FingerTable(tableSize);
        for (int i = 0; i < 4; i++) {
            inetSocketAddress = new InetSocketAddress("127.0.0.1", 1025 + i);
            fingTable.setFinger(i, inetSocketAddress);
        }
        fingTable.printTable();
    }

    @Test
    public void ftt4() {
        InetSocketAddress inetSocketAddress;
        setupTest();
        fingTable.printTable();
        assertEquals(fingTable.getFinger(4), new InetSocketAddress("127.0.0.1", 1029));
        fingTable.clearFinger(4);
        assertNull(fingTable.getFinger(4));
        BigInteger fingerId = fingTable.getFingerId(0);
        assertEquals(fingerId, new BigInteger(String.valueOf(2)));
        fingTable.clearFingers(new InetSocketAddress("127.0.0.1", 1025));
    }

    @Test
    public void ftt5(){
        InetSocketAddress inetSocketAddress;
        setupTest();
    }

}
