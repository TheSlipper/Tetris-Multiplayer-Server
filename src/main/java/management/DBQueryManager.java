package management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBQueryManager {

    private Connection mySQLConnection;

    private String dbName = "TetrisMP";

    private String url = "jdbc:mysql://localhost:3306/";

    private String username;

    private String password = "";

    public DBQueryManager(String dbName, String username, String password) {
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.url += dbName + "?user=" + this.username + "&password=" + this.password
                + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            this.mySQLConnection = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.err.println("[Could not connect to the MySQL server]");
            e.printStackTrace();
        }
    }

    public DBQueryManager() {
        Scanner sc = new Scanner(System.in);
        System.out.println("[Connecting to the database]");
        System.out.print("Database username: ");
        this.username = sc.nextLine();
        System.out.print("Database password: ");
        this.password = sc.nextLine();
        this.url += dbName + "?user=" + this.username + "&password=" + this.password
            + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            this.mySQLConnection = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.err.println("[Could not connect to the MySQL server]");
            e.printStackTrace();
        }
    }

    public boolean areLoginCredentialsValid(String login, String passwd) {
        System.out.println("Make the logging thing here"); // TODO:
        return true;
    }
}
