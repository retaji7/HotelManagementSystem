package javafxtesting.exceptions;

/**
 * Custom exception for invalid reservation errors
 * Thrown when reservation validation fails (invalid dates, booking conflicts, etc.)
 */
public class InvalidReservationException extends Exception {

    /**
     * Constructor with error message
     * @param message Description of the reservation error
     */
    public InvalidReservationException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     * @param message Description of the reservation error
     * @param cause The underlying cause of the exception
     */
    public InvalidReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
