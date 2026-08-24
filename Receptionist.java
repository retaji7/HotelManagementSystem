package javafxtesting.interfaces;

import javafxtesting.exceptions.LoginException;

/**
 * Interface for classes that support authentication functionality
 * Implemented by User class and its subclasses
 */
public interface Authenticatable {

    /**
     * Authenticate user with username and password
     * @param username The username to authenticate
     * @param password The password to verify
     * @return true if authentication successful, false otherwise
     * @throws LoginException if authentication fails
     */
    boolean authenticate(String username, String password) throws LoginException;

    /**
     * Logout the current user
     * Clears any session data or user context
     */
    void logout();
}
