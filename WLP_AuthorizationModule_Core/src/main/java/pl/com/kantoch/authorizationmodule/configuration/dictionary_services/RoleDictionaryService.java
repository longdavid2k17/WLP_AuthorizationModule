package pl.com.kantoch.authorizationmodule.configuration.dictionary_services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.ERole;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.RoleRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RoleDictionaryService {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleDictionaryService.class);

    private final RoleRepository roleRepository;

    public RoleDictionaryService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Scheduled(fixedRate = 360000,initialDelay = 1000)
    private void setupRolesData() {
        Collection<Role> roleCollection = roleRepository.findAll();
        if(roleCollection.isEmpty()) {
            LOGGER.warn("Roles database table is empty!");
            List<Role> newRolesCollection = new ArrayList<>();
            for(ERole eRole : ERole.values()){
                newRolesCollection.add(new Role(eRole));
            }
            roleRepository.saveAll(newRolesCollection);
            LOGGER.info("List of {} roles has been saved into database.",newRolesCollection.size());
        }
    }
}
