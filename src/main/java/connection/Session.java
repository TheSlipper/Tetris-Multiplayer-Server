package connection;

import management.RequestManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Session extends Thread {

    private Socket socket = new Socket();

    private BufferedInputStream bufferedInputStream;

    private BufferedOutputStream bufferedOutputStream;

    private int sessionId;

    private boolean connected = false;

    private boolean playing = false;

    private boolean inQueue = false;

    public Session() {}

    public Session(ServerSocket serverSocket, int sessionId) throws IOException {
        this.connect(serverSocket, sessionId);
    }

    private void messageLoop() throws IOException {
        while (true) {
            byte[] byteArr = new byte[512];
            int length = bufferedInputStream.read(byteArr);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < length; i++)
                sb.append((char)byteArr[i]);

            if (!sb.toString().equals("") && !RequestManager.processRequest(sb.toString(), this.sessionId))
                System.err.println("\r\n[Error in communication between host and the client]");

            if (!this.connected)
                return;
        }
    }

    public String getIp() {
        if (this.connected)
            return socket.getInetAddress().getAddress().toString();
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
            if (this.socket.isConnected())
            {
                this.socket.shutdownInput();
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringData(String data) throws IOException {

        bufferedOutputStream.write(data.getBytes(), 0, data.length());
        bufferedOutputStream.flush();
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }

    public void connect(ServerSocket serverSocket, int sessionId) throws IOException {
        this.socket = serverSocket.accept();
        this.bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
        this.bufferedOutputStream = new BufferedOutputStream(this.socket.getOutputStream());
        this.sessionId = sessionId;
        this.connected = true;
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
}
