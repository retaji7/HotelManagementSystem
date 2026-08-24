package javafxtesting.exceptions;

/**
 * Custom exception for login-related errors
 * Thrown when authentication fails (invalid credentials, user not found, etc.)
 */
public class LoginException extends Exception {

    /**
     * Constructor with error message
     * @param message Description of the login error
     */
    public LoginException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     * @param message Description of the login error
     * @param cause The underlying cause of the exception
     */
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
