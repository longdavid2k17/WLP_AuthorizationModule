package pl.com.kantoch.authorizationmodule.configuration.security_entities.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("select count(u) from User u")
    Long countAllUsers();

    @Query("select count(u) from User u where u.active = false")
    Long countNonActivatedUsers();
}
