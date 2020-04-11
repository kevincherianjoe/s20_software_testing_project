package chordfusion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Set of utility methods related to hashing
 */
public final class HashUtils {

    private HashUtils() { }
 
    public static int HASH_SIZE = 8;

    /**
     * Compute the hash of an input string
     * @param input
     * @return hash of the input
     */
    public static BigInteger getHash(String input) {
        try {
            // algorithm SHA-1 
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // compute the hash
            byte[] messageDigest = md.digest(input.getBytes());

            // convert into BigInteger and return
            BigInteger hash = new BigInteger(1, messageDigest);
            BigInteger mask = new BigInteger("255");
            hash = hash.and(mask);

            return hash;
        }
        // if the agorithm isn't present, handle it
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Compute the hash of an address
     * @param address
     * @return
     */
    public static BigInteger getAddressHash(InetSocketAddress address) {
        return getHash(MessageConstants.build(address.getHostString(), Integer.toString(address.getPort())));
    }

    /**
     * Compute the relative distance of the hash values
     * @param to
     * @param from
     * @return
     */
    public static BigInteger computeRelativeHash(BigInteger to, BigInteger from) {
        BigInteger relative = to.subtract(from);
        if (relative.signum() == -1) {
            relative = relative.add(BigDecimal.valueOf(Math.pow(2, HASH_SIZE)).toBigInteger());
        }
        return relative;
    }
}