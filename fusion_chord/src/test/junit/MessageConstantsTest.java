package test.junit;

import chordfusion.MessageConstants;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageConstantsTest {

    @Test
    public void mct0() {
        final String endOfMessage = MessageConstants.build("Beginning of message - ", "end of message");
        String originalSockAddress = "127.0.0.1:1025";
        InetSocketAddress inetSocketAddress = MessageConstants.parseAddress(originalSockAddress);
        String strInetSockAddress = MessageConstants.addressToString(inetSocketAddress);
        assertEquals(originalSockAddress, strInetSockAddress);
    }

    @Test
    public void mct1() {
        final String endOfMessage = MessageConstants.build("Beginning of message - ", "end of message");
        String originalMessage = "Middle|of|message";
        String message = MessageConstants.constructMessage("Middle", "of", "message");
        String padString = MessageConstants.padInputvalue("xxx");
        System.out.println(message);
        System.out.println(padString);
        assertEquals(originalMessage, message);
    }

    @Test
    public void mct2() {
        InetSocketAddress inetSocketAddress;
        MessageConstants.parseAddress(null);
        String badIpPort = "127.0.0.1:1025:65535";
        MessageConstants.parseAddress(badIpPort);
        String strNullInetSockAddress = MessageConstants.addressToString(null);
        String padStringLong = MessageConstants.padInputvalue("123456789012345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890");
        String padStringNormal = MessageConstants.padInputvalue("123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890");
        assertTrue(padStringNormal.equals(padStringLong));
    }

}
