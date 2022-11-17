package pl.com.kantoch.authorizationmodule.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.GrantRolesRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.LoginRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SignUpRequest;
import pl.com.kantoch.authorizationmodule.configuration.payload.response.UserResponse;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.RoleRepository;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.configuration.user_details.UserDetailsImpl;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class AuthorizationService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PrivilegesService privilegesService;
    private final MailingService mailingService;

    public AuthorizationService(AuthenticationManager authenticationManager,
                                JWTUtil jwtUtil,
                                UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder,
                                TokenService tokenService,
                                PrivilegesService privilegesService,
                                MailingService mailingService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.privilegesService = privilegesService;
        this.mailingService = mailingService;
    }

    public UserResponse authenticateUser(LoginRequest loginRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new UserResponse(jwt, userDetails.getId(),userDetails.getUsername(),userDetails.getEmail(),
                roles, jwtUtil.getExpirationDate(jwt));
    }

    @Transactional
    public User registerUser(SignUpRequest signUpRequest) throws RuntimeException{
        if (userRepository.existsByUsername(signUpRequest.getUsername())) throw new IllegalStateException("Error: username is already taken!");
        if (userRepository.existsByEmail(signUpRequest.getEmail())) throw new IllegalStateException("Error: email is already in use!");

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setLastName(signUpRequest.getLastName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setActive(false);
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null)
        {
            Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        else
        {
            strRoles.forEach(role ->
            {
                switch (role)
                {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByRoleName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public void confirmRegistration(String token) throws Exception {
        boolean result = tokenService.validateVerificationToken(token);
        if(!result) throw new IllegalStateException("Could not complete registration! Try again!");
    }

    @Transactional
    public void grantRole(String targetUsername, ERole role, HttpServletRequest httpServletRequest) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException {
        String token = jwtUtil.getToken(httpServletRequest);
        String username = jwtUtil.getUsernameFromJwtToken(httpServletRequest);
        if(!privilegesService.hasRequiredPrivileges(token,ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<User> optionalTargetUser = userRepository.findByUsername(targetUsername);
        if(optionalTargetUser.isEmpty()) throw new NoSuchUserException(targetUsername);
        User targetUser = optionalTargetUser.get();
        Optional<Role> optionalTargetRole = roleRepository.findByRoleName(role);
        if(optionalTargetRole.isEmpty()) throw new NoSuchRoleException(role);
        if(!targetUser.getRoles().contains(optionalTargetRole.get())){
            targetUser.getRoles().add(optionalTargetRole.get());
            userRepository.save(targetUser);
        }
    }

    @Transactional
    public void grantRoles(GrantRolesRequest grantRolesRequest, HttpServletRequest request) throws NoSuchRoleException, NoRequiredRoleException, NoSuchUserException, IOException, InterruptedException {
        if(!privilegesService.hasRequiredPrivileges(request,ERole.ROLE_ADMIN)) throw new IllegalStateException("You have not authority to proceed this action!");
        Optional<User> userOptional = userRepository.findById(grantRolesRequest.getTargetUserId());
        if(userOptional.isEmpty()) throw new NoSuchUserException(grantRolesRequest.getTargetUserId());
        User user = userOptional.get();
        Set<Role> targetRoles = new HashSet<>(grantRolesRequest.getRoleCollection());
        user.setRoles(targetRoles);
        AtomicReference<String> rolesString = new AtomicReference<>("");
        targetRoles.forEach(e-> rolesString.updateAndGet(v -> v + e.getRoleName()+","));
        String message = "Administration of our system has changed your roles in system. Current roles are: "+rolesString;
        mailingService.sendMailRequest(user.getEmail(),"Account roles granted",message);
        userRepository.save(user);
    }
}