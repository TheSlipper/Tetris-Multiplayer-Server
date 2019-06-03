package command;

import management.DBQueryManager;
import management.SessionManager;
import management.CommandManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class List extends Command {

    private boolean cmdFlag = true;

    private boolean ipFlag = false;

    private boolean osFlag = false;

    private boolean usersFlag = false;

    public List(String cmdName) {
        super(cmdName);
    }

    private void output() throws SQLException {
        // CMDs
        if (cmdFlag) {
            int index = 0;
            System.out.println("[Commands]");
            for (Command cmd : CommandManager.commandArr) {
                System.out.println("[" + index + "]: " + cmd.getCommandName());
                index++;
            }
            System.out.println();
        }

        // IPs and OS'
        String[] ips = SessionManager.getIPs();
        if (ipFlag && osFlag) System.out.println("[IP and OS]");
        else if (ipFlag) System.out.println("[IP]");
        else if (osFlag) System.out.println("[OS]");
        for (int i = 0; i < ips.length; i++) {
            System.out.print("[" + i + "] ");
            if (ipFlag)
                System.out.print("ip: " + ips[i] + " ");
            if (osFlag)
                System.out.print("os: "); // TODO: Implement OS and build info
            System.out.println();
        }

        // Users
        if (this.usersFlag) {
            System.out.println("[Users]");
            System.out.println("[Format: User's ID; Nickname; Email; Registration Date]");
            ResultSet rs = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`users`");
            while (rs.next()) {
                System.out.printf("[%d]     %s     %s     %s\r\n", rs.getInt("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("registration_date"));
            }
        }
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("-")) {
                if (token.contains("c")) // c - command line commands output
                    this.cmdFlag = true;
                if (token.contains("i")) // i - ip addresses output
                    this.ipFlag = true;
                if (token.contains("o")) // o - OS output
                    this.osFlag = true;
                if (token.contains("u"))
                    this.usersFlag = true;
            }
        }
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        if (cmdTokenizer.hasMoreTokens()) {
            this.cmdFlag = false;
            this.loadFlags(cmdTokenizer);
        }
        try {
            this.output();
        } catch (SQLException e) {
            e.printStackTrace();
            this.executionStatus = false;
        }
        return this.executionStatus;
    }

    protected void clearFlags() {
        this.executionStatus = true;
        this.cmdFlag = true;
        this.ipFlag = false;
        this.osFlag = false;
        this.usersFlag = false;
    }
}
