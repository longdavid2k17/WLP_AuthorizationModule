package pl.com.kantoch.authorizationmodule.services;

import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;
import pl.com.kantoch.authorizationmodule.entities.PasswordResetToken;
import pl.com.kantoch.authorizationmodule.entities.VerificationToken;
import pl.com.kantoch.authorizationmodule.repositories.PasswordResetTokenRepository;
import pl.com.kantoch.authorizationmodule.repositories.VerificationTokenRepository;

import java.util.Calendar;

@Service
public class TokenServiceImplementation implements TokenService{

    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    public TokenServiceImplementation(VerificationTokenRepository verificationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public void createResetPasswordToken(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public Boolean validateVerificationToken(String token) throws Exception {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) throw new NullPointerException("Passed token value is null!");

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) throw new Exception("This token has been expired!");
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return true;
    }
}
