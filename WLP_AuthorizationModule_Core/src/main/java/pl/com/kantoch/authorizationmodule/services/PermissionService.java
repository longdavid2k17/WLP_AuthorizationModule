package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchPermissionException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.authorizationmodule.repositories.PermissionRepository;
import pl.com.kantoch.payload.request.DirectiveRequest;
import pl.com.kantoch.payload.request.RequestContextEnum;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class PermissionService {
    private final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PrivilegesService privilegesService;

    public PermissionService(PermissionRepository permissionRepository, UserRepository userRepository,
                             JWTUtil jwtUtil, PrivilegesService privilegesService) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.privilegesService = privilegesService;
    }

    public Collection<Permission> getPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermission(Long id){
        Optional<Permission> optionalPermission = permissionRepository.findPermissionById(id);
        return optionalPermission.orElse(null);
    }

    public Permission getPermission(String name){
        Optional<Permission> optionalPermission = permissionRepository.findPermissionByName(name);
        return optionalPermission.orElse(null);
    }

    public Collection<User> getAllUsersWithGrantedPermission(String name) throws NoSuchPermissionException {
        Permission permission = getPermission(name);
        if(permission==null) throw new NoSuchPermissionException(name);
        return permission.getUserSet();
    }

    public Collection<User> getAllUsersWithGrantedPermission(Long id) throws NoSuchPermissionException {
        Permission permission = getPermission(id);
        if(permission==null) throw new NoSuchPermissionException(id);
        return permission.getUserSet();
    }

    @Transactional
    public Permission createPermission(DirectiveRequest permissionRequest, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException {
        if(permissionRequest.getRequestContext()!= RequestContextEnum.PERMISSION) throw new UnsupportedOperationException();
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Permission permission = new Permission();
        permission.setDescription(permissionRequest.getDescription());
        permission.setEnabled(permissionRequest.getEnabled());
        permission.setName(permissionRequest.getName());
        permission.setDisplayName(permissionRequest.getDisplayName());
        permission.setCreationUser(optionalUser.get());
        permission.setCreationDate(LocalDateTime.now());
        permission.setModificationUser(optionalUser.get());
        permission.setModificationDate(LocalDateTime.now());
        Permission createdPermission = permissionRepository.save(permission);
        LOGGER.info("Permission {} has been created",permission.getName());
        return createdPermission;
    }

    @Transactional
    public Permission modifyPermission(DirectiveRequest permissionRequest, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchPermissionException {
        if(permissionRequest.getRequestContext()!= RequestContextEnum.PERMISSION) throw new UnsupportedOperationException();
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Permission permission = getPermission(permissionRequest.getId());
        if(permission==null) throw new NoSuchPermissionException(permissionRequest.getId());
        permission.setEnabled(permissionRequest.getEnabled());
        permission.setName(permissionRequest.getName());
        permission.setDisplayName(permissionRequest.getDisplayName());
        permission.setDescription(permissionRequest.getDescription());
        permission.setModificationUser(optionalUser.get());
        permission.setModificationDate(LocalDateTime.now());
        Permission modifiedPermission = permissionRepository.save(permission);
        LOGGER.info("Permission {} has been modified",permission.getName());
        return modifiedPermission;
    }

    @Transactional
    public void addUsersPermission(Long permissionId,Long userId,HttpServletRequest httpServletRequest) throws Exception {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Permission> optionalPermission = permissionRepository.findPermissionById(permissionId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalPermission.isPresent() && optionalUser.isPresent()){
            Permission targetPermission = optionalPermission.get();
            User targetUser = optionalUser.get();
            targetPermission.setModificationUser(optionalLoggedUser.get());
            targetPermission.setModificationDate(LocalDateTime.now());
            targetPermission.getUserSet().add(targetUser);
            permissionRepository.save(targetPermission);
        } else throw new Exception("No user found or no permission found!");
    }

    @Transactional
    public void removeGrantPermission(Long permissionId,Long userId,HttpServletRequest httpServletRequest) throws Exception {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Permission> optionalPermission = permissionRepository.findPermissionById(permissionId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalPermission.isPresent() && optionalUser.isPresent()){
            Permission targetPermission = optionalPermission.get();
            User targetUser = optionalUser.get();
            targetPermission.setModificationUser(optionalLoggedUser.get());
            targetPermission.setModificationDate(LocalDateTime.now());
            targetPermission.getUserSet().remove(targetUser);
            permissionRepository.save(targetPermission);
        } else throw new Exception("No user found or no permission found!");
    }

    public Set<Permission> getUserPermissions(User user){
        return permissionRepository.getAllPermissionsContainingUser(user);
    }

    @Transactional
    public Permission enablePermission(Long permissionId, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchPermissionException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Permission> optionalPermission = permissionRepository.findPermissionById(permissionId);
        if(optionalPermission.isEmpty()) throw new NoSuchPermissionException(permissionId);
        Permission permission = optionalPermission.get();
        permission.setEnabled(true);
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission disablePermission(Long permissionId, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException, NoSuchPermissionException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        Optional<User> optionalLoggedUser = userRepository.findByUsername(username);
        if(optionalLoggedUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<Permission> optionalPermission = permissionRepository.findPermissionById(permissionId);
        if(optionalPermission.isEmpty()) throw new NoSuchPermissionException(permissionId);
        Permission permission = optionalPermission.get();
        permission.setEnabled(false);
        return permissionRepository.save(permission);
    }
}
