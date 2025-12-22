package ph.edu.unc.pageantms.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/PageantManagementSystem";
    private static final String USER = "root";

    private static final String PASS = "admin1234";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }
}