package pl.com.kantoch.authorizationmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Scope;

import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<Scope,Long> {
    Optional<Scope> findByName(String name);
}
