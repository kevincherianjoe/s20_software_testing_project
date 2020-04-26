package test.junit;

import chordfusion.ServerDataTable;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ServerDataTableTest {

    @Test
    public void sdt0() {

        String ipAddressIP;
        int portIP;
        int serverProcessNumberIP;
        ServerDataTable serverDataTable;
        List<ServerDataTable.ServerData> serverDataTableList;
        String serverType;

        ipAddressIP = "127.0.0.1";
        portIP = 8033;
        serverProcessNumberIP = 3;
        serverType = "C";

        serverDataTable = ServerDataTable.getServerTable("fusion_chord/src/chordfusion/server.txt", 1, "C");
        serverDataTable.addServer(ipAddressIP, portIP, serverProcessNumberIP, serverType);
        serverDataTable.updateMyServerDetails(ipAddressIP, portIP, serverProcessNumberIP, serverType);
        assertEquals(serverDataTable.getMyServer().getIpAddress(), ipAddressIP);
        assertEquals(serverDataTable.getMyServer().getPort(), portIP);
        assertEquals(serverDataTable.getMyServer().getServerProcessNumber(), serverProcessNumberIP);
        assertEquals(serverDataTable.getMyServer().getServerType(), serverType);
        assertEquals(serverDataTable.getNumberOfFusionServers(), 3);
        assertEquals(serverDataTable.getMyServer().getStatus(), 1);
        serverDataTable.updateServerStatus(serverProcessNumberIP,0);
        assertEquals(serverDataTable.getMyServer().getStatus(), 1);
        int liveServerCount = serverDataTable.getLiveServerCount();
        assertEquals(liveServerCount, 2);
        ServerDataTable.ServerTCPClient serverTCPClient = serverDataTable.getMyServer().getServerClient();
        try {
            serverTCPClient.send("Hello");
        } catch (IOException e) {
            System.out.println("IOException");
            System.out.println(e);
        }
        try {
            serverTCPClient.sendReceieve("Goodbye");
        } catch (IOException e) {
            System.out.println("IOException");
            System.out.println(e);
        }
        serverDataTableList = serverDataTable.getServerDataTable();

        for (ServerDataTable.ServerData serverData: serverDataTableList) {
            System.out.printf("IP: %s, Port: %d, Process #: %d, Server Type: %s\n", serverData.getIpAddress(),
                    serverData.getPort(), serverData.getServerProcessNumber(), serverData.getServerType());
        }

    }

    @Test
    public void sdt1() {
        ServerDataTable.getServerTable("xxx", 1, "C");
    }
}
