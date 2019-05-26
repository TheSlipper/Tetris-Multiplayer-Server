package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServerSocket {

    private DatagramSocket socket;

    private final String PORT_ALLOCATION_RESPONSE = "PORT_ALLOCATED";

    private final String PORT_ALLOCATION_REQUEST = "PORT_ALL_REQ";

    private final int GAME_PORT = 7000;

    public UDPServerSocket(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public InetAddress accept(int sessionId) throws IOException {
        byte[] buffer = new byte[255];
        String responseStr = this.PORT_ALLOCATION_RESPONSE + " " + (7002 + sessionId);
        byte[] response = responseStr.getBytes();

        DatagramPacket dp;
        while(true) {
            dp = new DatagramPacket(buffer, 0, buffer.length);
            socket.receive(dp);
            if (new String(dp.getData()).startsWith(this.PORT_ALLOCATION_REQUEST))
                break;
        }


        DatagramPacket responsePacket = new DatagramPacket(response, response.length, dp.getAddress(), GAME_PORT);
        socket.send(responsePacket);

        return dp.getAddress();
    }

}
