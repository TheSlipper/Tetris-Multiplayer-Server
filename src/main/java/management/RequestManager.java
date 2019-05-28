package management;

import connection.Session;

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
            "LOGIN", // [0]
            "LOGOUT", // [1]
            "GET_USER_DATA", // [2]
            "GAME_SEARCH", // [3]
            "CANCEL_SEARCH", // [4]
            "ABORT_GAME", // [5]
            "GET_UPDATE_LOGS", // [6]
            "P_F_DATA" // [7]
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
    private static boolean redirectRequest(String code, StringTokenizer requestTokenized, int sessionId) {
        if (code.equals(REQUEST_CODE_ARRAY[0])) /* Log in */ {
            try {
                return RequestManager.logIn(requestTokenized, sessionId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (code.equals(REQUEST_CODE_ARRAY[1])) /* Log out */ {
            return RequestManager.logOut(sessionId);
        }
        else if (code.equals(REQUEST_CODE_ARRAY[2])) /* Get user Data */ {
            try {
                return RequestManager.getUserData(requestTokenized, sessionId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (code.equals(REQUEST_CODE_ARRAY[3])) /* game search */ {
            try {
                return RequestManager.gameSearch(sessionId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (code.equals(REQUEST_CODE_ARRAY[4])) /* Cancel Search */ {
            return false;
        }
        else if (code.equals(REQUEST_CODE_ARRAY[5])) /*  Abort Game */ {
            return false;
        }
        else if (code.equals(REQUEST_CODE_ARRAY[6])) /* Get Update Logs */ {
            try {
                return RequestManager.getUpdateLogs(sessionId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (code.equals(REQUEST_CODE_ARRAY[7])) /* Player Field Data */ {
            int matchId = Integer.parseInt(requestTokenized.nextToken());
            MatchManager.addFieldData(matchId, sessionId, requestTokenized.nextToken(""));
            return true;
        }
        return false;
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

        return RequestManager.redirectRequest(code, requestTokenized, sessionId);
    }

    /**
     * Executes the LOGIN code - checks whether the sent credentials are correct
     *
     * @param requestTokenized LOGIN parameters
     * @param sessionId id of the session
     * @return the status of LOGIN execution
     * @throws SQLException on incorrect sql query data input
     */
    private static boolean logIn(StringTokenizer requestTokenized, int sessionId) throws SQLException {
        String login = requestTokenized.nextToken();
        boolean execStatus = execStatus = DBQueryManager.areLoginCredentialsValid(login, requestTokenized.nextToken());
        if (!execStatus)
            ResponseManager.processResponse("INCORRECT_CREDENTIALS " + login, sessionId);
        else
            ResponseManager.processResponse("CORRECT_CREDENTIALS " + login, sessionId);
        return execStatus;
    }

    /**
     * Executes the LOGOUT code - disconnects the session
     *
     * @param sessionId id of the session
     * @return the status of LOGOUT execution
     */
    private static boolean logOut(int sessionId) {
        SessionManager.shutdownSession(sessionId);
        return true;
    }

    /**
     * Executes the GET_USER_DATA code - gets data about the specified user and if the session is not initialized with user data the extracted data will be used for it
     *
     * @param requestTokenized GET_USER_DATA parameters
     * @param sessionId id of the session
     * @return the status of GET_USER_DATA execution
     * @throws SQLException on incorrect sql query data input
     */
    private static boolean getUserData(StringTokenizer requestTokenized, int sessionId) throws SQLException {
        // TODO: Nested MySQL query
        String username = requestTokenized.nextToken();
        ResultSet rs = DBQueryManager.runSQLQuerry("SELECT user_id FROM `TetrisMP`.`users` WHERE "
                + "username=\"" + username + "\"");
        rs.next();
        String id = rs.getString("user_id");
        rs = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`user_game_data` WHERE "
                + "user_id=" + id);
        rs.next();

        Session s = SessionManager.getSession(sessionId);
        if (s.getDbUserNameId() == 0) {
            s.setDbUsernameId(Integer.parseInt(id));
            s.setElo(rs.getInt("elo"));
            s.setPrivilegeGroup(rs.getInt("privilege_group"));
            s.setUnrankedWins(rs.getInt("unranked_wins"));
            s.setUnrankedLosses(rs.getInt("unranked_losses"));
            s.setRankedWins(rs.getInt("ranked_wins"));
            s.setRankedLosses(rs.getInt("ranked_losses"));
            s.setTetrominoPoints(rs.getLong("tetromino_points"));
            s.setTimePlayed(rs.getLong("time_played"));
        }

        return ResponseManager.processResponse("SEND_USER_DATA " + rs.getString("elo")
                + " " + rs.getString("privilege_group") + " " + rs.getString("unranked_wins")
                + " " + rs.getString("unranked_losses") + " " + rs.getString("ranked_wins")
                + " " + rs.getString("ranked_losses") + " " + rs.getString("tetromino_points")
                + " " + rs.getString("time_played"), sessionId);
    }

    /**
     * Executes the GAME_SEARCH code
     *
     * @param sessionId id of the session
     * @return the status of GAME_SEARCH execution
     * @throws SQLException on incorrect sql query data input
     */
    private static boolean gameSearch(int sessionId) throws SQLException {
        ResultSet rs = DBQueryManager.runSQLQuerry("SELECT elo FROM `TetrisMP`.`user_game_data`"
                + " WHERE user_id=" + SessionManager.getSession(sessionId).getDbUserNameId());
        rs.next();
        MatchManager.addMatchTask("GAME_SETUP", sessionId + " "
                + rs.getString("elo"), System.currentTimeMillis() / 1000);
        return true;
    }

    /**
     * Executes the GET_UPDATE_LOGS code
     *
     * @param sessionId id of the session
     * @return the status of GET_UPDATE_LOGS execution
     * @throws SQLException on incorrect sql query data input
     */
    private static boolean getUpdateLogs(int sessionId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSet news = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`update_logs` ORDER BY "
                + "update_log_date DESC LIMIT 5");
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
    }
}
