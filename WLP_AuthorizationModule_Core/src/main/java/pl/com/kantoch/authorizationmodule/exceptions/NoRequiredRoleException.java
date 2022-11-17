package pl.com.kantoch.authorizationmodule.exceptions;

import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;

public class NoRequiredRoleException extends Exception {
    public NoRequiredRoleException(String username, ERole role) {
        super("User "+username+" has not required role to perform this action. Expected role: "+role.name());
    }

    public NoRequiredRoleException(String username, ERole[] roles) {
        super("User "+username+" has not required role to perform this action. Expected at least one of the role: "+roles);
    }
}
