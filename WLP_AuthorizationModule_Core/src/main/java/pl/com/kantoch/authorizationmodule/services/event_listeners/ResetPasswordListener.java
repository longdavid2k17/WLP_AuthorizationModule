package pl.com.kantoch.authorizationmodule.services.event_listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnPasswordResetRequestEvent;
import pl.com.kantoch.authorizationmodule.services.TokenService;
import pl.com.kantoch.requests.HttpRequests;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Component
public class ResetPasswordListener implements ApplicationListener<OnPasswordResetRequestEvent>
{
    private final TokenService tokenService;

    public ResetPasswordListener(TokenService tokenService) {
        this.tokenService = tokenService;
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
            sendMailRequest(recipientAddress,subject,message + "\r\n" + "http://localhost:4200/reset-confirm?token=" + token);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendMailRequest(String emailAddress,String subject, String message) throws IOException, InterruptedException {
        var values = new HashMap<String, String>() {{
            put("to", emailAddress);
            put ("subject", subject);
            put ("text", message);
        }};

        HttpRequests.sendPostRequest("http://localhost:8091/api/mailing",values);
    }
}
