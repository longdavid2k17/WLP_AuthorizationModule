package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Scope;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.exceptions.*;
import pl.com.kantoch.authorizationmodule.services.ScopeService;
import pl.com.kantoch.payload.request.DirectiveRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequestMapping("/authentication/scopes")
public class ScopeResource {
    private final ScopeService scopeService;

    public ScopeResource(ScopeService scopeService) {
        this.scopeService = scopeService;
    }

    @GetMapping
    public Collection<Scope> getScopes() {
        return scopeService.getAllScopes();
    }

    @GetMapping("/id/{id}")
    public Scope getScopeById(@PathVariable Long id) {
        return scopeService.getScope(id);
    }

    @GetMapping("/name/{name}")
    public Scope getScopeByName(@PathVariable String name) {
        return scopeService.getScope(name);
    }

    @GetMapping("/name/{name}/permissions")
    public Collection<Permission> getAllPermissionsGrantedToScope(@PathVariable String name) throws NoSuchPermissionException, NoSuchScopeException {
        return scopeService.getAllPermissionsGrantedToScope(name);
    }

    @GetMapping("/id/{id}/permissions")
    public Collection<Permission> getAllPermissionsGrantedToScope(@PathVariable Long id) throws NoSuchPermissionException, NoSuchScopeException {
        return scopeService.getAllPermissionsGrantedToScope(id);
    }

    @PostMapping
    public Scope createScope(@RequestBody DirectiveRequest createScopeRequest, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException {
        return scopeService.createScope(createScopeRequest,httpServletRequest);
    }

    @PutMapping
    public Scope modifyScope(@RequestBody DirectiveRequest modifyScopeRequest, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchScopeException {
        return scopeService.modifyScope(modifyScopeRequest,httpServletRequest);
    }

    @PutMapping("/enable")
    public Scope enableScope(@RequestParam Long scopeId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchPermissionException, NoSuchScopeException {
        return scopeService.enableScope(scopeId,httpServletRequest);
    }

    @PutMapping("/disable")
    public Scope disableScope(@RequestParam Long scopeId, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, NoSuchPermissionException, NoSuchScopeException {
        return scopeService.disableScope(scopeId,httpServletRequest);
    }

    @PutMapping("/grant-scope-permission")
    public void grantScopePermission(@RequestParam Long scopeId, @RequestParam Long permissionId, HttpServletRequest httpServletRequest) throws Exception {
        scopeService.addScopesPermission(scopeId,permissionId,httpServletRequest);
    }

    @PutMapping("/remove-grant-permission")
    public void removeGrantScopePermission(@RequestParam Long scopeId,@RequestParam Long permissionId,HttpServletRequest httpServletRequest) throws Exception {
        scopeService.removeGrantPermission(scopeId,permissionId,httpServletRequest);
    }
}
