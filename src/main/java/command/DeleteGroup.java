package command;

import management.DBQueryManager;

import java.sql.SQLException;
import java.util.StringTokenizer;

public class DeleteGroup extends Command {

    private String groupName;

    private int groupId;

    public DeleteGroup(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while(cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                String nextToken = cmdTokenizer.nextToken();
                if (token.contains("name")) {
                    this.groupName = nextToken;
                } else if (token.contains("id")) {
                    this.groupId = Integer.parseInt(nextToken);
                }
            } else if (token.startsWith("-")) {
                String nextToken = cmdTokenizer.nextToken();
                if (token.contains("n"))
                    this.groupName = nextToken;
                else if (token.contains("i"))
                    this.groupId = Integer.parseInt(nextToken);
            }
        }
    }

    private void deleteGroup() throws SQLException {
        final String query = "DELETE FROM `privilege_groups` WHERE `privilege_groups`." +  (this.groupName == null ? "`group_id`='" + this.groupId + "'" : "`group_name` REGEXP '" + this.groupName + "';");
        DBQueryManager.runSQLueryNoRet(query);
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);

        if (this.groupName == null && this.groupId == -1)
            return false;


        try {
            this.deleteGroup();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.executionStatus;
    }

    protected void clearFlags() {
        this.groupName = null;
        this.groupId = -1;
    }
}
