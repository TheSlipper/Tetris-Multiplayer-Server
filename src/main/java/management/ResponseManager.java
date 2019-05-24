package management;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * This class manages outgoing data and finishing tasks.
 *
 */
public class ResponseManager {

    /** List of various response codes */
    private final static String[] RESPONSE_CODE_ARRAY = {
            "MAINTENANCE_SHUTDOWN",
            "SUDDEN_SHUTDOWN",
            "INCORRECT_CREDENTIALS",
            "CORRECT_CREDENTIALS",
            "GAME_SETUP",
            "SEND_USER_DATA",
            "SEND_UPDATE_LOGS"
    };

    /**
     * Checks whether the response code is in the response code array
     *
     * @param code codename
     * @return true if there is such a code
     */
    private static boolean containsCode(String code) {
        for (String str : RESPONSE_CODE_ARRAY) {
            if (str.equals(code))
                return true;
        }
        return false;
    }

    /**
     * Redirects the response to a specific method/class to be executed or executes the response inside of its body
     *
     * @param code codename of the response
     * @param responseTokenized tokenized request arguments/data
     * @param sessionId session id of the response receiver
     * @return returns the execution status
     * @throws SQLException on incorrect query or response
     */
    private static boolean redirectResponse(String code, StringTokenizer responseTokenized, int sessionId) throws SQLException {
        if (code.equals(RESPONSE_CODE_ARRAY[0])) { // Maintenance shutdown
            System.out.println("[Maintenance shutdown response not implemented yet]");
        } else if (code.equals(RESPONSE_CODE_ARRAY[1])) { // Sudden shutdown
            System.out.println("[Sudden shutdown response not implemented yet]");
        } else if (code.equals(RESPONSE_CODE_ARRAY[2])) { // Incorrect credentials
            SessionManager.sendStringData(RESPONSE_CODE_ARRAY[2] + " " + responseTokenized.nextToken(), sessionId);
        } else if (code.equals(RESPONSE_CODE_ARRAY[3])) { // Correct credentials
            String username = responseTokenized.nextToken();
            ResultSet rs = DBQueryManager.runSQLQuerry("SELECT user_id FROM `TetrisMP`.`users` WHERE username=\""
                            + username + "\"");
            rs.next();
            SessionManager.assignDbId(sessionId, rs.getInt("user_id"));
            SessionManager.sendStringData(RESPONSE_CODE_ARRAY[3] + " " + username, sessionId);
        } else if (code.equals(RESPONSE_CODE_ARRAY[4])) { // Game set up
            SessionManager.sendStringData(RESPONSE_CODE_ARRAY[4] + responseTokenized.nextToken(""), sessionId);
        } else if (code.equals(RESPONSE_CODE_ARRAY[5])) { // Send User Data
            SessionManager.sendStringData(code + responseTokenized.nextToken(""), sessionId);
        } else if (code.equals(RESPONSE_CODE_ARRAY[6])) { // Send Update Logs
            SessionManager.sendStringData(code + responseTokenized.nextToken(""), sessionId);
        }

        return true;
    }

    /**
     * Public wrapper of multiple methods for handling responses in one method
     *
     * @param responseData string data of the response
     * @param sessionId session id of the request receiver
     * @return execution status of the response
     */
    public static boolean processResponse(String responseData, int sessionId) {
        StringTokenizer responseTokenizer = new StringTokenizer(responseData);
        String code = responseTokenizer.nextToken();

        if (!ResponseManager.containsCode(code))
            return false;

        try {
            return ResponseManager.redirectResponse(code, responseTokenizer, sessionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
