package management;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * This class manages incoming data and executes specific tasks.
 *
 */
public class RequestManager {

    /** List of various request codes */
    private final static String[] REQUEST_CODE_ARRAY = {
            "LOGIN",
            "LOGOUT",
            "GET_USER_DATA",
            "GAME_SEARCH",
            "CANCEL_SEARCH",
            "ABORT_GAME",
            "GET_UPDATE_LOGS",
            "P_F_DATA"
    };

    /**
     * Checks whether the request code is in the request code array
     *
     * @param code codename
     * @return true if there is such a code
     */
    private static boolean containsCode(String code) {
        for (String str : REQUEST_CODE_ARRAY) {
            if (str.equals(code))
                return true;
        }
        return false;
    }

    /**
     * Redirects the request to a specific method/class to be executed or executes the request inside of its body
     *
     * @param code codename of the request
     * @param requestTokenized tokenized request arguments/data
     * @param sessionId session id of the request sender
     * @return returns the execution status
     * @throws SQLException on incorrect query or request
     */
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
            return ResponseManager.processResponse("SEND_USER_DATA " + rs.getString("elo")
                + " " + rs.getString("privilege_group") + " " + rs.getString("unranked_wins")
                + " " + rs.getString("unranked_losses") + " " + rs.getString("ranked_wins")
                + " " + rs.getString("ranked_losses") + " " + rs.getString("tetromino_points")
                + " " + rs.getString("time_played"), sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[3])) { // game search
            ResultSet rs = DBQueryManager.runSQLQuerry("SELECT elo FROM `TetrisMP`.`user_game_data`"
                            + " WHERE user_id=" + SessionManager.getSession(sessionId).getDbUserNameId());
            rs.next();
            MatchManager.addMatchTask("GAME_SETUP", sessionId + " "
                            + rs.getString("elo"), System.currentTimeMillis() / 1000);
            return true;
        } else if (code.equals(REQUEST_CODE_ARRAY[4])) { // Cancel Search
            return false;
        } else if (code.equals(REQUEST_CODE_ARRAY[5])) { // Abort Game
            return false;
        } else if (code.equals(REQUEST_CODE_ARRAY[6])) { // Get Update Logs
            StringBuilder sb = new StringBuilder();
            ResultSet news = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`update_logs` ORDER BY "
                    + "update_log_date LIMIT 5");
            while (news.next()) {
                ResultSet author = DBQueryManager.runSQLQuerry("SELECT username FROM `TetrisMP`.`users` WHERE user_id="
                        + news.getString("update_log_author"));
                author.next();
                String authorName = author.getString("username");

                sb.append(news.getString("update_log_header") + "\r\n");
                sb.append(news.getString("update_log_content") + "\r\n");
                sb.append(authorName + "\r\n");
                sb.append(news.getString("update_log_date") + "\r\n");
            }
            return ResponseManager.processResponse("SEND_UPDATE_LOGS\r\n" + sb.toString(), sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[7])) { // Player Field Data
            int matchId = Integer.parseInt(requestTokenized.nextToken());
            MatchManager.addFieldData(matchId, sessionId, requestTokenized.nextToken(""));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Public wrapper of multiple methods for handling requests in one method
     *
     * @param requestData string data of the request
     * @param sessionId session id of the request sender
     * @return execution status of the request
     */
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
