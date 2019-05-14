package management;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class ResponseManager {

    private final static String[] REQUEST_CODE_ARRAY = {
            "MAINTENANCE_SHUTDOWN",
            "SUDDEN_SHUTDOWN",
            "INCORRECT_CREDENTIALS",
            "CORRECT_CREDENTIALS",
            "GAME_SETUP",
            "SEND_USER_DATA",
            "SEND_UPDATE_LOGS"
    };

    private static boolean containsCode(String code) {
        for (String str : REQUEST_CODE_ARRAY) {
            if (str.equals(code))
                return true;
        }
        return false;
    }

    private static boolean redirectResponse(String code, StringTokenizer responseTokenizer, int sessionId) throws SQLException {
        if (code.equals(REQUEST_CODE_ARRAY[0])) { // Maintenance shutdown
            System.out.println("[Maintenance shutdown response not implemented yet]");
        } else if (code.equals(REQUEST_CODE_ARRAY[1])) { // Sudden shutdown
            System.out.println("[Sudden shutdown response not implemented yet]");
        } else if (code.equals(REQUEST_CODE_ARRAY[2])) { // Incorrect credentials
            SessionManager.sendStringData(REQUEST_CODE_ARRAY[2] + " " + responseTokenizer.nextToken(), sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[3])) { // Correct credentials
            String username = responseTokenizer.nextToken();
            ResultSet rs = DBQueryManager.runSQLQuerry("SELECT user_id FROM `TetrisMP`.`users` WHERE username=\""
                            + username + "\"");
            rs.next();
            SessionManager.assignDbId(sessionId, rs.getInt("user_id"));
            SessionManager.sendStringData(REQUEST_CODE_ARRAY[3] + " " + username, sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[4])) { // Game set up
            // TODO: Put the opponent data in here
            SessionManager.sendStringData(REQUEST_CODE_ARRAY[4] + " ", sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[5])) { // Send User Data
            SessionManager.sendStringData(code + responseTokenizer.nextToken(""), sessionId);
        } else if (code.equals(REQUEST_CODE_ARRAY[6])) { // Send Update Logs
            SessionManager.sendStringData(code + responseTokenizer.nextToken(""), sessionId);
        }

        return true;
    }


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
