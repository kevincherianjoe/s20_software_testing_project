package test.mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import chordfusion.ChordNode;
import chordfusion.FingerTable;
import chordfusion.FixFingersThread;
import chordfusion.MessageConstants;
import chordfusion.MessageRouter;
import chordfusion.PingPredecessorThread;
import chordfusion.ServerDataTable;
import chordfusion.StabilizeThread;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ChordNode.class })
public class ChordNodeTest {

    // test first join & ensure threads are started
    @Test
    public void cst01() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);

        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(isa, sdt);
        assertTrue(cn.join(isa));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected = "Joining ring via 127.0.0.1:3001\n";
        assertEquals(expected, allWrittenLines);
        verify(mrMock).start();
        verify(stMock).start();
        verify(fftMock).start();
        verify(pptMock).start();
    }

    // test join NOT as first node & null successor
    @Test
    public void cnt02() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));
        
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);

        InetSocketAddress firstIsa = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress otherIsa = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(firstIsa, sdt);
        assertFalse(cn.join(otherIsa));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected =
            "Joining ring via 127.0.0.1:3002\n" +
            "Failed to send request (find-successor|6) to /127.0.0.1:3002\n" +
            "Failed to parse address from response\n" +
            "Failed to locate successor node when joining.\n";
        assertEquals(expected, allWrittenLines);
        verifyZeroInteractions(mrMock);
        verifyZeroInteractions(stMock);
        verifyZeroInteractions(fftMock);
        verifyZeroInteractions(pptMock);
    }

    // test join NOT as first node & non-null successor
    @Test
    public void cnt03() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));
        
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock = new ByteArrayInputStream("no-value".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock);

        InetSocketAddress firstIsa = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress otherIsa = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(firstIsa, sdt);
        assertTrue(cn.join(otherIsa));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected =
            "Joining ring via 127.0.0.1:3002\n";
        assertEquals(expected, allWrittenLines);
        verify(mrMock).start();
        verify(stMock).start();
        verify(fftMock).start();
        verify(pptMock).start();
    }

    // test getSuccessor w/ non-null successor
    @Test
    public void cnt04() throws Exception {
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock = new ByteArrayInputStream("no-value".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock);

        InetSocketAddress firstIsa = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress otherIsa = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(firstIsa, sdt);
        cn.join(otherIsa);

        assertEquals(otherIsa, cn.getSuccessor());
    }

    // test findSuccessor w/ non-null successor
    @Test
    public void cnt05() throws Exception {
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock = new ByteArrayInputStream("no-value".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock);

        InetSocketAddress firstIsa = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress otherIsa = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();
        ChordNode cn = new ChordNode(firstIsa, sdt);
        cn.join(otherIsa);

        assertEquals(otherIsa, cn.findSuccessor(new BigInteger("0")));
    }

    // test findSuccessor w/ different predecessor
    @Test
    public void cnt06() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa01)
            .thenReturn(isa02)
            .thenReturn(isa03);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock04 = new ByteArrayInputStream("\\|127.0.0.1:3004".getBytes());
        InputStream isMock05 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04)
            .thenReturn(isMock05);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa01, cn.findSuccessor(new BigInteger("179")));
    }

    // test findPredecessor w/ join setup
    // & closest = some other node
    // & successor not null
    @Test
    public void cnt07() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        InetSocketAddress isa04 = new InetSocketAddress("127.0.0.1", 3004);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02)
            .thenReturn(isa03);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock04 = new ByteArrayInputStream("\\|127.0.0.1:3004".getBytes());
        InputStream isMock05 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04)
            .thenReturn(isMock05);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa04, cn.findPredecessor(new BigInteger("179")));
    }

    // test findPredecessor w/ join setup
    // & closest = some other node
    // & successor null
    @Test
    public void cnt08() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02)
            .thenReturn(isa03);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock04 = new ByteArrayInputStream("\\|127.0.0.1:3004".getBytes());
        InputStream isMock05 = new ByteArrayInputStream("\\|invalid".getBytes());
        InputStream isMock06 = new ByteArrayInputStream("\\|127.0.0.1:3001".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04)
            .thenReturn(isMock05)
            .thenReturn(isMock06);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa03, cn.findPredecessor(new BigInteger("179")));
    }

    // test findPredecessor w/ join setup
    // & closest = self
    @Test
    public void cnt09() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02)
            .thenReturn(isa03);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock04 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa03, cn.findPredecessor(new BigInteger("179")));
    }

    // test findPredecessor w/ join setup
    // & closest = no response
    @Test
    public void cnt10() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02)
            .thenReturn(isa03);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock04 = new ByteArrayInputStream("\\|invalid".getBytes());
        InputStream isMock05 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock06 = new ByteArrayInputStream("\\|127.0.0.1:3001".getBytes());
        InputStream isMock07 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock08 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04)
            .thenReturn(isMock05)
            .thenReturn(isMock06)
            .thenReturn(isMock07)
            .thenReturn(isMock08);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa03, cn.findPredecessor(new BigInteger("179")));
    }

    // test closestPrecedingFinger w/ non-null
    // & closest and alive
    @Test
    public void cnt11() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa02, cn.closestPrecedingFinger(new BigInteger("179")));
    }

    // test closestPrecedingFinger w/ non-null
    // & closest and not alive
    @Test
    public void cnt12() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("10"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock03 = new ByteArrayInputStream("not-pong".getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa01, cn.closestPrecedingFinger(new BigInteger("179")));
    }

    // test closestPrecedingFinger w/ non-null
    // & not closest and alive
    @Test
    public void cnt13() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        FingerTable ftMock = mock(FingerTable.class);
        PowerMockito.whenNew(FingerTable.class).withAnyArguments().thenReturn(ftMock);
        when(ftMock.getFinger(anyInt()))
            .thenReturn(isa02);
        when(ftMock.getFingerId(anyInt()))
            .thenReturn(new BigInteger("6"));
        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock02 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.join(isa02);

        assertEquals(isa01, cn.closestPrecedingFinger(new BigInteger("179")));
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor not null and not successor
    // & predecessorsPredecessor is null
    // & predecessorsPredecessor is predecessor
    @Test
    public void cnt14() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock02 = new ByteArrayInputStream("\\|127.0.0.1:3002".getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa01);
        cn.setPredecessor(isa02);
        cn.deleteSuccessor();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor not null and not successor
    // & predecessorsPredecessor is null
    // & predecessorsPredecessor is self
    @Test
    public void cnt15() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock02 = new ByteArrayInputStream("\\|127.0.0.1:3001".getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa02);
        cn.setPredecessor(isa03);
        cn.deleteSuccessor();
        assertEquals(isa03, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor not null and not successor
    // & predecessorsPredecessor is null
    // & predecessorsPredecessor is successor
    @Test
    public void cnt16() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock02 = new ByteArrayInputStream("\\|127.0.0.1:3002".getBytes());
        InputStream isMock03 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa02);
        cn.setPredecessor(isa03);
        cn.deleteSuccessor();
        assertEquals(isa03, cn.getSuccessor());
    }

    // test deleteSuccessor w/ successor as only entry in finger table
    // & predecessor not null and not successor
    // & predecessorsPredecessor is null
    // & predecessorsPredecessor is not predecessor or self or successor
    @Test
    public void cnt17() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        InetSocketAddress isa04 = new InetSocketAddress("127.0.0.1", 3004);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        InputStream isMock02 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        InputStream isMock03 = new ByteArrayInputStream("\\|127.0.0.1:3002".getBytes());
        InputStream isMock04 = new ByteArrayInputStream(MessageConstants.PONG.getBytes());
        when(soMock.getInputStream())
            .thenReturn(isMock01)
            .thenReturn(isMock02)
            .thenReturn(isMock03)
            .thenReturn(isMock04);

        ChordNode cn = new ChordNode(isa01, sdt);
        cn.setFingerSyncrhonized(0, isa02);
        cn.setPredecessor(isa04);
        cn.deleteSuccessor();
        assertEquals(isa02, cn.getSuccessor());
    }

    // test sendReceiveMessage no exceptions
    @Test
    public void cnt18() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("expected".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        String expected = "expected";
        assertEquals(expected, cn.sendReceiveMessage(isa01, "message"));
    }

    // test sendReceiveMessage output exception
    @Test
    public void cnt19() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        doThrow(new IOException("exception")).when(soMock).getOutputStream();
        InputStream isMock01 = new ByteArrayInputStream("expected".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveMessage(isa01, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected = "Failed to send request (message) to /127.0.0.1:3001\n";
        assertEquals(expected, allWrittenLines);
    }

    // test sendReceiveMessage input exception
    @Test
    public void cnt20() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        doThrow(new IOException("exception")).when(soMock).getInputStream();

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveMessage(isa01, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected = "Failed to read response from /127.0.0.1:3001\n";
        assertEquals(expected, allWrittenLines);
    }

    // test sendReceiveMessage close exception
    @Test
    public void cnt21() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("expected".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);
        doThrow(new IOException("exception")).when(soMock).close();

        ChordNode cn = new ChordNode(isa01, sdt);
        String expected = "expected";
        assertEquals(expected, cn.sendReceiveMessage(isa01, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expectedStdout = "Failed to close socket after use\n";
        assertEquals(expectedStdout, allWrittenLines);
    }

    // test sendReceiveMessage no input
    @Test
    public void cnt22() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveMessage(isa01, "message"));
    }

    // test sendReceiveAddress - no-value response
    @Test
    public void cnt23() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("no-value".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(isa02, cn.sendReceiveAddress(isa02, "message"));
    }

    // test sendReceiveAddress - valid response
    @Test
    public void cnt24() throws Exception {
        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        InetSocketAddress isa03 = new InetSocketAddress("127.0.0.1", 3003);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|127.0.0.1:3003".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(isa03, cn.sendReceiveAddress(isa02, "message"));
    }

    // test sendReceiveAddress - invalid address
    @Test
    public void cnt25() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|notanipaddress".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveAddress(isa02, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expectedStdout = "Failed to parse address from response\n";
        assertEquals(expectedStdout, allWrittenLines);
    }

    // test sendReceiveAddress - invalid response w/ too many tokens
    @Test
    public void cnt26() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("\\|notanipaddress\\|anothertoken".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveAddress(isa02, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expectedStdout = "Invalid format of address in response: \\|notanipaddress\\|anothertoken\n";
        assertEquals(expectedStdout, allWrittenLines);
    }

    // test sendReceiveAddress - invalid response w/ no tokens
    @Test
    public void cnt27() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        when(soMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        InputStream isMock01 = new ByteArrayInputStream("invalid".getBytes());
        when(soMock.getInputStream()).thenReturn(isMock01);

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveAddress(isa02, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expectedStdout = "Invalid format of address in response: invalid\n";
        assertEquals(expectedStdout, allWrittenLines);
    }

    // test sendReceiveAddress - parse exception
    @Test
    public void cnt28() throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));

        InetSocketAddress isa01 = new InetSocketAddress("127.0.0.1", 3001);
        InetSocketAddress isa02 = new InetSocketAddress("127.0.0.1", 3002);
        ServerDataTable sdt = new ServerDataTable();

        MessageRouter mrMock = mock(MessageRouter.class);
        PowerMockito.whenNew(MessageRouter.class).withAnyArguments().thenReturn(mrMock);
        StabilizeThread stMock = mock(StabilizeThread.class);
        PowerMockito.whenNew(StabilizeThread.class).withAnyArguments().thenReturn(stMock);
        FixFingersThread fftMock = mock(FixFingersThread.class);
        PowerMockito.whenNew(FixFingersThread.class).withAnyArguments().thenReturn(fftMock);
        PingPredecessorThread pptMock = mock(PingPredecessorThread.class);
        PowerMockito.whenNew(PingPredecessorThread.class).withAnyArguments().thenReturn(pptMock);
        Socket soMock = mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withAnyArguments().thenReturn(soMock);
        doThrow(new IOException("exception")).when(soMock).getOutputStream();

        ChordNode cn = new ChordNode(isa01, sdt);
        assertEquals(null, cn.sendReceiveAddress(isa02, "message"));

        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expectedStdout =
            "Failed to send request (message) to /127.0.0.1:3002\n" +
            "Failed to parse address from response\n";
        assertEquals(expectedStdout, allWrittenLines);
    }
}