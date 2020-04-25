package test.mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import chordfusion.ChordNode;
import chordfusion.ChordServer;
import chordfusion.ServerDataTable;
import chordfusion.ServerDataTable.ServerData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServerDataTable.class, ChordServer.class })
public class ChordServerTest {

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
        PowerMockito.whenNew(ChordNode.class).withAnyArguments().thenReturn(cnMock);
        when(cnMock.join(any()))
            .thenReturn(true);

        String in01 = "exit";
        InputStream in = new ByteArrayInputStream(in01.getBytes());
        System.setIn(in);

        ChordServer.main(new String[]{ path, "1" });
        assertTrue(false);
    }
}