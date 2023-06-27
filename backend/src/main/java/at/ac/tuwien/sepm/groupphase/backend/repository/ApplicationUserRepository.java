package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    /**
     * Saves the specified user in the database.
     *
     * @param applicationUser the user which will be persisted
     * @return the user which was persisted in the database
     */
    ApplicationUser save(ApplicationUser applicationUser);

    /**
     * Find user with the specified email.
     *
     * @param email the users email address
     * @return the user with the specified email
     */
    ApplicationUser findUserByEmail(String email);

    /**
     * Delete user by id.
     *
     * @param id the users id
     */
    @Override
    void deleteById(Long id);

    /**
     * Change isLocked status for user.
     *
     * @param email    the email of the user
     * @param isLocked the boolean to change
     */
    @Transactional
    @Modifying
    @Query("update ApplicationUser u set u.isLocked = :isLocked where u.email = :email")
    void updateIsLocked(@Param(value = "email") String email, @Param(value = "isLocked") boolean isLocked);

    /**
     * Returns locked users.
     *
     * @param email    search parameter
     * @param admin    current logged-in admin
     * @param pageable specifies parameters and index of page
     * @return ordered page of user entries
     */
    @Transactional(readOnly = true)
    @Query("SELECT u FROM ApplicationUser u WHERE u.isLocked = TRUE AND u.email LIKE %:email% AND u.email NOT LIKE %:admin%")
    List<ApplicationUser> findUserByIsLockedIsTrueAndEmail(@Param(value = "email") String email, @Param(value = "admin") String admin, @NonNull Pageable pageable);

    /**
     * Returns unlocked users.
     *
     * @param email    search parameter
     * @param admin    current logged-in admin
     * @param pageable specifies parameters and index of page
     * @return ordered page of user entries
     */
    @Transactional(readOnly = true)
    @Query("SELECT u FROM ApplicationUser u WHERE u.isLocked = FALSE AND u.email LIKE %:email% AND u.email NOT LIKE %:admin%")
    List<ApplicationUser> findUserByIsLockedIsFalseAndEmail(@Param(value = "email") String email, @Param(value = "admin") String admin, @NonNull Pageable pageable);

    /**
     * get the user with the given id.
     *
     * @param id the id of the user
     * @return the user with the id or null of no such user exists
     */
    ApplicationUser getApplicationUserById(Long id);

    @Transactional
    @Modifying
    @Query("update ApplicationUser u set u.password = :newPassword where u.email = :email")
    void updatePassword(@Param(value = "email") String email, @Param(value = "newPassword") String newPassword);
}
