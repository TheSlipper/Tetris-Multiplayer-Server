import connection.SessionManager;
import management.DBQueryManager;
import management.CommandManager;

import java.io.IOException;
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
        Server.runServer();
    }

    private static void runServer() {
        Server.sessionManager.start();

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
