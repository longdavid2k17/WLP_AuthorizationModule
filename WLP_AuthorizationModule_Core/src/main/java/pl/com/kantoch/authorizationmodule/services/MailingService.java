package pl.com.kantoch.authorizationmodule.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.module_registrator.ModuleRegistrationService;
import pl.com.kantoch.requests.HttpRequests;
import pl.com.kantoch.requests.ModuleEntity;

import java.io.IOException;
import java.util.HashMap;


@Service
public class MailingService {

    private final ModuleRegistrationService moduleRegistrationService;

    public MailingService(ModuleRegistrationService moduleRegistrationService) {
        this.moduleRegistrationService = moduleRegistrationService;
    }

    public void sendMailRequest(String emailAddress,String subject, String message) throws IOException, InterruptedException {
        var values = new HashMap<String, String>() {{
            put("to", emailAddress);
            put ("subject", subject);
            put ("text", message);
        }};

        ModuleEntity mailingModule = null;
        ObjectMapper mapper = new ObjectMapper();
        String response = moduleRegistrationService.getModuleConfiguration("MAILING_MODULE");
        mailingModule = mapper.readValue(response,ModuleEntity.class);
        String requestUrl = "http://"+mailingModule.getHostAddress()+":"+mailingModule.getServicePort()+"/api/mailing";
        HttpRequests.sendPostRequest(requestUrl,values);
    }
}
