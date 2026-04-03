package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private DBConnection() {
        // Utility class
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Add mysql-connector-j-8.3.0.jar to utils folder and run with classpath .;utils/*",
                    e
            );
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
