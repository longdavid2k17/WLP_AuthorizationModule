package pl.com.kantoch.authorizationmodule.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.LoginRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SetNewPasswordRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SignUpRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.response.UserResponse;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnRegistrationCompleteEvent;
import pl.com.kantoch.authorizationmodule.services.AuthorizationService;
import pl.com.kantoch.authorizationmodule.services.PasswordResetService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/authorization")
public class AuthorizationResource {

    private final AuthorizationService authorizationService;
    private final PasswordResetService passwordResetService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthorizationResource(AuthorizationService authorizationService, PasswordResetService passwordResetService, ApplicationEventPublisher eventPublisher) {
        this.authorizationService = authorizationService;
        this.passwordResetService = passwordResetService;
        this.eventPublisher = eventPublisher;
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
    @ApiOperation(value = "Registration token verification", notes = "Confirms user account and activates it, if only token is correct")
    public void grantRole(@NotEmpty @RequestParam String targetUsername, @NotEmpty @RequestParam ERole role, HttpServletRequest request) throws Exception {
        authorizationService.grantRole(targetUsername,role,request);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Logs out user")
    @RequestMapping(value = "/logout", method = RequestMethod.PUT)
    public String logout(HttpServletRequest httpServletRequest) throws ServletException {
        httpServletRequest.logout();
        return "redirect: http://localhost:8081";
    }
}
