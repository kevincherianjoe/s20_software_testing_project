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
}