package pl.com.kantoch.authorizationmodule.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.GrantRolesRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.LoginRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SetNewPasswordRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SignUpRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.response.UserResponse;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnRegistrationCompleteEvent;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.authorizationmodule.services.AccountDeactivationService;
import pl.com.kantoch.authorizationmodule.services.AuthorizationService;
import pl.com.kantoch.authorizationmodule.services.PasswordResetService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;

@RestController
@RequestMapping("/authorization")
@CrossOrigin("*")
public class AuthorizationResource {

    private final AuthorizationService authorizationService;
    private final PasswordResetService passwordResetService;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountDeactivationService accountDeactivationService;

    public AuthorizationResource(AuthorizationService authorizationService,
                                 PasswordResetService passwordResetService,
                                 ApplicationEventPublisher eventPublisher,
                                 AccountDeactivationService accountDeactivationService) {
        this.authorizationService = authorizationService;
        this.passwordResetService = passwordResetService;
        this.eventPublisher = eventPublisher;
        this.accountDeactivationService = accountDeactivationService;
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "Login request", notes = "Provides user login request support, needs user credentials")
    public UserResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authorizationService.authenticateUser(loginRequest);
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign in request", notes = "User registration, need to provide SignUpRequest DTO")
    public void registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
        User user = authorizationService.registerUser(signUpRequest);
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
    }

    @PostMapping("/reset-password")
    @ApiOperation(value = "Reset password request for user entity", notes = "Deactivates user account till user will provide new password")
    public ResponseEntity<?> resetPassword(@NotEmpty @RequestBody String username)
    {
        return passwordResetService.resetPassword(username);
    }

    @PostMapping("/new-password")
    @ApiOperation(value = "New password for authorization request", notes = "Provides user new credential password for authorization")
    public ResponseEntity<?> setNewPassword(@RequestBody SetNewPasswordRequest request)
    {
        return passwordResetService.setNewPassword(request);
    }

    @PostMapping("/token/verify")
    @ApiOperation(value = "Registration token verification", notes = "Confirms user account and activates it, if only token is correct")
    public void confirmRegistration(@NotEmpty @RequestParam String token) throws Exception {
        authorizationService.confirmRegistration(token);
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

    @PutMapping(value = "/log-out")
    @ApiOperation(value = "Logs out user")
    public void logout(HttpServletRequest httpServletRequest) throws ServletException {
        httpServletRequest.logout();
    }

    @PutMapping("/deactivate")
    @ApiOperation(value = "Deactivates user account")
    public void deactivateUserAccount(@RequestParam Long userId, @RequestParam String deactivationReason, HttpServletRequest request) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, IOException, InterruptedException {
        accountDeactivationService.deactivateUser(userId,deactivationReason,request);
    }
}
