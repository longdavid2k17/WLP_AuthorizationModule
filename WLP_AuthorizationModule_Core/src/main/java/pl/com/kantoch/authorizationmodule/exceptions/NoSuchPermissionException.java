package pl.com.kantoch.authorizationmodule.exceptions;

public class NoSuchPermissionException extends Exception{
    public NoSuchPermissionException(String permissionName) {
        super("No such permission as '"+permissionName+"' has been found! Check naming and query again");
    }

    public NoSuchPermissionException(Long id) {
        super("No such permission with ID="+id+" has been found!");
    }
}
