package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.AuthorityCheckRequest;
import pl.com.kantoch.authorizationmodule.services.AuthorityCheckService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authority")
@CrossOrigin("*")
public class AuthorityCheckResource {

    private final AuthorityCheckService authorityCheckService;

    public AuthorityCheckResource(AuthorityCheckService authorityCheckService) {
        this.authorityCheckService = authorityCheckService;
    }

    @GetMapping
    public ResponseEntity<?> checkAuthority(@RequestBody AuthorityCheckRequest authorityCheckRequest, HttpServletRequest httpServletRequest){
        return authorityCheckService.checkAuthority(authorityCheckRequest,httpServletRequest);
    }
}
