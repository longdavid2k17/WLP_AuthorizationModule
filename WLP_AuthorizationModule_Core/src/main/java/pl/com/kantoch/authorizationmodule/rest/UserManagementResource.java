package pl.com.kantoch.authorizationmodule.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.GrantRolesRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.response.UserDTO;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.authorizationmodule.services.AccountDeactivationService;
import pl.com.kantoch.authorizationmodule.services.AuthorizationService;
import pl.com.kantoch.authorizationmodule.services.UserManagementService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;

@RestController
@RequestMapping("/api/user/management")
@CrossOrigin("*")
public class UserManagementResource {

    private final AccountDeactivationService accountDeactivationService;
    private final AuthorizationService authorizationService;
    private final UserManagementService userManagementService;

    public UserManagementResource(AccountDeactivationService accountDeactivationService, AuthorizationService authorizationService, UserManagementService userManagementService) {
        this.accountDeactivationService = accountDeactivationService;
        this.authorizationService = authorizationService;
        this.userManagementService = userManagementService;
    }

    @PutMapping("/deactivate")
    @ApiOperation(value = "Deactivates user account")
    public void deactivateUserAccount(@RequestParam Long userId, @RequestParam String deactivationReason, HttpServletRequest request) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, IOException, InterruptedException {
        accountDeactivationService.deactivateUser(userId,deactivationReason,request);
    }

    @PostMapping("/grant-role")
    @ApiOperation(value = "Grants user requested role", notes = "If only token is correct, and request author has  authority for performing action, it will grant selected role for target user")
    public void grantRole(@NotEmpty @RequestParam String targetUsername, @NotEmpty @RequestParam ERole role, HttpServletRequest request) throws Exception {
        authorizationService.grantRole(targetUsername,role,request);
    }

    @PostMapping("/grant-roles")
    @ApiOperation(value = "Grants user requested roles", notes = "If only token is correct, and request author has  authority for performing action, it will grant selected roles for target user")
    public void grantRoles(@RequestBody GrantRolesRequest grantRolesRequest, HttpServletRequest request) throws Exception {
        authorizationService.grantRoles(grantRolesRequest,request);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editAccountData(@RequestBody UserDTO userDTO, HttpServletRequest httpServletRequest) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException {
        return userManagementService.editAccountData(userDTO,httpServletRequest);
    }
}
