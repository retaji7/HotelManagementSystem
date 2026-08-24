package javafxtesting.exceptions;

/**
 * Custom exception for database-related errors
 * Thrown when database operations fail (connection, query execution, etc.)
 */
public class DatabaseException extends Exception {

    /**
     * Constructor with error message
     * @param message Description of the database error
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     * @param message Description of the database error
     * @param cause The underlying cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
