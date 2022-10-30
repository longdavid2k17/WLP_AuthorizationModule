package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    @RequestMapping(value = "/anonymous", method = RequestMethod.GET)
    public ResponseEntity<String> getAnonymous() {
        return ResponseEntity.ok("Hello Anonymous");
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getUser() {
        return "Hello User";
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdmin() {
        return "Hello Admin";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/all-user", method = RequestMethod.GET)
    public ResponseEntity<String> getAllUser() {
        return ResponseEntity.ok("Hello All User");
    }
}
