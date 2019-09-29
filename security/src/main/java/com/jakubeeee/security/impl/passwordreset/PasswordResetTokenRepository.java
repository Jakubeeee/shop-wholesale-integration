package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.security.impl.passwordreset.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring data jpa repository for crud operations on {@link PasswordResetToken} objects.
 */
@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByValue(String value);

}

