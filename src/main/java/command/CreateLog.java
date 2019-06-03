package command;

import management.DBQueryManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class CreateLog extends Command {

    private StringBuilder logHeader;

    private StringBuilder logContent;

    private String authorName;

    private int authorId;

    public CreateLog(String cmdName) {
        super(cmdName);
    }

    @Override
    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                if (token.contains("log-header")) {
                    do {
                        logHeader.append(cmdTokenizer.nextToken());
                    } while (logHeader.charAt(logHeader.length()-1) != '"' || logHeader.charAt(logHeader.length()-1) == '\\');
                } else if (token.contains("content")) {
                    do {
                        logContent.append(cmdTokenizer.nextToken());
                    } while (logContent.charAt(logContent.length()-1) != '"' || logContent.charAt(logContent.length()-1) == '\\');
                } else if (token.contains("author-name")) {
                    this.authorName = cmdTokenizer.nextToken();
                } else if (token.contains("author-id")) {
                    this.authorId = Integer.parseInt(cmdTokenizer.nextToken());
                }
            } else if (token.startsWith("-")) {
                if (token.contains("h")) {
                    do {
                        logHeader.append(" ");
                        logHeader.append(cmdTokenizer.nextToken());
                    } while (logHeader.charAt(logHeader.length()-1) != '"' || logHeader.charAt(logHeader.length()-1) == '\\');
                } else if (token.contains("c")) {
                    do {
                        logContent.append(" ");
                        logContent.append(cmdTokenizer.nextToken());
                    } while (logContent.charAt(logContent.length()-1) != '"' || logContent.charAt(logContent.length()-1) == '\\');
                } else if (token.contains("n")) {
                    this.authorName = cmdTokenizer.nextToken();
                } else if (token.contains("i")) {
                    this.authorId = Integer.parseInt(cmdTokenizer.nextToken());
                }
            } else {
                System.err.println("[Incorrect token: " + token + " ]");
                this.executionStatus = false;
            }
        }
    }


    private void getUserIdByNickname() throws SQLException {
        final String nicknameQuery = "SELECT user_id FROM `TetrisMP`.`users` WHERE username='" + this.authorName + "'";
        ResultSet rs = DBQueryManager.runSQLQuerry(nicknameQuery);
        if (!rs.next()) {
            this.executionStatus = false;
            return;
        }
        this.authorId = rs.getInt("user_id");
    }

    private void createLog() throws SQLException {
        final String insertionQuery = "INSERT INTO `TetrisMP`.`update_logs` (`update_log_id`, `update_log_header`, `update_log_content`, `update_log_author`, `update_log_date`) VALUES (NULL, '" + this.logHeader.toString().replaceAll("[\"]", "") + "', '" + this.logContent.toString().replaceAll("[\"]", "") + "', '" + this.authorId + "', CURRENT_TIMESTAMP)";
        DBQueryManager.runSQLueryNoRet(insertionQuery);
    }

    @Override
    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);
        if (!this.executionStatus)
            return this.executionStatus;

            try {
                if (this.authorName != null)
                    this.getUserIdByNickname();
                this.createLog();
            } catch (SQLException e) {
                e.printStackTrace();
                this.executionStatus = false;
            }

        return this.executionStatus;
    }

    @Override
    protected void clearFlags() {
        this.logHeader = new StringBuilder();
        this.logContent = new StringBuilder();
        this.authorName = null;
        this.authorId = -1;
    }
}
