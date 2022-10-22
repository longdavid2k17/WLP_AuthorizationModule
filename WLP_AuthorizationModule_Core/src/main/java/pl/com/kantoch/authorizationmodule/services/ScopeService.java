package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Scope;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.exceptions.*;
import pl.com.kantoch.authorizationmodule.repositories.ScopeRepository;
import pl.com.kantoch.payload.request.DirectiveRequest;
import pl.com.kantoch.payload.request.RequestContextEnum;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class ScopeService {
    private final Logger LOGGER = LoggerFactory.getLogger(ScopeService.class);

    private final ScopeRepository scopeRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PrivilegesService privilegesService;
    private final PermissionService permissionService;

    public ScopeService(ScopeRepository scopeRepository, UserRepository userRepository, JWTUtil jwtUtil, PrivilegesService privilegesService, PermissionService permissionService) {
        this.scopeRepository = scopeRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.privilegesService = privilegesService;
        this.permissionService = permissionService;
    }

    public Collection<Scope> getAllScopes() {
        return scopeRepository.findAll();
    }

    public Scope getScope(Long id){
        Optional<Scope> optionalScope = scopeRepository.findById(id);
        return optionalScope.orElse(null);
    }

    public Scope getScope(String name){
        Optional<Scope> optionalScope = scopeRepository.findByName(name);
        return optionalScope.orElse(null);
    }

    @Transactional
    public Scope createScope(DirectiveRequest createScopeRequest, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException {
        if(createScopeRequest.getRequestContext()!= RequestContextEnum.SCOPE) throw new UnsupportedOperationException();
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token, ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Scope scope = new Scope();
        scope.setDescription(createScopeRequest.getDescription());
        scope.setEnabled(createScopeRequest.getEnabled());
        scope.setName(createScopeRequest.getName());
        scope.setDisplayName(createScopeRequest.getDisplayName());
        scope.setCreationUser(optionalUser.get());
        scope.setCreationDate(LocalDateTime.now());
        scope.setModificationUser(optionalUser.get());
        scope.setModificationDate(LocalDateTime.now());
        Scope createdScope = scopeRepository.save(scope);
        LOGGER.info("Scope {} has been created",createdScope.getName());
        return createdScope;
    }

    @Transactional
    public Scope modifyScope(DirectiveRequest modifyScopeRequest,HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchScopeException {
        if(modifyScopeRequest.getRequestContext()!= RequestContextEnum.SCOPE) throw new UnsupportedOperationException();
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token, ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Scope scope = getScope(modifyScopeRequest.getId());
        if(scope==null) throw new NoSuchScopeException(modifyScopeRequest.getId());
        scope.setDescription(modifyScopeRequest.getDescription());
        scope.setEnabled(modifyScopeRequest.getEnabled());
        scope.setName(modifyScopeRequest.getName());
        scope.setDisplayName(modifyScopeRequest.getDisplayName());
        scope.setModificationUser(optionalUser.get());
        scope.setModificationDate(LocalDateTime.now());
        Scope createdScope = scopeRepository.save(scope);
        LOGGER.info("Scope {} has been modified",createdScope.getName());
        return createdScope;
    }

    @Transactional
    public void addScopesPermission(Long scopeId,Long permissionId,HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchPermissionException, NoSuchScopeException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Permission permission = permissionService.getPermission(permissionId);
        if(permission==null) throw new NoSuchPermissionException(permissionId);
        Scope scope = getScope(scopeId);
        if(scope==null) throw new NoSuchScopeException(scopeId);
        try {
            scope.getPermissionSet().add(permission);
            scope.setModificationDate(LocalDateTime.now());
            scope.setModificationUser(optionalLoggedUser.get());
            scopeRepository.save(scope);
        }
        catch (Exception e){
            LOGGER.error("Error during saving scope changes. Permission ID = {}, Scope ID = {}. Exception: {}",permissionId,scopeId,e.getMessage());
        }
    }

    public void removeGrantPermission(Long scopeId, Long permissionId, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchPermissionException, NoSuchScopeException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Permission permission = permissionService.getPermission(permissionId);
        if(permission==null) throw new NoSuchPermissionException(permissionId);
        Scope scope = getScope(scopeId);
        if(scope==null) throw new NoSuchScopeException(scopeId);
        try {
            scope.getPermissionSet().remove(permission);
            scope.setModificationDate(LocalDateTime.now());
            scope.setModificationUser(optionalLoggedUser.get());
            scopeRepository.save(scope);
        }
        catch (Exception e){
            LOGGER.error("Error during saving scope changes. Permission ID = {}, Scope ID = {}. Exception: {}",permissionId,scopeId,e.getMessage());
        }
    }

    public Collection<Permission> getAllPermissionsGrantedToScope(Long id) throws NoSuchScopeException {
        Scope scope = getScope(id);
        if(scope==null) throw new NoSuchScopeException(id);
        return scope.getPermissionSet();
    }

    public Collection<Permission> getAllPermissionsGrantedToScope(String name) throws NoSuchScopeException {
        Scope scope = getScope(name);
        if(scope==null) throw new NoSuchScopeException(name);
        return scope.getPermissionSet();
    }

    @Transactional
    public Scope enableScope(Long scopeId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchScopeException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Scope> optionalScope = scopeRepository.findById(scopeId);
        if(optionalScope.isEmpty()) throw new NoSuchScopeException(scopeId);
        Scope scope = optionalScope.get();
        scope.setEnabled(true);
        return scopeRepository.save(scope);
    }

    @Transactional
    public Scope disableScope(Long scopeId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchScopeException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Scope> optionalScope = scopeRepository.findById(scopeId);
        if(optionalScope.isEmpty()) throw new NoSuchScopeException(scopeId);
        Scope scope = optionalScope.get();
        scope.setEnabled(false);
        return scopeRepository.save(scope);
    }
}
