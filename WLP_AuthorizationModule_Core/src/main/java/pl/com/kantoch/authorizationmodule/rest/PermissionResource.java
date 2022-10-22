package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchPermissionException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.authorizationmodule.services.PermissionService;
import pl.com.kantoch.payload.request.DirectiveRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequestMapping("/authentication/permissions")
public class PermissionResource {
    private final PermissionService permissionService;

    public PermissionResource(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public Collection<Permission> getPermissions() {
        return permissionService.getPermissions();
    }

    @GetMapping("/id/{id}")
    public Permission getPermissionById(@PathVariable Long id) {
        return permissionService.getPermission(id);
    }

    @GetMapping("/name/{name}")
    public Permission getPermissionByName(@PathVariable String name) {
        return permissionService.getPermission(name);
    }

    @GetMapping("/name/{name}/users")
    public Collection<User> getAllUsersWithGrantedPermission(@PathVariable String name) throws NoSuchPermissionException {
        return permissionService.getAllUsersWithGrantedPermission(name);
    }

    @GetMapping("/id/{id}/users")
    public Collection<User> getAllUsersWithGrantedPermission(@PathVariable Long id) throws NoSuchPermissionException {
        return permissionService.getAllUsersWithGrantedPermission(id);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Permission createPermission(@RequestBody DirectiveRequest createPermissionRequest, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException {
        return permissionService.createPermission(createPermissionRequest,httpServletRequest);
    }

    @PutMapping("/enable")
    public Permission enablePermission(@RequestParam Long permissionId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchPermissionException {
        return permissionService.enablePermission(permissionId,httpServletRequest);
    }

    @PutMapping("/disable")
    public Permission disablePermission(@RequestParam Long permissionId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchPermissionException {
        return permissionService.disablePermission(permissionId,httpServletRequest);
    }

    @PutMapping
    public Permission modifyPermission(@RequestBody DirectiveRequest modifyPermissionRequest, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchPermissionException {
        return permissionService.modifyPermission(modifyPermissionRequest,httpServletRequest);
    }

    @PutMapping("/grant-permission")
    public void addUsersPermission(@RequestParam Long permissionId,@RequestParam Long userId,HttpServletRequest httpServletRequest) throws Exception {
        permissionService.addUsersPermission(permissionId,userId,httpServletRequest);
    }

    @PutMapping("/remove-grant-permission")
    public void removeGrantPermission(@RequestParam Long permissionId,@RequestParam Long userId,HttpServletRequest httpServletRequest) throws Exception {
        permissionService.removeGrantPermission(permissionId,userId,httpServletRequest);
    }
}
