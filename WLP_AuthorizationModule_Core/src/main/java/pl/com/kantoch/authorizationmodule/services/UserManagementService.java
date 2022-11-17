package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.com.kantoch.authorizationmodule.configuration.payload.response.UserDTO;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.configuration.user_details.UserDetailsImpl;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.payload.response.StatsResponse;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagementService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;
    private final PrivilegesService privilegesService;
    private final MailingService mailingService;

    public UserManagementService(UserRepository userRepository,
                                 SessionRegistry sessionRegistry,
                                 PrivilegesService privilegesService,
                                 MailingService mailingService) {
        this.userRepository = userRepository;
        this.sessionRegistry = sessionRegistry;
        this.privilegesService = privilegesService;
        this.mailingService = mailingService;
    }

    public ResponseEntity<?> queryUsers(Collection<Role> roles){
        try {
            Collection<UserDTO> users = getUsers();
            if(roles==null || roles.isEmpty()) return ResponseEntity.ok(users);
            return ResponseEntity.ok(
                    users.stream()
                            .filter(e-> CollectionUtils.containsAny(e.getRoles(),roles))
                            .collect(Collectors.toUnmodifiableList()));
        } catch (Exception e){
            String errorMessage = "Error during querying users. Error class: "+e.getClass()+". Error message: "+e.getMessage();
            LOGGER.error(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    private Collection<UserDTO> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(UserDTO::build)
                .collect(Collectors.toUnmodifiableList());
    }

    public ResponseEntity<?> getStats() {
        try {
            List<String> users = queryUsersFromSession();
            Long allUsersAccounts = userRepository.countAllUsers();
            Long allNonActivatedUserAccounts = userRepository.countNonActivatedUsers();
            Collection<StatsResponse> response = new ArrayList<>();
            response.add(new StatsResponse("Currently logged users",users.size()));
            response.add(new StatsResponse("All user accounts count",allUsersAccounts));
            response.add(new StatsResponse("All non-activated user accounts count",allNonActivatedUserAccounts));
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot query users state!");
        }
    }

    private List<String> queryUsersFromSession(){
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(this::mapToUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private String mapToUser(Object object){
        UserDetailsImpl userDetails = (UserDetailsImpl) object;
        return userDetails.getUsername();
    }

    @Transactional
    public ResponseEntity<?> editAccountData(UserDTO userDTO, HttpServletRequest httpServletRequest) {
        try {
            ERole[] eRoles = {ERole.ROLE_MODERATOR,ERole.ROLE_ADMIN};
            if(!privilegesService.hasRequiredPrivileges(httpServletRequest,eRoles)) throw new IllegalStateException("You have not authority to proceed this action!");
            Optional<User> userOptional = userRepository.findById(userDTO.getId());
            if(userOptional.isEmpty()) throw new NoSuchUserException(userDTO.getId());
            User user = userOptional.get();
            user.setName(userDTO.getName());
            user.setLastName(userDTO.getLastName());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            userRepository.save(user);
            mailingService.sendMailRequest(user.getEmail(),"Account data change","We occurred yours account data change request from the administration. This is your actual data: "+ userDTO);
            LOGGER.warn("User with ID={} has been edited",userDTO.getId());
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            LOGGER.error("Error during editing account data! Exception class: {}. Error message: {}",e.getClass(),e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
