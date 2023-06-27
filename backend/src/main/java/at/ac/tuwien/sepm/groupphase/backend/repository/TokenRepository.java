package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {


    /**
     * Saves the token in the database.
     *
     * @param token the token which is persisted in the database
     */
    String save(String token);

    /**
     * Finds Token by email.
     *
     * @param email return string
     */
    PasswordResetToken getTokenByEmail(String email);

    /**
     * Delete a Token entry by email.
     *
     * @param email the email of which the entry will be deleted
     */
    @Transactional
    void deleteByEmail(String email);

}
