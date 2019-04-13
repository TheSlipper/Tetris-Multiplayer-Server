import connection.SessionManager;
import management.DBQueryManager;
import management.CommandManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Server {

    private static SessionManager sessionManager;

    private static DBQueryManager queryManager;

    private static CommandManager cmdManager;

    private static void initServer() {
        try {
            Server.sessionManager = new SessionManager("localhost", 6911);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server.cmdManager = new CommandManager();
        Server.queryManager = new DBQueryManager();
        try {
            Server.runServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void runServer() throws UnknownHostException {
        Server.sessionManager.start();

        System.out.println("[Tetris Multiplayer Server - Pre-Alpha Build]");
        System.out.println("[Host info: " + InetAddress.getLocalHost() + "]");
        System.out.println("########################################################################");
        System.out.println("This is a university project - it is not meant to be used commercially");
        System.out.println("For license check the LICENSE file in the included source");
        System.out.println("Made by Kornel (TheSlipper) Domeradzki");
        System.out.println("https://github.com/TheSlipper/Tetris-Multiplayer-Server");


        while (true) {
            Scanner sc = new Scanner(System.in);
            String cmd;
            System.out.print(">");
            cmd = sc.nextLine();
            if (!cmdManager.manageCommand(cmd))
                System.err.println("Unkown command [" + cmd + "]:");
        }
    }

    public static void main(String[] args) {
        Server.initServer();
    }

}
