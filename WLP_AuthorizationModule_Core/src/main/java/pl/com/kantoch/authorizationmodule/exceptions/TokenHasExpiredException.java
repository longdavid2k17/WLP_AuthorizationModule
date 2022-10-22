package pl.com.kantoch.authorizationmodule.exceptions;

public class TokenHasExpiredException extends Exception {
    public TokenHasExpiredException(String message) {
        super(message);
    }
}
