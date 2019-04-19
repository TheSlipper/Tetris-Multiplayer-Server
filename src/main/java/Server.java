import connection.SessionManager;
import management.DBQueryManager;
import management.CommandManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

// TODO: SU mode

public class Server {

    private static SessionManager sessionManager;

    private static DBQueryManager queryManager;

    private static CommandManager cmdManager;

    private static String username = "slipper"; // TODO: Load from database

    private static boolean rootMode = false;

    private static final String IP_ADDR = "localhost";

    private static final int PORT = 6969;

    private static void login() {
        System.out.println("[Initializing command-line interface]");
        Scanner sc = new Scanner(System.in);
        System.out.print("Server Login: ");
        String login = sc.nextLine();
        System.out.print("Server password: ");
        String passwd = sc.nextLine();

        if (!Server.queryManager.areLoginCredentialsValid(login, passwd)) {
            System.out.println("[Error: No such login or password]");
            System.exit(-1);
        } else {
            System.out.println("[Successfully logged in]");
            Server.username = login;
            cmdManager.manageCommand("clear");
        }
    }

    private static void initServer() {
        try {
            Server.sessionManager = new SessionManager(Server.IP_ADDR, Server.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server.cmdManager = new CommandManager();
        Server.queryManager = new DBQueryManager();
        Server.login();
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
            System.out.print("tetris-mp: ");
            if (!Server.rootMode)
                System.out.print(Server.username + "$ ");
            else
                System.out.print("root$ ");
            cmd = sc.nextLine();
            if (!cmdManager.manageCommand(cmd))
                System.err.println("Error occurred while executing [" + cmd + "]");
        }
    }

    public static void main(String[] args) {
        Server.initServer();
    }

}
