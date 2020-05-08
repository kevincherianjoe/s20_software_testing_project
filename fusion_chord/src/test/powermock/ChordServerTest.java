package test.powermock;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import chordfusion.ChordNode;
import chordfusion.ChordServer;
import chordfusion.MessageConstants;
import chordfusion.ServerDataTable;
import chordfusion.ServerDataTable.ServerData;
import chordfusion.ServerDataTable.ServerTCPClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServerDataTable.class, ChordServer.class })
public class ChordServerTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    // test invalid server data table file path
    @Test(expected = NullPointerException.class)
    public void cst01() {
        PowerMockito.mockStatic(ServerDataTable.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(new ServerDataTable());
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        when(sdtMock.getMyServer()).thenReturn(null);

        ChordServer.main(new String[]{ "invalid-path", "0" });
        assertTrue(false);
    }

    // test empty ServerDataTable & immediate exit
    @Test
    public void cst02() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
        when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdtMock.getServerDataTable())
            .thenReturn(new ArrayList<ServerData>());
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(true);

        String in01 = "exit";
        InputStream in = new ByteArrayInputStream(in01.getBytes());
        System.setIn(in);

        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test empty ServerDataTable & fail to join
    @Test
    public void cst03() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
        when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdtMock.getServerDataTable())
            .thenReturn(new ArrayList<ServerData>());
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(false);

        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test empty ServerDataTable & print status
    @Test
    public void cst04() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
        when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdtMock.getServerDataTable())
            .thenReturn(new ArrayList<ServerData>());
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(true);
        exit.checkAssertionAfterwards(new Assertion() {
            public void checkAssertion() {
                verify(cnMock).printStatus();
            }
        });
        exit.expectSystemExitWithStatus(0);

        String in = "status\nexit\n";
        InputStream is = new ByteArrayInputStream(in.getBytes());
        System.setIn(is);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test empty ServerDataTable & execute mock action
    @Test
    public void cst05() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
        when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdtMock.getServerDataTable())
            .thenReturn(new ArrayList<ServerData>());
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(true);
        when(cnMock.handleClientMessages("command"))
            .thenReturn("response");
        exit.checkAssertionAfterwards(new Assertion() {
            public void checkAssertion() {
                verify(cnMock).handleClientMessages("command");
            }
        });
        exit.expectSystemExitWithStatus(0);

        String in = "command\nexit\n";
        InputStream is = new ByteArrayInputStream(in.getBytes());
        System.setIn(is);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test non-empty ServerDataTable & fusion server type
    @Test
    public void cst06() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        List<ServerData> sdMockList = new ArrayList<ServerData>();
        sdMockList.add(sdMock);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
        when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdMock.getServerType())
            .thenReturn(ServerDataTable.FUSION_SERVER_TYPE);
        when(sdtMock.getServerDataTable())
            .thenReturn(sdMockList);
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(false);
        when(cnMock.handleClientMessages("command"))
            .thenReturn("response");
        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test non-empty ServerDataTable & chord server type & valid ping response
    @Test
    public void cst07() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ServerTCPClient scMock = mock(ServerTCPClient.class);
        List<ServerData> sdMockList = new ArrayList<ServerData>();
        sdMockList.add(sdMock);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
            when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdMock.getServerType())
            .thenReturn(ServerDataTable.CHORD_SERVER_TYPE);
        when(sdMock.getServerClient())
            .thenReturn(scMock);
        when(scMock.sendReceieve(MessageConstants.PING))
            .thenReturn(MessageConstants.PONG);
        when(sdtMock.getServerDataTable())
            .thenReturn(sdMockList);
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(false);
        when(cnMock.handleClientMessages("command"))
            .thenReturn("response");
        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test non-empty ServerDataTable 
    // & chord server type
    // & valid ping response
    // & throw exception when creating InetSocketAddress
    @Test
    public void cst08() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ServerTCPClient scMock = mock(ServerTCPClient.class);
        List<ServerData> sdMockList = new ArrayList<ServerData>();
        sdMockList.add(sdMock);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort)
            .thenThrow(NumberFormatException.class);
            when(sdMock.getIpAddress())
            .thenReturn(ipAddr)
            .thenThrow(NumberFormatException.class);
        when(sdMock.getServerType())
            .thenReturn(ServerDataTable.CHORD_SERVER_TYPE);
        when(sdMock.getServerClient())
            .thenReturn(scMock);
        when(scMock.sendReceieve(MessageConstants.PING))
            .thenReturn(MessageConstants.PING);
        when(sdtMock.getServerDataTable())
            .thenReturn(sdMockList);
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(false);
        when(cnMock.handleClientMessages("command"))
            .thenReturn("response");
        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }

    // test non-empty ServerDataTable 
    // & chord server type
    // & invalid ping response
    @Test
    public void cst09() throws Exception {
        String path = System.getProperty("user.dir") + "/../serverTable.txt";
        String ipAddr = "ipaddr";
        int ipPort = 10;

        PowerMockito.mockStatic(ServerDataTable.class);
        ServerDataTable sdtMock = mock(ServerDataTable.class);
        ServerData sdMock = mock(ServerData.class);
        ServerTCPClient scMock = mock(ServerTCPClient.class);
        List<ServerData> sdMockList = new ArrayList<ServerData>();
        sdMockList.add(sdMock);
        ChordNode cnMock = mock(ChordNode.class);
        when(ServerDataTable.getServerTable(anyString(), anyInt(), anyString()))
            .thenReturn(sdtMock);
        when(sdtMock.getMyServer())
            .thenReturn(sdMock);
        when(sdMock.getPort())
            .thenReturn(ipPort);
            when(sdMock.getIpAddress())
            .thenReturn(ipAddr);
        when(sdMock.getServerType())
            .thenReturn(ServerDataTable.CHORD_SERVER_TYPE);
        when(sdMock.getServerClient())
            .thenReturn(scMock);
        when(scMock.sendReceieve(MessageConstants.PING))
            .thenReturn("not-pong");
        when(sdtMock.getServerDataTable())
            .thenReturn(sdMockList);
        PowerMockito.whenNew(ChordNode.class)
            .withAnyArguments()
            .thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(false);
        when(cnMock.handleClientMessages("command"))
            .thenReturn("response");
        exit.expectSystemExitWithStatus(0);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }
}