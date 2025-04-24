package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseManager provides a single point of access to the PostgreSQL database connection.
 * Used throughout the application to retrieve a live connection to the expense_db.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/expense_db";
    private static final String USER = "emmanuel";
    private static final String PASSWORD = "1234";

    /**
     * Returns a new connection to the PostgreSQL database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
