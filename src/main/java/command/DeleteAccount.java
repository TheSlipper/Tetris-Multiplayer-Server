package command;

import management.DBQueryManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

// TODO: Man Page
public class DeleteAccount extends Command {

    private boolean allResultsFlag = false;

    private boolean forceFlag = false;

    private boolean usernameTag = false;

    private String username;

    private boolean emailTag = false;

    private String email;

    private boolean idTag = false;

    private int userId = -1;

    private boolean groupTag = false;

    private int groupid = -1;

    private ArrayList<Integer> targetIds;

    public DeleteAccount(String cmdName) {
        super(cmdName);
    }

    @Override
    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();

            if (token.startsWith("--")) {
                if (token.contains("username")) {
                    this.usernameTag = true;
                    this.username = cmdTokenizer.nextToken();
                } else if (token.contains("email")) {
                    this.emailTag = true;
                    this.email = cmdTokenizer.nextToken();
                } else if (token.contains("id")) {
                    this.idTag = true;
                    this.userId = Integer.parseInt(cmdTokenizer.nextToken());
                } else if (token.contains("group-no")) {
                    this.groupTag = true;
                    this.groupid = Integer.parseInt(cmdTokenizer.nextToken());
                }
            } else if (token.startsWith("-")) {
                if (token.contains("a"))
                    this.allResultsFlag = true;
                if (token.contains("f"))
                    this.forceFlag = true;

                if (token.contains("u")) {
                    this.usernameTag = true;
                    this.username = cmdTokenizer.nextToken();
                } else if (token.contains("e")) {
                    this.emailTag = true;
                    this.email = cmdTokenizer.nextToken();
                } else if (token.contains("i")) {
                    this.idTag = true;
                    this.userId = Integer.parseInt(cmdTokenizer.nextToken());
                } else if (token.contains("g")) {
                    this.groupTag = true;
                    this.groupid = Integer.parseInt(cmdTokenizer.nextToken());
                }
            }
            else
                this.executionStatus = false;
        }
    }

    private void findAndDisplayUsers() throws SQLException {
        // TODO: Show off the inner join and REGEXP
        boolean prevAppended = false;
        StringBuilder searchQuery = new StringBuilder();
        searchQuery.append("SELECT * FROM `TetrisMP`.`users` INNER JOIN `TetrisMP`.`user_game_data` ON users.user_id=user_game_data.user_id WHERE ");
        if (this.usernameTag) {
            searchQuery.append("users.username REGEXP '" + this.username + "'");
            prevAppended = true;
        }
        if (this.emailTag) {
            if (prevAppended)
                searchQuery.append(" AND ");
            searchQuery.append("users.email REGEXP '" + this.email + "'");
        }
        if (this.idTag) {
            if (prevAppended)
                searchQuery.append(" AND ");
            searchQuery.append("users.user_id=" + this.userId + " AND user_game_data.user_id=" + this.userId);
        }
        if (this.groupTag) {
            if (prevAppended)
                searchQuery.append(" AND ");
            searchQuery.append("user_game_data.privilege_group=" + this.groupid);
        }

        ResultSet rs = DBQueryManager.runSQLQuerry(searchQuery.toString());
        if (!rs.next()) {
            System.err.println("No results found");
            this.executionStatus = false;
            return;
        }
        System.err.println("[Warning]");
        System.out.println("You are about to delete that user/those users: ");
        do {
            this.targetIds.add(rs.getInt("users.user_id"));
            System.out.printf("User's Id: %s; Group: %s; Username: %s; Email: %s; Registration Date: %s\r\n",
                    rs.getString("users.user_id"),
                    rs.getString("user_game_data.privilege_group"),
                    rs.getString("users.username"),
                    rs.getString("users.email"),
                    rs.getString("users.registration_date"));
            if (!this.allResultsFlag)
                break;
        } while (rs.next());
        System.out.print("Are you really sure you want to delete them y/n: ");
        if (!this.forceFlag && !new Scanner(System.in).nextLine().startsWith("y"))
            this.executionStatus = false;
    }

    private void deleteTargetUsers() throws SQLException {
        System.out.println("Deleting specified users...");
        for (int a : this.targetIds) {
            final String query = "DELETE FROM `users` WHERE `users`.`user_id` = " + a;
            DBQueryManager.runSQLueryNoRet(query);
        }
    }

    @Override
    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);
        if (!this.executionStatus)
            return this.executionStatus;

        try {
            this.findAndDisplayUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            this.executionStatus = false;
        }
        if (!this.executionStatus)
            return this.executionStatus;

        try {
            this.deleteTargetUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            this.executionStatus = false;
        }

        return this.executionStatus;
    }

    @Override
    protected void clearFlags() {
        this.targetIds = new ArrayList<Integer>();
        this.executionStatus = true;
        this.usernameTag = false;
        this.emailTag = false;
        this.idTag = false;
        this.userId = -1;
        this.username = null;
        this.email = null;
        this.forceFlag = false;
        this.allResultsFlag = false;
        this.groupTag = false;
        this.groupid = -1;
    }
}
