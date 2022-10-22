package pl.com.kantoch.authorizationmodule.configuration.module_registrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.com.kantoch.JsonLoader;
import pl.com.kantoch.mavn_tools.VersionReceiverService;
import pl.com.kantoch.requests.HttpRequests;
import pl.com.kantoch.requests.ModuleEntity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static pl.com.kantoch.authorizationmodule.tools.NameDictionary.SERVICE_DISCOVERY_CONFIGURATION_FILE_PATH;
import static pl.com.kantoch.authorizationmodule.tools.NameDictionary.MAVEN_FILE_PATH;

@Service
public class ModuleRegistrationService {

    private final Logger LOGGER = LoggerFactory.getLogger(ModuleRegistrationService.class);

    private VersionReceiverService versionReceiverService;

    @Value("${server.port}")
    private String SERVICE_PORT;
    private final static String MODULE_NAME = "AUTHORIZATION_MODULE";
    private final static String APPLICATION_CONTEXT = "WLP_"+MODULE_NAME;
    private String HOST_ADDRESS;
    private final Map<String,String> configuration;

    private ModuleEntity serviceDiscoveryModuleConfiguration;
    private JsonLoader jsonLoader;

    public ModuleRegistrationService() {
        this.configuration = new HashMap<>();
        this.jsonLoader = new JsonLoader();
        try {
            initializeHostAddress();
            this.versionReceiverService = new VersionReceiverService(MAVEN_FILE_PATH);
            this.serviceDiscoveryModuleConfiguration = getServiceDiscoveryModuleConfiguration();
            LOGGER.warn("Service discovery is available on address {}, (PORT:{})",serviceDiscoveryModuleConfiguration.getHostAddress(),serviceDiscoveryModuleConfiguration.getServicePort());
        } catch (UnknownHostException e) {
            LOGGER.error("Error occurred during host address property initialization. Class: {}. Message: {}",e.getClass(),e.getMessage());
        } catch (XmlPullParserException | IOException e) {
            LOGGER.error("Error occurred during reading pom.xml file. Exception class: {}. Message: {}",e.getClass(),e.getMessage());
        } catch (Exception e){
            LOGGER.error("Error during loading service discovery configuration. Exception class: {}. Message: {}",e.getClass(),e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1200000)
    private void checkConfigRegistrationState(){
        registerService();
    }

    private void initializeConfiguration(){
        configuration.put("moduleName",MODULE_NAME);
        configuration.put("servicePort",SERVICE_PORT);
        configuration.put("hostAddress",HOST_ADDRESS);
        configuration.put("applicationContext",APPLICATION_CONTEXT);
        configuration.put("moduleVersion", versionReceiverService.getVersion());
    }

    private ModuleEntity getServiceDiscoveryModuleConfiguration(){
        ModuleEntity module = null;
        File configurationFile = new File(SERVICE_DISCOVERY_CONFIGURATION_FILE_PATH);
        if(configurationFile.exists()){
            try {
                String loadedJson = jsonLoader.loadFile(SERVICE_DISCOVERY_CONFIGURATION_FILE_PATH);
                ObjectMapper mapper = new ObjectMapper();
                module = mapper.readValue(loadedJson,ModuleEntity.class);
            } catch (Exception e) {
                throw new RuntimeException("Error during loading service discovery configuration from file. Path: "+ SERVICE_DISCOVERY_CONFIGURATION_FILE_PATH +" . Message: "+e.getMessage());
            }
        }
        return module;
    }

    public String getModuleConfiguration(String moduleName) {
        try {
            Map<String,String> params = new HashMap<String, String>() {{
                put("moduleName", moduleName);
            }};
            if(serviceDiscoveryModuleConfiguration==null) throw new IllegalStateException("Cannot connect to service discovery. Unknown configuration");
            String requestUrl = "http://"+serviceDiscoveryModuleConfiguration.getHostAddress()+":"+serviceDiscoveryModuleConfiguration.getServicePort()+"/api/module-registration/search";
            HttpResponse<String> response = HttpRequests.sendGetRequest(requestUrl,params);
            if(response.statusCode()!=200) throw new IllegalStateException("Request response code = "+response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerService(){
        try {
            initializeConfiguration();
            String requestUrl = "http://"+serviceDiscoveryModuleConfiguration.getHostAddress()+":"+serviceDiscoveryModuleConfiguration.getServicePort()+"/api/module-registration";
            HttpResponse<String> response = HttpRequests.sendPostRequest(requestUrl,configuration);
            if(response.statusCode()!=200) throw new IllegalStateException("Status code: "+response.statusCode()+", message: "+response.body());
            LOGGER.warn(response.body());
        }
        catch (Exception e){
            LOGGER.error("ModuleRegistrationService has occurred exception {} with message: {}",e.getClass(),e.getMessage());
        }
    }

    private void initializeHostAddress() throws UnknownHostException {
        HOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
    }

    public Map<String, String> getCurrentConfiguration() {
        return configuration;
    }
}
