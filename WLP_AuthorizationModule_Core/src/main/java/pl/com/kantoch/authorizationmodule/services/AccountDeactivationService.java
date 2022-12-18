package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.jwt.JWTUtil;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.exceptions.NoRequiredRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchRoleException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Service
public class AccountDeactivationService {
    private final Logger LOGGER = LoggerFactory.getLogger(AccountDeactivationService.class);

    private final UserRepository userRepository;
    private final PrivilegesService privilegesService;
    private final MailingService mailingService;
    private final JWTUtil jwtUtil;

    public AccountDeactivationService(UserRepository userRepository,
                                      PrivilegesService privilegesService,
                                      MailingService mailingService,
                                      JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.privilegesService = privilegesService;
        this.mailingService = mailingService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void deactivateUser(Long id, String userDeactivationReason, HttpServletRequest request) throws NoSuchUserException, IOException, InterruptedException, NoSuchRoleException, NoRequiredRoleException {
        String username = jwtUtil.getUsernameFromJwtToken(request);
        Optional<User> optionalPerformingUser = userRepository.findByUsername(username);
        if(optionalPerformingUser.isEmpty()) throw new NoSuchUserException(username);
        if(!privilegesService.hasRequiredPrivileges(request, ERole.ROLE_ADMIN)) throw new NoRequiredRoleException(username,ERole.ROLE_ADMIN);
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) throw new NoSuchUserException(id);
        User user = optionalUser.get();
        user.setActive(false);
        mailingService.sendMailRequest(user.getEmail(),"Account deactivation",userDeactivationReason);
        userRepository.save(user);
        LOGGER.warn("User with ID={} has been deactivated",id);
    }
}
