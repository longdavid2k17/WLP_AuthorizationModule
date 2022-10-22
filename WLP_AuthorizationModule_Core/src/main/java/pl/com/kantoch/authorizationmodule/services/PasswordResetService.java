package pl.com.kantoch.authorizationmodule.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.payload.requests.SetNewPasswordRequest;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.entities.PasswordResetToken;
import pl.com.kantoch.authorizationmodule.entities.events.OnPasswordResetRequestEvent;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchPasswordResetRequestException;
import pl.com.kantoch.authorizationmodule.exceptions.NoSuchUserException;
import pl.com.kantoch.authorizationmodule.exceptions.TokenHasExpiredException;
import pl.com.kantoch.authorizationmodule.repositories.PasswordResetTokenRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository,
                                ApplicationEventPublisher eventPublisher, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> resetPassword(String username) {
        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if(optionalUser.isEmpty()) throw new NoSuchUserException(username);
            User user = optionalUser.get();
            user.setActive(false);
            userRepository.save(user);
            eventPublisher.publishEvent(new OnPasswordResetRequestEvent(user));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (Exception e){
            LOGGER.error("Error during resetPassword method process. Message: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> setNewPassword(SetNewPasswordRequest request) {
        try
        {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken());
            if(resetToken!=null)
            {
                if(resetToken.getExpiryDate().after(new Date()))
                {
                    User user = resetToken.getUser();
                    user.setActive(true);
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    userRepository.save(user);
                    List<PasswordResetToken> allTokens = passwordResetTokenRepository.findAllByUser_Id(user.getId());
                    passwordResetTokenRepository.deleteAll(allTokens);
                    return ResponseEntity.ok().build();
                }
                else
                {
                    List<PasswordResetToken> allTokens = passwordResetTokenRepository.findAllByUser_Id(resetToken.getUser().getId());
                    passwordResetTokenRepository.deleteAll(allTokens);
                    throw new TokenHasExpiredException("The token link has expired. Try again!");
                }
            }
            else throw new NoSuchPasswordResetRequestException("No password reset request has been found for this request!");
        }
        catch (Exception e)
        {
            LOGGER.error("Error during setNewPassword method process. Message: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
