package pl.com.kantoch.authorizationmodule.services.event_listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnRegistrationCompleteEvent;
import pl.com.kantoch.authorizationmodule.services.MailingService;
import pl.com.kantoch.authorizationmodule.services.TokenService;

import java.io.IOException;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent>
{
    private final TokenService service;
    private final MailingService mailingService;

    public RegistrationListener(TokenService service,
                                MailingService mailingService) {
        this.service = service;
        this.mailingService = mailingService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws IOException, InterruptedException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Confirm registration";
        String confirmationUrl
                = event.getAppUrl() + "/auth/token/verify?token=" + token;
        //String message = "Your account has been created successfully. To confirm and end registration process click the link bellow."+ "\r\n" + "http://localhost:4200/register-confirm?token=" + token;
        String message = "Your account has been created successfully. To confirm and end registration process click the link bellow."+ "\r\n" + confirmationUrl;

        //mailService.sendMail(recipientAddress,subject,message + "\r\n" + "http://localhost:4200/register-confirm?token=" + token,false);
        mailingService.sendMailRequest(recipientAddress,subject,message);
    }
}
