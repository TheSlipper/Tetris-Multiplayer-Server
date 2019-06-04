package command;

import management.DBQueryManager;

import java.sql.SQLException;
import java.util.StringTokenizer;

public class DeleteLog extends Command {

    private int logId;

    public DeleteLog(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            final String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                final String tokenVal = cmdTokenizer.nextToken();
                if (token.contains("log-id")) {
                    this.logId = Integer.parseInt(tokenVal);
                }
            }
        }
    }

    private void deleteLog() throws SQLException {
        final String query = "DELETE FROM `update_logs` WHERE `update_logs`.`update_log_id` = " + logId;
        DBQueryManager.runSQLueryNoRet(query);
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);

        if (logId == -1)
            return false;
        try {
            this.deleteLog();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.executionStatus;
    }

    protected void clearFlags() {
        this.executionStatus = true;
        this.logId = -1;
    }
}
