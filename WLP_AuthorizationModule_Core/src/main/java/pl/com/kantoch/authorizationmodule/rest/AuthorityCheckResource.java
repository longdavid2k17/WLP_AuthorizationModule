package pl.com.kantoch.authorizationmodule.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.AuthorityCheckRequest;
import pl.com.kantoch.authorizationmodule.services.AuthorityCheckService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authority")
public class AuthorityCheckResource {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthorityCheckResource.class);

    private final AuthorityCheckService authorityCheckService;

    public AuthorityCheckResource(AuthorityCheckService authorityCheckService) {
        this.authorityCheckService = authorityCheckService;
    }

    @GetMapping
    public ResponseEntity<?> checkAuthority(@RequestBody AuthorityCheckRequest authorityCheckRequest, HttpServletRequest httpServletRequest){
        return authorityCheckService.checkAuthority(authorityCheckRequest,httpServletRequest);
    }
}
