package management;

import java.sql.*;
import java.util.Scanner;

/**
 * This class manages MySQL server connection and all of the MySQL queries for the MySQL server that should run alongside this server.
 *
 * @author Kornel Domeradzki
 */
public class DBQueryManager {

    /** Connection point to the MySQL Database */
    private static Connection mySQLConnection;

    /** Target MySQL database name */
    private String dbName = "TetrisMP";

    /** MySQL database login */
    private static String username;

    /** MySQL database password */
    private static String password = "";

    /**
     * DBQueryManager constructor
     *
     * @param dbName name of the database
     *
     * @param username mysql database login
     *
     * @param password mysql database password
     */
    public DBQueryManager(String dbName, String username, String password) {
        this.dbName = dbName;
        DBQueryManager.username = username;
        DBQueryManager.password = password;
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?user=" + username + "&password=" + password
                + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            DBQueryManager.mySQLConnection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("[Could not connect to the MySQL server]");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Default DBQueryManager constructor with automated login interface
     */
    public DBQueryManager() {
        Scanner sc = new Scanner(System.in);
        System.out.println("[Connecting to the database]");
        System.out.print("Database username: ");
        DBQueryManager.username = sc.nextLine();
        System.out.print("Database password: ");
        DBQueryManager.password = sc.nextLine();
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?user=" + DBQueryManager.username + "&password=" + DBQueryManager.password
            + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            DBQueryManager.mySQLConnection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("[Could not connect to the MySQL server]");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Gets the result set of a custom sql query
     *
     * @param sqlQuery content of the MySQL query
     * @return returns the result set of the query
     * @throws SQLException on incorrect query
     */
    public static ResultSet runSQLQuerry(String sqlQuery) throws SQLException {
        Statement myStmt = DBQueryManager.mySQLConnection.createStatement();
        return  myStmt.executeQuery(sqlQuery);
    }

    public static void runSQLueryNoRet(String sqlQuery) throws SQLException {
        PreparedStatement ps = DBQueryManager.mySQLConnection.prepareStatement(sqlQuery);
        ps.executeUpdate();
    }

    /**
     * Checks whether the passed game username credentials are correct
     *
     * @param login game login
     * @param passwd game password
     * @return true if the credentials are correct
     * @throws SQLException on incorrect query or when illegal characters are passed
     */
    public static boolean areLoginCredentialsValid(String login, String passwd) throws SQLException {
        String query = "SELECT user_id FROM `TetrisMP`.`users` WHERE username='"
                + login + "' AND user_password='" + passwd + "'";
        ResultSet myRs = DBQueryManager.runSQLQuerry(query);
        return myRs.next();
    }

    /**
     * Gets the currently used MySQL Database username
     *
     * @return mysql database username
     */
    public static String getUsername() {
        return DBQueryManager.username;
    }
}
