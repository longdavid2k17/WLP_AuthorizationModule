package pl.com.kantoch.authorizationmodule.exceptions;

import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;

public class NoSuchRoleException extends Exception {
    public NoSuchRoleException(ERole role) {
        super(role.name()+" has not been found in database!");
    }
}
