package pl.com.kantoch.authorizationmodule.exceptions;

public class NoSuchPasswordResetRequestException extends Exception{
    public NoSuchPasswordResetRequestException(String message) {
        super(message);
    }
}
