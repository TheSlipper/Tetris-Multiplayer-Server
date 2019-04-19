package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Session extends Thread {

    private Socket socket;

    private BufferedInputStream bufferedInputStream;

    private BufferedOutputStream bufferedOutputStream;

    Session(ServerSocket serverSocket) throws IOException {
        this.socket = serverSocket.accept();
        this.bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
        this.bufferedOutputStream = new BufferedOutputStream(this.socket.getOutputStream());
    }

    private void messageLoop() throws IOException {
        while (true) {
            byte[] byteArr = new byte[512];
            int length = bufferedInputStream.read(byteArr);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < length; i++)
                sb.append((char)byteArr[i]);
            System.out.println(sb.toString());
        }
    }

    String getIp() {
        return socket.getInetAddress().getAddress().toString();
    }

    @Override
    public void run() {
        try {
            this.messageLoop();
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
}
