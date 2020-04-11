# F19 - Final Project - Fusion with Chord

Kevin John Cherian Joseph (eid: kc43529)
<br/>
Arnob Mallick (eid: am93476)

## List of files
- Ensure the following files are available along with com.backblaze.erasure for Encoding and Decoding the Fused Data
```
ChordNode.java
ChordServer.java
DataTuple.java
FaultRecoveryAgentThread.java
FingerTable.java
FixFingersThread.java
FusedBackUpTable.java
FusionServer.java
FusionServerThread.java
HashUtils.java
LyricsTable.java
MessageConstants.java
MessageHandler.java
MessageRouter.java
PingPredecessorThread.java
ServerDataTable.java
StabilizeThread.java
server.txt

```

## Build
- Build the client and server programs
```
javac ChordServer.java

javac FusionServer.java

server.txt file contains the port, I.P, process number and server type - Fusion or Chord
```


## Run Chord
- Run the ChordServer
- Provide the server process id based on the content in server.txt (needs to start from 1 to n)
- And start all the chord servers
```
java ChordServer <path to server.txt> <serverProcessID> 

```

## Run Fusion
- Run the FusionServer
- Provide the server process id based on the content in server.txt (needs to start from 1 to n)
- And start all the fusion servers
```
java FusionServer <path to server.txt> <serverProcessID> 

```

## Testing the application
- The application is a shared storage P2P application where users can store Lyrics of songs and retrieve.
- to add lyrics (songName can not have spaces and combined limit of songName and Lyrics is 80 characters)
- to view lyrics 
- status to view data stored in each server
```
addLyrics <songName> <songLyrics> 
findLyrics <songName>
status 
```

