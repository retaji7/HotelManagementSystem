package javafxtesting.exceptions;

/**
 * Custom exception for room availability errors
 * Thrown when attempting to book a room that is not available
 */
public class RoomNotAvailableException extends Exception {

    /**
     * Constructor with error message
     * @param message Description of the availability error
     */
    public RoomNotAvailableException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     * @param message Description of the availability error
     * @param cause The underlying cause of the exception
     */
    public RoomNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
