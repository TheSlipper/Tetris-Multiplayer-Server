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
        if (this.connected)
            return tcpClientSocket.getInetAddress().getAddress().toString();
        else
            return "[Not connected]";
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
            else if (!this.usesTcp) {
                this.udpClientSocket.disconnect();
                this.udpClientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connected = false;
    }

    public void sendStringData(String data) throws IOException {
        if (this.usesTcp) {
            bufferedOutputStream.write(data.getBytes(), 0, data.length());
            bufferedOutputStream.flush();
        } else {
            DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(), this.ipAddress, 7000);
            this.udpClientSocket.send(dp);
        }
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
}
