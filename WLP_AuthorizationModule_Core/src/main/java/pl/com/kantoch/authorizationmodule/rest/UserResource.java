package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.services.UserManagementService;

import java.util.Collection;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserResource {

    private final UserManagementService userManagementService;


    public UserResource(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    //@PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUser(@RequestParam(value = "roles",required = false) Collection<Role> roles) {
        return userManagementService.queryUsers(roles);
    }


    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<?> getStatistics() {
        return userManagementService.getStats();
    }

}
