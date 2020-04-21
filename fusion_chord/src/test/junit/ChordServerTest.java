package test.junit;

import chordfusion.ChordServer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class ChordServerTest {

    @Test
    public void cst0() {
//        ChordServer.main(new String[]{"No Agruements"});
    }

    @Test
    public void cst1() {

        String data = "exit\r\n";
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        } finally {
            System.setIn(stdin);
        }
        ChordServer.main(new String[]{"fusion_chord/src/server.txt", "1"});

    }
}
