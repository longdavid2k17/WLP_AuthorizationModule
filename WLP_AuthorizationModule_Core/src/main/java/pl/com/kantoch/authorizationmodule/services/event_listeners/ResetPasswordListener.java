package pl.com.kantoch.authorizationmodule.services.event_listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnPasswordResetRequestEvent;
import pl.com.kantoch.authorizationmodule.services.MailingService;
import pl.com.kantoch.authorizationmodule.services.TokenService;

import java.io.IOException;
import java.util.UUID;

@Component
public class ResetPasswordListener implements ApplicationListener<OnPasswordResetRequestEvent>
{
    private final TokenService tokenService;
    private final MailingService mailingService;

    public ResetPasswordListener(TokenService tokenService,
                                 MailingService mailingService) {
        this.tokenService = tokenService;
        this.mailingService = mailingService;
    }

    @Override
    public void onApplicationEvent(OnPasswordResetRequestEvent event)
    {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.createResetPasswordToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Resetowanie hasła";
        String message = "Odnotowanie żądanie zmiany hasła dostępu do systemu MagIT. Aby zresetować hasło kliknij w link poniżej";
        try {
            mailingService.sendMailRequest(recipientAddress,subject,message + "\r\n" + "http://localhost:4200/reset-confirm?token=" + token);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
