package pl.com.kantoch.authorizationmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.Permission;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findPermissionById(Long id);
    Optional<Permission> findPermissionByName(String name);

    @Query(nativeQuery =true,value = "SELECT * FROM Permission p WHERE :user MEMBER OF p.userSet")   // 3. Spring JPA In cause using native query
    Set<Permission> getAllPermissionsContainingUser(@Param("user") User user);
}
