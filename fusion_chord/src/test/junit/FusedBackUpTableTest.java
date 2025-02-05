/**
 * 
 */
package test.junit;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import chordfusion.FusedBackUpTable;

/**
 *
 */
public class FusedBackUpTableTest {
	
	@Test 
	public void addOrReplaceFusedDataT1() {
		FusedBackUpTable fusedBackUpTable = new FusedBackUpTable();
		fusedBackUpTable.addOrReplaceFusedData(new byte[1],0,0);
		assertNotNull(fusedBackUpTable.getFusedData(0));
		assertNotNull(fusedBackUpTable.getClientServerFusedAuxPointerList().get(0));
		assertNotNull(fusedBackUpTable.getClientServersWithFusedDataAtAnIndex(0));
		assertNotNull(fusedBackUpTable.getClientServerFusedAuxPointer(0));
		assertEquals(fusedBackUpTable.print(), "Fused Bkp Table: \n" + 
				" \n" + 
				"ClientServerFusedAuxPointerList: \n" + 
				"Client ID : 0\n" + 
				"FusedAuxPointer : \n" + 
				"Index : 0IndexLocationInFusedBkp : 0" +
				"\n");
	}

}
