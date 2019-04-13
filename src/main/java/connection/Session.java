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
            byte[] byteArr = new byte[1];
            int sizeOfMsg = bufferedInputStream.read(byteArr);
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
}
