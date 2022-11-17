package pl.com.kantoch.authorizationmodule.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.kantoch.authorizationmodule.configuration.dictionary_services.RoleDictionaryService;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;

import java.util.Collection;

@RestController
@RequestMapping("/api/role")
@CrossOrigin("*")
public class RoleResource {
    private final RoleDictionaryService roleService;

    public RoleResource(RoleDictionaryService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/all")
    public Collection<Role> queryRoles() {
        return roleService.getAllRoles();
    }
}
