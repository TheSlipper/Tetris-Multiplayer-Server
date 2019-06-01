package command;

import management.DBQueryManager;

import java.sql.SQLException;
import java.util.StringTokenizer;

public class CreateGroup extends Command {

    private String groupName;

    private boolean banUsrPermissionFlag = false;

    private boolean postNewsPermissionFlag = false;

    private boolean resetUsrDataPermissionFlag = false;

    private boolean servLogInPermission = false;

    public CreateGroup(String cmdName) {
        super(cmdName);
    }

    @Override
    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while(cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                String nextToken = cmdTokenizer.nextToken();
                if (token.contains("group-name"))
                    this.groupName = nextToken;
            } else if (token.startsWith("-")) {
                if (token.contains("b"))
                    this.banUsrPermissionFlag = true;
                if (token.contains("p"))
                    this.postNewsPermissionFlag = true;
                if (token.contains("r"))
                    this.resetUsrDataPermissionFlag = true;
                if (token.contains("l"))
                    this.servLogInPermission = true;
            }
        }

        if (this.groupName == null) {
            System.err.println("No specified group name");
            this.executionStatus = false;
        }
    }

    private int boolToInt(boolean value) {
        return value ? 1 : 0;
    }

    private void makeGroup() throws SQLException {
        final String query = "INSERT INTO `privilege_groups` (`group_id`, `group_name`, `ban_usr_permission`, `post_news_permission`," +
                "`reset_usr_data_permission`, `server_log_in_permission`) VALUES (NULL, '" + this.groupName + "', '" +
                this.boolToInt(this.banUsrPermissionFlag) + "', '" + this.boolToInt(this.postNewsPermissionFlag) + "', '"  +
                this.boolToInt(this.resetUsrDataPermissionFlag) + "', '" + this.boolToInt(this.servLogInPermission) + "')";
        DBQueryManager.runSQLueryNoRet(query);
    }

    @Override
    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);

        if (!this.executionStatus)
            return this.executionStatus;

        try {
            this.makeGroup();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return this.executionStatus;
    }

    @Override
    protected void clearFlags() {
        this.groupName = null;
        this.postNewsPermissionFlag = false;
        this.banUsrPermissionFlag = false;
        this.resetUsrDataPermissionFlag = false;
        this.servLogInPermission = false;
    }
}
