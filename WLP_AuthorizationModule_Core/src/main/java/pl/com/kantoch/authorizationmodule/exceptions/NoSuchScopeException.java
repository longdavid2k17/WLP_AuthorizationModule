package pl.com.kantoch.authorizationmodule.exceptions;

import pl.com.kantoch.authorizationmodule.configuration.payload.requests.AuthorityCheckRequest;

public class NoSuchScopeException extends Exception{
    public NoSuchScopeException(String name) {
        super("No such scope as '"+name+"' has been found! Check naming and query again");
    }

    public NoSuchScopeException(AuthorityCheckRequest request) {
        super("No such scope as '"+request.getScopeName()+"'/'"+request.getScopeId()+"' has been found! Check naming and query again");
    }

    public NoSuchScopeException(Long scopeId) {
        super("No such scope with ID="+scopeId+" has been found!");
    }
}
