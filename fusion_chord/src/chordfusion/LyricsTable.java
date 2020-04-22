package chordfusion;

import java.util.ArrayList;
import java.util.List;

/**
 * table for song and corresponding lyrics data also auxiliary pointers to fused
 * data index
 */
public class LyricsTable {

    List<LyricsData> listOfLyricsData = new ArrayList<LyricsData>();
    List<LyricsDataAuxPointer> dataAuxPointers = new ArrayList<LyricsDataAuxPointer>();

    public static class LyricsData {
        private String song;
        private String lyrics;

        public LyricsData(String songName, String lyrics) {
            this.song = songName;
            this.lyrics = lyrics;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public String getLyrics() {
            return lyrics;
        }

        public void setLyrics(String lyrics) {
            this.lyrics = lyrics;
        }
    }

    public static class LyricsDataAuxPointer {
        private int indexLocationInLyricsTable;
        private int index;

        public LyricsDataAuxPointer(int indexLocationInLyricsTable, int index) {
            this.index = index;
            this.indexLocationInLyricsTable = indexLocationInLyricsTable;
        }

        public int getIndexLocationInLyricsTable() {
            return indexLocationInLyricsTable;
        }

        public void setIndexLocationInLyricsTable(int indexLocationInLyricsTable) {
            this.indexLocationInLyricsTable = indexLocationInLyricsTable;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    // add a lyrics to the table
    public synchronized void addLyrics(int index, String songName, String lyrics) {
        listOfLyricsData.add(index, new LyricsData(songName, lyrics));
        dataAuxPointers.add(new LyricsDataAuxPointer(index, index));
    }

    // get lyrics
    public synchronized String getLyrics(String songName) {
        for (LyricsData lyricsData : listOfLyricsData) {
            if (lyricsData.getSong().equals(songName)) {
                return lyricsData.getLyrics();
            }
        }

        return "no song found";
    }

    public synchronized String getAllLyrics() {
        StringBuilder sb = new StringBuilder();
        boolean firstSong = true;
        for (LyricsData data : listOfLyricsData) {
            if (firstSong) {
                firstSong = false;
            } else {
                sb.append("|#|");
            }
            sb.append(data.getSong());
            sb.append("|:|");
            sb.append(data.getLyrics());
        }

        return sb.toString();
    }

    // get data for fusion
    public synchronized String getDataAtIndex(int index) {
        for (LyricsDataAuxPointer lyricsDataAuxPointer : dataAuxPointers) {
            if (lyricsDataAuxPointer.getIndex() == index) {
                LyricsData responseData = listOfLyricsData.get(lyricsDataAuxPointer.getIndexLocationInLyricsTable());
                return responseData.getSong() + MessageConstants.VALUE_DELIM + responseData.getLyrics();
            }
        }
        return "no data found";
    }

    public synchronized List<LyricsData> getLyricsDataList() {
        return listOfLyricsData;
    }

    // TODO Update to handle spaces and use this for when a new chord node joins
    /*
     * public synchronized String getTransferableData(BigInteger hashValue) {
     * List<String> transferableData = new ArrayList<String>(); for(String songName
     * : listOfLyricsData.keySet()) { int compare =
     * HashUtils.getHash(songName).compareTo(hashValue); if(compare == -1 || compare
     * == 0) {
     * transferableData.add(songName+MessageConstants.SPACE+listOfLyricsData.get(
     * songName)); listOfLyricsData.remove(songName); } } return
     * MessageConstants.constructMessage((String[])transferableData.toArray()); }
     */

    /**
     * prints the data of this table
     */
    public synchronized String printTable() {
        StringBuilder builder = new StringBuilder();

        builder.append("\n My Lyrics Table: \n");
        for (LyricsData lyricsData : listOfLyricsData) {
            builder.append(lyricsData.getSong());
            builder.append(MessageConstants.VALUE_DELIM);
            builder.append(lyricsData.getLyrics());
            builder.append("\n");
        }
        builder.append("\n My Data AuxPointers: \n");
        for (LyricsDataAuxPointer lyricsDataAuxPointer : dataAuxPointers) {
            builder.append("Index : " + lyricsDataAuxPointer.getIndex());
            builder.append(MessageConstants.VALUE_DELIM);
            builder.append("Index at lyric data : " + lyricsDataAuxPointer.getIndexLocationInLyricsTable());
            builder.append("\n");
        }

        return "" + builder.toString();
    }
}
