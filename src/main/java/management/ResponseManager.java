package management;

import connection.SessionManager;

import java.util.StringTokenizer;

public class ResponseManager {

    private final static String[] REQUEST_CODE_ARRAY = {
            "MAINTENANCE_SHUTDOWN",
            "SUDDEN_SHUTDOWN",
            "INCORRECT_CREDENTIALS",
            "CORRECT_CREDENTIALS"
    };

    private static boolean containsCode(String code) {
        for (String str : REQUEST_CODE_ARRAY) {
            if (str.equals(code))
                return true;
        }
        return false;
    }

    private static boolean redirectResponse(String code, StringTokenizer responseTokenizer, int userId) {
        if (code.equals(REQUEST_CODE_ARRAY[0])) { // Maintenance shutdown
            System.out.println("[Maintenance shutdown response not implemented yet]");
        } else if (code.equals(REQUEST_CODE_ARRAY[1])) { // Sudden shutdown
            System.out.println("[Sudden shutdown response not implemented yet]");
        } else if (code.equals(REQUEST_CODE_ARRAY[2])) { // Incorrect credentials
            SessionManager.sendStringData(REQUEST_CODE_ARRAY[2] + " " + responseTokenizer.nextToken(), userId);
        } else if (code.equals(REQUEST_CODE_ARRAY[3])) { // Correct credentials
            SessionManager.sendStringData(REQUEST_CODE_ARRAY[3] + " " + responseTokenizer.nextToken(), userId);
        }

        return true;
    }


    public static boolean processResponse(String responseData, int sessionId) {
        StringTokenizer responseTokenizer = new StringTokenizer(responseData);
        String code = responseTokenizer.nextToken();

        if (!ResponseManager.containsCode(code))
            return false;

        return ResponseManager.redirectResponse(code, responseTokenizer, sessionId);
    }

}
