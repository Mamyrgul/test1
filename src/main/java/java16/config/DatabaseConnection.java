package java16.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String url = "jdbc:postgresql://localhost:5432/java16";
    private static final String user = "postgres";
    private static final String password = "1234";

    public static Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
