package pl.com.kantoch.authorizationmodule.exceptions;

public class NoSuchUserException extends Exception {
    public NoSuchUserException(String username) {
        super("User with username '"+username+"' has not been found!");
    }

    public NoSuchUserException(Long id) {
        super("User with ID '"+id+"' has not been found!");
    }
}
