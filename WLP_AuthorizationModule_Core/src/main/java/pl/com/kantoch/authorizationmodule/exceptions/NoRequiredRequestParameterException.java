package pl.com.kantoch.authorizationmodule.exceptions;

import java.util.Collection;

public class NoRequiredRequestParameterException extends Exception{
    public NoRequiredRequestParameterException(String requestName, Collection<String> parameters) {
        super("Cannot proceed this request ["+requestName+"] because of missing parameter/s : "+parameters.toString());
    }
}
