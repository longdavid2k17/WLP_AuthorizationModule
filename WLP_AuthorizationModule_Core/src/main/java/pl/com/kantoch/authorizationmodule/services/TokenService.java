package pl.com.kantoch.authorizationmodule.services;

import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.VerificationToken;

public interface TokenService {
    void createVerificationToken(User user, String token);
    void createResetPasswordToken(User user, String token);
    VerificationToken getVerificationToken(String VerificationToken);
    Boolean validateVerificationToken(String token) throws Exception;
}
