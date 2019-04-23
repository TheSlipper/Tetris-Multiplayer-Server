package connection;

import management.RequestManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Session extends Thread {

    private Socket socket;

    private OutputStream outputStream;

    private BufferedInputStream bufferedInputStream;

    private BufferedOutputStream bufferedOutputStream;

    private PrintWriter printWriter;

    private int sessionId;

    Session(ServerSocket serverSocket, int sessionId) throws IOException {
        this.socket = serverSocket.accept();
        this.bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
        this.outputStream = this.socket.getOutputStream();
        this.bufferedOutputStream = new BufferedOutputStream(outputStream);
        this.printWriter = new PrintWriter(this.outputStream);
        this.sessionId = sessionId;
    }

    private void messageLoop() throws IOException {
        while (true) {
            byte[] byteArr = new byte[512];
            int length = bufferedInputStream.read(byteArr);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < length; i++)
                sb.append((char)byteArr[i]);

            if (!RequestManager.processRequest(sb.toString(), this.sessionId))
                System.err.println("\r\n[Error in communication between host and the client]");

            if (this.socket.isClosed())
                return;
        }
    }

    String getIp() {
        return socket.getInetAddress().getAddress().toString();
//                return new String(socket.getInetAddress().getAddress());
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
        this.stop();
        try {
            this.socket.sendUrgentData(-2); // SUDDEN_SHUTDOWN
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringData(String data) throws IOException {
        bufferedOutputStream.write(data.getBytes(), 0, data.length());
        bufferedOutputStream.flush();
    }
}
