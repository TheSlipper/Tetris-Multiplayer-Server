package command;

import management.DBQueryManager;

import java.sql.SQLException;
import java.util.StringTokenizer;

// TODO: A man page
public class CreateAccount extends Command {

    private String username, passwd, email;

    private int privilegeGroup, tetrominoPoints;

    private final int unrankedWins = 0, unrankedLosses = 0;

    private final int rankedWins = 0, rankedLosses = 0;

    private final int elo = 800;

    private final long timePlayed = 0;

    public CreateAccount(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                String nextToken = cmdTokenizer.nextToken();
                if (nextToken.startsWith("-")) {
                    System.err.println("[Incorrect token value: " + nextToken + " for " + token + " token]");
                    this.executionStatus = false;
                }
                else if (token.contains("username"))
                    this.username = nextToken;
                else if (token.contains("password"))
                    this.passwd = nextToken;
                else if (token.contains("email"))
                    this.email = nextToken;
                else if (token.contains("group-no")) // TODO: group-name
                    this.privilegeGroup = Integer.parseInt(nextToken);
                else if (token.contains("tetromino-pts"))
                    this.tetrominoPoints = Integer.parseInt(nextToken);
                else {
                    System.err.println("[Incorrect token: " + token + " ]");
                    this.executionStatus = false;
                }
            }
        }
    }

    private void createAccount() {
        // TODO: Show this query (contains transactions, )
        final String query = "BEGIN;"
                + "INSERT INTO `TetrisMP`.`users` VALUES (NULL, '" + this.username + "', '" + this.passwd + "'," +
                " '" + this.email + "', NULL);" +
                "INSERT INTO `TetrisMP`.`user_game_data` VALUES (NULL, (SELECT user_id FROM `TetrisMP`.`users` ORDER BY" +
                " DESC LIMIT 1), " + this.privilegeGroup + ", " + this.tetrominoPoints + ", " + this.timePlayed + "," +
                this.timePlayed + ", " + this.unrankedWins + ", " + this.unrankedLosses + ", " + this.rankedWins +
                ", " + this.rankedLosses + ", " + this.elo + ");" +
                "COMMIT;";
        try {
            DBQueryManager.runSQLueryNoRet(query);
        } catch (SQLException e) {
            this.executionStatus = false;
            e.printStackTrace();
        }
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);
        if (!this.executionStatus)
            return executionStatus;
        this.createAccount();
        return executionStatus;
    }

    protected void clearFlags() {
        this.executionStatus = true;
        this.username = null;
        this.passwd = null;
        this.email = null;
        this.privilegeGroup = -1;
        this.tetrominoPoints = -1;
    }
}
