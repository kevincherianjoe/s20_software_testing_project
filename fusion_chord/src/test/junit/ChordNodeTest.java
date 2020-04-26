package test.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;

import static org.junit.Assert.*;
import org.junit.Test;

import chordfusion.ChordNode;
import chordfusion.HashUtils;
import chordfusion.ServerDataTable;

public class ChordNodeTest {

    // test getters and setters
    @Test
    public void cnt01() {
        InetSocketAddress isa = new InetSocketAddress("addr", 10);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(isa, cn.getMyAddress());
        assertEquals(null, cn.getPredecessor());
        assertEquals(HashUtils.getAddressHash(isa), cn.getId());
        assertEquals(0, cn.getLyricsTable().getLyricsDataList().size());
        assertEquals(sdt, cn.getServerDataTable());
        cn.setPredecessor(isa);
        assertEquals(isa, cn.getPredecessor());
    }

    // test printStatus of fresh ChordNode
    @Test(expected = NullPointerException.class)
    public void cnt02() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa = new InetSocketAddress("addr", 10);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        cn.printStatus();

        assertTrue(false);
    }

    // test printStatus of fresh ChordNode
    @Test
    public void cnt03() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        cn.printStatus();

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected =
            "\n" +
            "Running at: 127.0.0.1:3001\n" +
            "My id: 6\n" +
            "Successor is: null\n" +
            "Predecessor is: null\n" +
            "Finger Table:\n" +
            "[0] 	>> \n" +
            "[1] 	>> \n" +
            "[2] 	>> \n" +
            "[3] 	>> \n" +
            "[4] 	>> \n" +
            "[5] 	>> \n" +
            "[6] 	>> \n" +
            "[7] 	>> \n" +
            "\n" +
            " My Lyrics Table: \n" +
            "\n" +
            " My Data AuxPointers: \n" +
            "\n";
        assertEquals(expected, allWrittenLines);
    }

    // test join as first node
    @Test
    public void cnt04() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertTrue(cn.join(isa));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected =
            "Joining ring via 127.0.0.1:3001\n";
        assertEquals(expected, allWrittenLines);
    }

    // test join NOT as first node
    // cannot fully test w/ only JUnit
    @Test
    public void cnt05() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress firstIsa = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress otherIsa = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(firstIsa, sdt);
        assertFalse(cn.join(otherIsa));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected =
            "Joining ring via 127.0.0.1:3002\n";
        assertTrue(allWrittenLines.contains(expected));
    }

    // test getSuccessor w/ null successor
    @Test
    public void cnt06() {
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(null, cn.getSuccessor());
    }

    // test findSuccessor
    @Test
    public void cnt07() {
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(isa, cn.findSuccessor(new BigInteger("0")));
    }

    // test findPredecessor w/ no setup
    @Test
    public void cnt08() {
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(isa, cn.findPredecessor(new BigInteger("0")));
    }

    // test clearPredecessor
    @Test
    public void cnt09() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setPredecessor(isa02);
        assertEquals(isa02, cn.getPredecessor());
        cn.clearPredecessor();
        assertEquals(null, cn.getPredecessor());
    }

    // test closestPrecedingFinger
    @Test
    public void cnt10() {
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(isa, cn.closestPrecedingFinger(new BigInteger("0")));
    }

    // test notify null successor
    @Test
    public void cnt11() {
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertEquals(null, cn.notify(null));
    }

    // test notify non-null success & not self
    @Test
    public void cnt12() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.notify(isa02));
    }

    // test notify non-null success & is self
    @Test
    public void cnt13() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.notify(isa01));
    }

    // test notified w/ null predecessor
    @Test
    public void cnt14() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.notified(isa02);
        assertEquals(isa02, cn.getPredecessor());
    }

    // test notified w/ self predecessor
    @Test
    public void cnt15() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setPredecessor(isa01);
        cn.notified(isa02);
        assertEquals(isa02, cn.getPredecessor());
    }

    // test notified w/ not null predecessor or self predecessor
    // & proposed not closer to self
    @Test
    public void cnt16() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setPredecessor(isa02);
        cn.notified(isa03);
        assertEquals(isa02, cn.getPredecessor());
    }

    // test notified w/ not null predecessor or self predecessor
    // & proposed closer to self
    @Test
    public void cnt17() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3008);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setPredecessor(isa02);
        cn.notified(isa03);
        assertEquals(isa03, cn.getPredecessor());
    }

    // test setFingerSynchronized
    @Test
    public void cnt18() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa02);
        assertEquals(isa02, cn.getSuccessor());
    }

    // test setFingerSynchronized & don't notify - not self
    @Test
    public void cnt19() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        assertEquals(isa01, cn.getSuccessor());
    }

    // test setFingerSynchronized & don't notify - not 0 index
    @Test
    public void cnt20() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(1, isa01);
        assertEquals(null, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor null
    @Test
    public void cnt21() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.populateSuccessors();
        assertEquals(null, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor is self
    @Test
    public void cnt22() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.populateSuccessors();
        assertEquals(isa01, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor null
    // & second finger pre-assigned
    // BUG in inner for-loop
    @Test
    public void cnt23() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(2, isa02);
        cn.populateSuccessors();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor null
    // & first finger pre-assigned
    @Test
    public void cnt24() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(1, isa02);
        cn.populateSuccessors();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor null
    // & predecessor not null
    // & predecessor not self
    @Test
    public void cnt25() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setPredecessor(isa02);
        cn.populateSuccessors();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test populateSuccessors w/ successor self
    // & predecessor not null
    // & predecessor not self
    @Test
    public void cnt26() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.setPredecessor(isa02);
        cn.populateSuccessors();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test deleteSuccessor w/ null successor
    @Test
    public void cnt27() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.deleteSuccessor();
        assertEquals(null, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & no predecessor
    @Test
    public void cnt28() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.deleteSuccessor();
        assertEquals(null, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor is successor
    @Test
    public void cnt29() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.setPredecessor(isa01);
        cn.deleteSuccessor();
        assertEquals(null, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor not null and not successor
    // & predecessorsPredecessor is null
    @Test
    public void cnt30() {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.setPredecessor(isa02);
        cn.deleteSuccessor();
        assertEquals(isa02, cn.getSuccessor());
    }
}