package management;

import connection.SessionManager;

import java.sql.SQLException;
import java.util.StringTokenizer;

public class RequestManager {

    private final static String[] REQUEST_CODE_ARRAY = {
            "LOGIN",
            "LOGOUT",
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

    private static boolean redirectRequest(String code, StringTokenizer requestTokenized, int userId) throws SQLException {
        if (code.equals(REQUEST_CODE_ARRAY[0])) { // log in
            String login = requestTokenized.nextToken();
            boolean execStatus = DBQueryManager.areLoginCredentialsValid(login, requestTokenized.nextToken());
            if (!execStatus)
                ResponseManager.processResponse("INCORRECT_CREDENTIALS " + login, userId);
            else
                ResponseManager.processResponse("CORRECT_CREDENTIALS " + login, userId);
            return execStatus;
        } else if (code.equals(REQUEST_CODE_ARRAY[1])) { // log out
            SessionManager.shutdownSession(userId);
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
