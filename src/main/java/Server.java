import management.SessionManager;
import management.DBQueryManager;
import management.CommandManager;
import management.MatchManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This class manages the whole Tetris Multiplayer Server program.<br><br>
 *
 * Server class is a singleton class that manages the whole Tetris Multiplayer Server program, initializes all of the server's components and accepts command input from the user after the startup.
 *
 * @author Kornel Domeradzki
 * @version Indev 0.0.1 May 24, 2019
 */
public class Server {

    /** Access point to all of the sessions */
    private static SessionManager sessionManager;

    /** Access point to the database query management class */
    private static DBQueryManager queryManager;

    /** Access point to the command management class */
    private static CommandManager cmdManager;

    /** Access point to the match management class */
    private static MatchManager matchManager;

    /** Username used by server's pseudo-bash-like shell */
    private static String username = "slipper";

    /** Constant IP address of the server */
    private static final String IP_ADDR = "localhost";

    /** Constant port of the server */
    private static final int PORT = 7001;

    /**
     * login method is a method responsible for user login
     */
    private static void login() {
        Server.username = DBQueryManager.getUsername();
        System.out.println("[Successfully logged in]");
        cmdManager.manageCommand("clear");
    }

    /**
     * initTcpServer method initializes server's components and informs the whole system about using tcp sockets<br><br>
     *
     * It is responsible for creation of SessionManager, CommandManager, MatchManager and is an entry point for the runServer method
     */
    private static void initTcpServer() {
        try {
            Server.sessionManager = new SessionManager(true, Server.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server.cmdManager = new CommandManager();
        Server.queryManager = new DBQueryManager();
        Server.matchManager = new MatchManager();
        Server.login();
        try {
            Server.runServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * initUdpServer method initializes server's components and informs the whole system about using udp sockets<br><br>
     *
     * It is responsible for creation of SessionManager, CommandManager, MatchManager and is an entry point for the runServer method
     */
    private static void initUdpServer() {
        try {
            Server.sessionManager = new SessionManager(false, Server.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server.cmdManager = new CommandManager();
        Server.queryManager = new DBQueryManager();
        Server.matchManager = new MatchManager();
        Server.login();
        try {
            Server.runServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * runServer method runs the server after initialization and accepts server-side commands
     *
     * runServer method is a method responsible for constant command input and session and match manager start
     *
     * @throws UnknownHostException
     */
    private static void runServer() throws UnknownHostException {
        Server.sessionManager.start();
        Server.matchManager.start();

        System.out.println("[Tetris Multiplayer Server - Pre-Alpha Build]");
        System.out.println("[Host info: " + InetAddress.getLocalHost() + "]");
        System.out.println("########################################################################");
        System.out.println("This is a university project - it is not meant to be used commercially");
        System.out.println("If you find any bugs or problems with this piece of software please contact me");
        System.out.println("For license check the LICENSE file in the included source");
        System.out.println("Made by Kornel (TheSlipper) Domeradzki");
        System.out.println("https://github.com/TheSlipper/Tetris-Multiplayer-Server");


        while (true) {
            Scanner sc = new Scanner(System.in);
            String cmd;
            System.out.print("tetris-mp: ");
            System.out.print(Server.username + "$ ");
            cmd = sc.nextLine();
            if (!cmdManager.manageCommand(cmd))
                System.err.println("Error occurred while executing [" + cmd + "]");
        }
    }

    public static int getServerPort() {
        return Server.PORT;
    }

    /**
     * Main method responsible for parsing the passed arguments
     *
     * @param args cli arguments
     */
    public static void main(String[] args) {
        System.out.println("[TCP/UDP]");
        System.out.println("TCP - Slower but reliable");
        System.out.println("UDP - Faster but less reliable");
        System.out.print("Do you want to use TCP(t) or UDP(u) sockets: ");
        Scanner sc = new Scanner(System.in);
        String response = sc.nextLine();
        if (response.toLowerCase().startsWith("t"))
            Server.initTcpServer();
        else
            Server.initUdpServer();
    }

}
