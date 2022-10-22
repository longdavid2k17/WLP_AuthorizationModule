package pl.com.kantoch.authorizationmodule.services.event_listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import pl.com.kantoch.authorizationmodule.configuration.module_registrator.ModuleRegistrationService;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.entities.events.OnRegistrationCompleteEvent;
import pl.com.kantoch.authorizationmodule.services.TokenService;
import pl.com.kantoch.requests.HttpRequests;
import pl.com.kantoch.requests.ModuleEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent>
{
    private final TokenService service;
    private final ModuleRegistrationService moduleRegistrationService;
    private final MessageSource messages;

    public RegistrationListener(TokenService service, ModuleRegistrationService moduleRegistrationService, MessageSource messages) {
        this.service = service;
        this.moduleRegistrationService = moduleRegistrationService;
        this.messages = messages;
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
        sendMailRequest(recipientAddress,subject,message);
    }


    private void sendMailRequest(String emailAddress,String subject, String message) throws IOException, InterruptedException {
        var values = new HashMap<String, String>() {{
            put("to", emailAddress);
            put ("subject", subject);
            put ("text", message);
        }};

        ModuleEntity mailingModule = null;
        ObjectMapper mapper = new ObjectMapper();
        String response = moduleRegistrationService.getModuleConfiguration("MAILING_MODULE");
        mailingModule = mapper.readValue(response,ModuleEntity.class);

        HttpRequests.sendPostRequest("http://localhost:8091/api/mailing",values);
    }
}
