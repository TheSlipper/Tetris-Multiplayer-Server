package management;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class RequestManager {

    private final static String[] REQUEST_CODE_ARRAY = {
            "LOGIN",
            "LOGOUT",
            "GET_USER_DATA",
            "GAME_SEARCH",
            "CANCEL_SEARCH",
            "ABORT_GAME"
    };

    private static boolean containsCode(String code) {
        for (String str : REQUEST_CODE_ARRAY) {
            if (str.equals(code))
                return true;
        }
        return false;
    }

    private static boolean redirectRequest(String code, StringTokenizer requestTokenized, int sessionId) throws SQLException {
        if (code.equals(REQUEST_CODE_ARRAY[0])) { // log in
            String login = requestTokenized.nextToken();
            boolean execStatus = DBQueryManager.areLoginCredentialsValid(login, requestTokenized.nextToken());
            if (!execStatus)
                ResponseManager.processResponse("INCORRECT_CREDENTIALS " + login, sessionId);
            else
                ResponseManager.processResponse("CORRECT_CREDENTIALS " + login, sessionId);
            return execStatus;
        } else if (code.equals(REQUEST_CODE_ARRAY[1])) { // log out
            SessionManager.shutdownSession(sessionId);
            return true;
        } else if (code.equals(REQUEST_CODE_ARRAY[2])) { // User Data
            String username = requestTokenized.nextToken();
            ResultSet rs = DBQueryManager.runSQLQuerry("SELECT user_id FROM `TetrisMP`.`users` WHERE "
                    + "username=\"" + username + "\"");
            rs.next();
            String id = rs.getString("user_id");
            rs = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`user_game_data` WHERE "
                    + "user_id=" + id);
            rs.next();
            ResponseManager.processResponse("SEND_USER_DATA " + rs.getString("elo")
                + " " + rs.getString("privilege_group") + " " + rs.getString("unranked_wins")
                + " " + rs.getString("unranked_losses") + " " + rs.getString("ranked_wins")
                + " " + rs.getString("ranked_losses") + " " + rs.getString("tetromino_points")
                + " " + rs.getString("time_played"), sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[3])) { // game search
            MatchManager.addMatchTask("GAME_SETUP", Integer.toString(sessionId),
                    System.currentTimeMillis() / 1000);
            return true;
        }

        return false;
    }

    public static boolean processRequest(String requestData, int sessionId) {
        StringTokenizer requestTokenized = new StringTokenizer(requestData);
        String code = requestTokenized.nextToken();

        if (!RequestManager.containsCode(code))
            return false;

        try {
            return RequestManager.redirectRequest(code, requestTokenized, sessionId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
