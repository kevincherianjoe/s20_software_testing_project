package chordfusion;
import java.net.InetSocketAddress;

/**
 * Constants for messages passed between servers
 */
public final class MessageConstants {

    private MessageConstants() { }

    public static String DELIM = "|";
    public static String IP_DELIM = ":";
    public static String SPACE = " ";
    public static String VALUE_DELIM = "=";
    
    public static int MAX_LYRICS_LENGTH = 100;
    
    public static int THREAD_SLEEP_MS = 15000;

    //CHORD
    public static String FIND_SUCCESSOR = "find-successor";
    public static String GET_SUCCESSOR = "get-successor";
    public static String GET_PREDECESSOR = "get-predecessor";
    public static String MY_SUCCESSOR = "my-successor";
    public static String MY_PREDECESSOR = "my-predecessor";
    public static String NO_VALUE = "no-value";
    public static String FOUND_SUCCESSOR = "found-successor";
    public static String CLOSEST_PRECEDING = "closest";
    public static String MY_CLOSEST_PRECEDING = "my-closest";
    public static String NOTIFY = "notify";
    public static String NOTIFY_ACK = "notify-ack";
    
    //APP
    public static String ADD_LYRICS = "addLyrics";
    public static String FIND_LYRICS = "findLyrics";
    public static String LIST_MY_LYRICS = "listMyLyrics";
    public static String FIND_ALL_LYRICS = "findAllLyrics";
    
    //FUSION
    public static String ADD_DATA = "addData";
    public static String GET_DATA = "getData";
    public static String GET_ALL_DATA =  "getAllFusedData";

    //COMMON
    public static String PING = "ping";
    public static String PONG = "pong";

    /**
     * Compose a message separated by DELIM
     * @param prefix
     * @param suffix
     * @return
     */
    public static String build(String prefix, String suffix) {
        return prefix + DELIM + suffix;
    }

    /**
     * Compose an InetSocketAddress as a string
     * @param address
     * @return
     */
    public static String addressToString(InetSocketAddress address) {
        if (address == null) {
            return "null";
        } else {
            return address.getAddress().getHostAddress() + IP_DELIM + address.getPort();
        }
    }

    /**
     * Parse an InetSocketAddress from a string
     * @param stringAddress
     * @return
     */
    public static InetSocketAddress parseAddress(String stringAddress) {
        if (stringAddress == null) {
            return null;
        } else {
            String[] ipTokens = stringAddress.split(MessageConstants.IP_DELIM);
            if (ipTokens.length == 2) {
                String address = ipTokens[0];
                int port = Integer.parseInt(ipTokens[1]);
                return new InetSocketAddress(address, port);
            } else {
                return null;
            }
        }
    }
    
    /**
     * Build a DELIM separated message
     * @param args
     * @return
     */
	public static String constructMessage(String...args) {
    	
    	StringBuilder messageBuilder = null;
    	for(String arg:args){
    		if(messageBuilder == null) {
    			messageBuilder = new StringBuilder(arg);
    		} else {
    			messageBuilder.append(DELIM);
    			messageBuilder.append(arg);
    		}
    	}
    	
        return messageBuilder.toString();
    }
    
    /**
     * Pad lyric string
     * @param value
     * @return
     */
	public static String padInputvalue(String value) {
    	if(value.length() < MessageConstants.MAX_LYRICS_LENGTH) {
    		return String.format("%-" + MessageConstants.MAX_LYRICS_LENGTH + "s", value);
    	} else if (value.length() > MessageConstants.MAX_LYRICS_LENGTH) {
    		return value.substring(0, MessageConstants.MAX_LYRICS_LENGTH);
    	}
    	return value;
    }
	
}