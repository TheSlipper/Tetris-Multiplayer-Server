package connection;

import management.RequestManager;

import java.io.*;
import java.net.*;

public class Session extends Thread {

    private Socket tcpClientSocket = new Socket();

    private DatagramSocket udpClientSocket;

    private InetAddress ipAddress;

    private BufferedInputStream bufferedInputStream;

    private BufferedOutputStream bufferedOutputStream;

    private int sessionId;

    private int dbUsernameId;

    private int matchId = -1;

    private boolean usesTcp;

    private boolean connected = false;

    private boolean playing = false;

    private boolean inQueue = false;



    private int elo = 800;

    private int privilegeGroup = -1;

    private int unrankedWins = 0;

    private int unrankedLosses = 0;

    private int rankedWins = 0;

    private int rankedLosses = 0;

    private long tetrominoPoints = 0;

    private long timePlayed = 0;

    public Session() {}

    public Session(ServerSocket serverSocket, int sessionId) throws IOException {
        this.setName("Session-Thread-" + sessionId);
        this.connect(serverSocket, sessionId);
        this.connected = true;
        this.usesTcp = true;
    }

    public Session(int sessionId, InetAddress addr, int portId) throws SocketException {
        this.setName("Session-Thread-" + sessionId);
        this.ipAddress = addr;
        this.usesTcp = false;
        this.udpClientSocket = new DatagramSocket(portId);
        this.connected = true;
    }

//    private static int getStrChecksum(final String str, int strLen) {
//        int chk = 0;
//        for (int i = 0; i < strLen; i++)
//            chk -= (int)str.charAt(i);
//
//        return chk/str.length();
//    }

    private void tcpRead() throws IOException {
        if (!this.usesTcp) {
            this.udpRead();
            return;
        }
        byte[] byteArr = new byte[512];
        int length = this.bufferedInputStream.read(byteArr);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append((char)byteArr[i]);

        if (!sb.toString().equals("") && !RequestManager.processRequest(sb.toString(), this.sessionId))
            System.err.println("\r\n[Error in communication between host and the client]");
    }

    private void udpRead() throws IOException {
        if (this.usesTcp) {
            this.tcpRead();
            return;
        }
        byte[] byteArr = new byte[512];
        DatagramPacket datagramPacket = new DatagramPacket(byteArr, 0, byteArr.length);
        this.udpClientSocket.receive(datagramPacket);
        String content = new String(datagramPacket.getData());


        // TODO: Checksum maybe
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == (char)0)
                content = content.substring(0, i);
        }

        DatagramPacket dp = new DatagramPacket("OK".getBytes(), "OK".length(), this.ipAddress, 7000);
        this.udpClientSocket.send(dp);

//        System.out.println(Session.getStrChecksum(content, content.length()));

        if (!content.equals("") && !RequestManager.processRequest(content, this.sessionId))
            System.err.println("\r\n[Error in communication between host and the client]");
    }

    private void messageLoop() throws IOException {
        while (true) {
            if (this.usesTcp)
                this.tcpRead();
            else
                this.udpRead();

            if (!this.connected)
                return;
        }
    }

    public String getIp() {
        if (!this.usesTcp && this.connected)
            return this.ipAddress.getCanonicalHostName();

        else
            return this.connected ? tcpClientSocket.getInetAddress().getCanonicalHostName() : "[Not connected]";
    }

    @Override
    public void run() {
        try {
            this.messageLoop();
            this.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (this.usesTcp)
                this.tcpClientSocket.close();
            else if (!this.usesTcp && this.udpClientSocket != null) {
                this.udpClientSocket.disconnect();
                this.udpClientSocket.close();
            }
            else
                return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connected = false;
    }

    public boolean sendStringData(String data) throws IOException {
        boolean execStatus = false;
        if (this.usesTcp) {
            bufferedOutputStream.write(data.getBytes(), 0, data.length());
            bufferedOutputStream.flush();
            execStatus = true;
        } else {
            while (!execStatus) {
                // TODO: Checksum maybe
                DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(), this.ipAddress, 7000);
                this.udpClientSocket.send(dp);

                byte[] byteArr = new byte[2];
                DatagramPacket dp2 = new DatagramPacket(byteArr, 0, byteArr.length);
                this.udpClientSocket.receive(dp2);
                execStatus = new String(dp2.getData()).equals("OK");
            }
        }

        return execStatus;
    }

    public boolean isConnected() {
        if (this.usesTcp)
            return this.tcpClientSocket.isConnected();
        else
            return this.connected;
    }

    public void connect(ServerSocket serverSocket, int sessionId) throws IOException {
            this.tcpClientSocket = serverSocket.accept();
            this.bufferedInputStream = new BufferedInputStream(this.tcpClientSocket.getInputStream());
            this.bufferedOutputStream = new BufferedOutputStream(this.tcpClientSocket.getOutputStream());
            this.sessionId = sessionId;
            this.connected = true;
    }

    public void connect(int sessionId, InetAddress addr, int portId) throws SocketException {
        this.setName("Session-Thread-" + sessionId);
        this.ipAddress = addr;
        this.usesTcp = false;
        this.udpClientSocket = new DatagramSocket(portId + (sessionId+1));
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public void setInQueue(boolean queue) {
        this.inQueue = queue;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setMatchId(int id) {
        this.matchId = id;
    }

    public void setDbUsernameId(int id) {
        this.dbUsernameId = id;
    }

    public int getDbUserNameId() {
        return this.dbUsernameId;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getUnrankedWins() {
        return unrankedWins;
    }

    public void setUnrankedWins(int unrankedWins) {
        this.unrankedWins = unrankedWins;
    }

    public int getUnrankedLosses() {
        return unrankedLosses;
    }

    public void setUnrankedLosses(int unrankedLosses) {
        this.unrankedLosses = unrankedLosses;
    }

    public int getRankedWins() {
        return rankedWins;
    }

    public void setRankedWins(int rankedWins) {
        this.rankedWins = rankedWins;
    }

    public int getRankedLosses() {
        return rankedLosses;
    }

    public void setRankedLosses(int rankedLosses) {
        this.rankedLosses = rankedLosses;
    }

    public long getTetrominoPoints() {
        return tetrominoPoints;
    }

    public void setTetrominoPoints(long tetrominoPoints) {
        this.tetrominoPoints = tetrominoPoints;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }

    public int getPrivilegeGroup() {
        return privilegeGroup;
    }

    public void setPrivilegeGroup(int privilegeGroup) {
        this.privilegeGroup = privilegeGroup;
    }
}
