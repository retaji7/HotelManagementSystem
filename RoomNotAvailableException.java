package javafxtesting.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafxtesting.exceptions.DatabaseException;

/**
 * Manages database connections for the Hotel Reservation System
 * Uses Oracle Database with JDBC
 */
public class DatabaseConnection {

    // Database connection parameters
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "1234";
    private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

    // Singleton connection instance
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
    }

    /**
     * Get database connection (creates new connection if not exists)
     * @return Connection object
     * @throws DatabaseException if connection fails
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            // Load Oracle JDBC driver
            if (connection == null || connection.isClosed()) {
                Class.forName(DB_DRIVER);
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection established successfully.");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Oracle JDBC Driver not found. Please add ojdbc8.jar to lib folder.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (DatabaseException | SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
