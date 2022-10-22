package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.AuthorityCheckRequest;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Scope;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRequestParameterException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchScopeException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class AuthorityCheckService {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthorityCheckService.class);

    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final ScopeService scopeService;
    private final JWTUtil jwtUtil;

    public AuthorityCheckService(UserRepository userRepository, PermissionService permissionService, ScopeService scopeService, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.scopeService = scopeService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> checkAuthority(AuthorityCheckRequest authorityCheckRequest, HttpServletRequest httpRequest){
        try {
            Scope scope = getScope(authorityCheckRequest);
            if(scope==null) throw new NoSuchScopeException(authorityCheckRequest);
            Set<Permission> permissionSet = getPermissions(authorityCheckRequest,httpRequest);
            return validate(scope,permissionSet);
        }
        catch (Exception e){
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<?> validate(Scope scope, Set<Permission> permissionSet) {
        for (Permission permission : permissionSet){
            if(scope.getPermissionSet().contains(permission)) return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    private Set<Permission> getPermissions(AuthorityCheckRequest authorityCheckRequest, HttpServletRequest httpRequest) throws NoRequiredRequestParameterException, NoSuchUserException {
        if(authorityCheckRequest.getPermissionId()==null && authorityCheckRequest.getPermissionId()==null) {
            String token = httpRequest.getHeader("Authorization");
            if(token==null) throw new NoRequiredRequestParameterException("AUTHORITY_CHECK", List.of("permissionId","permissionName","Authorization token"));
            if (StringUtils.hasText(token) && token.startsWith("Bearer "))
                token =  token.substring(7);

            String username = jwtUtil.getUsernameFromJwtToken(token);
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
            return permissionService.getUserPermissions(optionalUser.get());
        }
        if(authorityCheckRequest.getPermissionName()!=null)
            return Collections.singleton(permissionService.getPermission(authorityCheckRequest.getPermissionName()));
        return Collections.singleton(permissionService.getPermission(authorityCheckRequest.getPermissionId()));
    }

    private Scope getScope(AuthorityCheckRequest authorityCheckRequest) throws NoRequiredRequestParameterException {
        if(authorityCheckRequest.getScopeId()==null && authorityCheckRequest.getScopeName()==null) throw new NoRequiredRequestParameterException("AUTHORITY_CHECK", List.of("scopeId","scopeName"));
        if(authorityCheckRequest.getScopeId()!=null) return scopeService.getScope(authorityCheckRequest.getScopeId());
        return scopeService.getScope(authorityCheckRequest.getScopeName());
    }
}
