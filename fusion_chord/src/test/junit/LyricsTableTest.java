package test.junit;

import static org.junit.Assert.*;
import org.junit.Test;

import chordfusion.LyricsTable;
import chordfusion.MessageConstants;

public class LyricsTableTest {

    // test LyricsData getters and setters
    @Test public void LyricsDataGettersSetters() {
        String initName = "name";
        String initLyrics = "lyrics";
        LyricsTable.LyricsData ld = new LyricsTable.LyricsData(initName, initLyrics);
        assertEquals(ld.getSong(), initName);
        assertEquals(ld.getLyrics(), initLyrics);
        String modName = "modname";
        String modLyrics = "modlyrics";
        ld.setSong(modName);
        ld.setLyrics(modLyrics);
        assertEquals(ld.getSong(), modName);
        assertEquals(ld.getLyrics(), modLyrics);
    }

    // test LyricsDataAuxPointer getters and setters
    @Test public void LyricsDataAuxPointerGettersSetters() {
        int initLoc = 1;
        int initIndex = 10;
        LyricsTable.LyricsDataAuxPointer ldap = new LyricsTable.LyricsDataAuxPointer(initLoc, initIndex);
        assertEquals(ldap.getIndexLocationInLyricsTable(), initLoc);
        assertEquals(ldap.getIndex(), initIndex);
        int modLoc = 2;
        int modIndex = 11;
        ldap.setIndexLocationInLyricsTable(modLoc);
        ldap.setIndex(modIndex);
        assertEquals(ldap.getIndexLocationInLyricsTable(), modLoc);
        assertEquals(ldap.getIndex(), modIndex);
    }

    // test addLyrics base
    @Test public void AddLyrics01() {
        int index = 0;
        String song = "song";
        String lyrics = "lyrics";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals(1, lt.getLyricsDataList().size());
        assertEquals(song + MessageConstants.VALUE_DELIM + lyrics, lt.getDataAtIndex(index));
    }

    // test addLyrics add duplicate
    @Test public void AddLyrics02() {
        int index = 0;
        String song = "song";
        String lyrics = "lyrics";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals(1, lt.getLyricsDataList().size());
        lt.addLyrics(index, song, lyrics);
        assertEquals(1, lt.getLyricsDataList().size());
    }

    // test addLyrics add two
    @Test public void AddLyrics03() {
        int index1 = 0;
        String song1 = "song1";
        String lyrics1 = "lyrics1";
        int index2 = 1;
        String song2 = "song2";
        String lyrics2 = "lyrics2";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index1, song1, lyrics1);
        assertEquals(1, lt.getLyricsDataList().size());
        assertEquals(song1 + MessageConstants.VALUE_DELIM + lyrics1,
            lt.getDataAtIndex(index1));
        lt.addLyrics(index2, song2, lyrics2);
        assertEquals(2, lt.getLyricsDataList().size());
        assertEquals(song2 + MessageConstants.VALUE_DELIM + lyrics2,
            lt.getDataAtIndex(index2));
    }

    // test getLyrics no songs
    @Test public void AddLyrics04() {
        String song = "song";
        LyricsTable lt = new LyricsTable();
        assertEquals("no song found", lt.getLyrics(song));
    }

    // test getLyrics valid song
    @Test public void AddLyrics05() {
        int index = 0;
        String song = "song";
        String lyrics = "lyrics";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals(lyrics, lt.getLyrics(song));
    }

    // test getLyrics invalid song
    @Test public void AddLyrics06() {
        int index = 0;
        String song = "song";
        String songInvalid = "songInvalid";
        String lyrics = "lyrics";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals("no song found", lt.getLyrics(songInvalid));
        assertEquals(lyrics, lt.getLyrics(song));
    }

    // test getAllLyrics no songs
    @Test public void GetAllLyrics01() {
        LyricsTable lt = new LyricsTable();
        assertEquals("", lt.getAllLyrics());
    }

    // test getAllLyrics one song
    @Test public void GetAllLyrics02() {
        int index = 0;
        String song = "song";
        String lyrics = "lyrics";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals(song + "|:|" + lyrics, lt.getAllLyrics());
    }

    // test getAllLyrics two songs
    @Test public void GetAllLyrics03() {
        int index1 = 0;
        String song1 = "song1";
        String lyrics1 = "lyrics1";
        int index2 = 1;
        String song2 = "song2";
        String lyrics2 = "lyrics2";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index1, song1, lyrics1);
        lt.addLyrics(index2, song2, lyrics2);
        assertEquals(song1 + "|:|" + lyrics1 + "|#|" + song2 + "|:|" + lyrics2,
            lt.getAllLyrics());
    }

    // test printTable no songs
    @Test public void PrintTable01() {
        LyricsTable lt = new LyricsTable();
        String expected = "\n My Lyrics Table: \n";
        expected += "\n My Data AuxPointers: \n";
        assertEquals(expected, lt.printTable());
    }

    // test printTable one song
    @Test public void PrintTable02() {
        int index = 0;
        String song = "song";
        String lyrics = "lyrics";
        String expected = "\n My Lyrics Table: \n";
        expected += "song=lyrics\n";
        expected += "\n My Data AuxPointers: \n";
        expected += "Index : 0=Index at lyric data : 0\n";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index, song, lyrics);
        assertEquals(expected, lt.printTable());
    }

    // test printTable two songs
    @Test public void PrintTable03() {
        int index1 = 0;
        String song1 = "song1";
        String lyrics1 = "lyrics1";
        int index2 = 1;
        String song2 = "song2";
        String lyrics2 = "lyrics2";
        String expected = "\n My Lyrics Table: \n";
        expected += "song1=lyrics1\n";
        expected += "song2=lyrics2\n";
        expected += "\n My Data AuxPointers: \n";
        expected += "Index : 0=Index at lyric data : 0\n";
        expected += "Index : 1=Index at lyric data : 1\n";
        LyricsTable lt = new LyricsTable();
        lt.addLyrics(index1, song1, lyrics1);
        lt.addLyrics(index2, song2, lyrics2);
        assertEquals(expected, lt.printTable());
    }
}