package pl.com.kantoch.authorizationmodule.services;

import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.RoleRepository;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;

import java.util.Optional;

@Service
public class PrivilegesService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RoleRepository roleRepository;

    public PrivilegesService(UserRepository userRepository, JWTUtil jwtUtil, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

    public boolean hasRequiredPrivileges(String token, ERole role) throws NoSuchUserException, NoSuchRoleException, NoRequiredRoleException {
        String username = jwtUtil.getUsernameFromJwtToken(token);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
        Optional<Role> adminRoleOptional = roleRepository.findByRoleName(role);
        if(adminRoleOptional.isEmpty()) throw new NoSuchRoleException(role);
        return optionalUser.get().getRoles().contains(adminRoleOptional.get());
    }
}
