package test;

import static org.junit.Assert.*;
import org.junit.Test;

import chordfusion.DataTuple;

public class DataTupleTest {

    // test first constructor w/ null primaryData
    @Test public void Constructor1stNullPrimaryData() {
        DataTuple dt = new DataTuple(null, 0);
        assertNull(dt.getData());
    }

    // test first constructor w/ empty primaryData
    @Test public void Constructor1stEmptyPrimaryData() {
        DataTuple dt = new DataTuple(new byte[] { }, 0);
        assertEquals(0, dt.getData().length);
    }

    // test first constructor w/ negative clientServerId
    @Test public void Constructor1stNegativeClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, -1);
        assertEquals(-1, dt.getClientServerId());
    }

    // test first constructor w/ positive clientServerId
    @Test public void Constructor1stPositiveClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 1);
        assertEquals(1, dt.getClientServerId());
    }

    // test first constructor w/ zero clientServerId
    @Test public void Constructor1stZeroClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 0);
        assertEquals(0, dt.getClientServerId());
    }

    // test second constructor w/ null primaryData
    @Test public void Constructor2ndNullPrimaryData() {
        DataTuple dt = new DataTuple(null, 0, false);
        assertNull(dt.getData());
    }

    // test second constructor w/ empty primaryData
    @Test public void Constructor2ndEmptyPrimaryData() {
        DataTuple dt = new DataTuple(new byte[] { }, 0, false);
        assertEquals(0, dt.getData().length);
    }

    // test second constructor w/ negative clientServerId
    @Test public void Constructor2ndNegativeClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, -1, false);
        assertEquals(-1, dt.getClientServerId());
    }

    // test second constructor w/ positive clientServerId
    @Test public void Constructor2ndPositiveClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 1, false);
        assertEquals(1, dt.getClientServerId());
    }

    // test second constructor w/ zero clientServerId
    @Test public void Constructor2ndZeroClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 0, false);
        assertEquals(0, dt.getClientServerId());
    }

    // test second constructor w/ isDataAvailable true
    @Test public void Constructor2ndTrueDataAvailable() {
        DataTuple dt = new DataTuple(new byte[] { }, 0, true);
        assertTrue(dt.isDataAvailable());
    }

    // test second constructor w/ isDataAvailable false
    @Test public void Constructor2ndFalseDataAvailable() {
        DataTuple dt = new DataTuple(new byte[] { }, 0, false);
        assertFalse(dt.isDataAvailable());
    }

    // test getData
    @Test public void DataTupleGetData() {
        DataTuple dt = new DataTuple(new byte[] { 1, 2 }, 0);
        assertArrayEquals(new byte[] { 1, 2 }, dt.getData());
    }

    // test setData
    @Test public void DataTupleSetData() {
        DataTuple dt = new DataTuple(new byte[] { 1 }, 0);
        dt.setData(new byte[] { 3, 4 });
        assertArrayEquals(new byte[] { 3, 4 }, dt.getData());
    }

    // test getClientServerId
    @Test public void DataTupleGetClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 25);
        assertEquals(25, dt.getClientServerId());
    }

    // test setClientServerId
    @Test public void DataTupleSetClientServerId() {
        DataTuple dt = new DataTuple(new byte[] { }, 35);
        dt.setClientServerId(45);
        assertEquals(45, dt.getClientServerId());
    }

    // test isDataAvailable
    @Test public void DataTupleIsDataAvailable() {
        DataTuple dt = new DataTuple(new byte[] { }, 0);
        assertTrue(dt.isDataAvailable());
    }

    // test setDataAvailable
    @Test public void DataTupleSetDataAvailable() {
        DataTuple dt = new DataTuple(new byte[] { }, 0);
        assertEquals(true, dt.isDataAvailable());
        dt.setDataAvailable(false);
        assertEquals(false, dt.isDataAvailable());
        dt.setDataAvailable(true);
        assertEquals(true, dt.isDataAvailable());
    }
}