package pl.com.kantoch.authorizationmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.kantoch.authorizationmodule.entities.PasswordResetToken;

import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);

    List<PasswordResetToken> findAllByUser_Id(Long id);
}
