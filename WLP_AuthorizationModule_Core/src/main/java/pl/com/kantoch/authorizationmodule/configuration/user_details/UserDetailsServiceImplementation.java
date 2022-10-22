package pl.com.kantoch.authorizationmodule.configuration.user_details;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.UserRepository;

/**
 * <p>Klasa serwisu implementująca UserDetailsService</p>
 * @author Dawid Kańtoch
 * @version 1.0
 * @since 1.0
 */
@Service
public class UserDetailsServiceImplementation implements UserDetailsService
{
    private final UserRepository userRepository;

    public UserDetailsServiceImplementation(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User with username '" + username+"' has not been found!"));
        return UserDetailsImpl.build(user);
    }
}
