package test;


import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.net.InetSocketAddress;

import org.junit.Test;

import chordfusion.HashUtils;

public class HashUtilsTest {
	
	@Test
	public void getHashT1() {
		
		assertEquals(new BigInteger("27"), HashUtils.getHash("A"));
		assertEquals(new BigInteger("236"), HashUtils.computeRelativeHash(new BigInteger("10"), new BigInteger("30")));
		
		InetSocketAddress address = new InetSocketAddress("localhost", 7890);
		assertEquals(new BigInteger("68"),HashUtils.getAddressHash(address));
		
	}

}
