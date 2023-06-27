package at.ac.tuwien.sepm.groupphase.backend.service;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordUser;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import jakarta.xml.bind.ValidationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Check for application user based on the email address.
     *
     * @param email the email address
     */
    void checkForExistingUserByEmail(String email) throws ValidationException;

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto) throws BadCredentialsException;

    /**
     * Register a user.
     *
     * @param applicationUser applicationuser which should be registered
     * @return the Applicationuser that was registered
     * @throws ValidationException if input fields in applicationuser are incorrect
     */
    ApplicationUser register(ApplicationUser applicationUser) throws ValidationException;

    /**
     * Edit a user.
     *
     * @param applicationUser holds the values to update a user
     * @return the Applicationuser that was edited
     */
    ApplicationUser edit(ApplicationUser applicationUser, String token);

    /**
     * Retrieve user from token.
     *
     * @param token which is used to retrieve the corresponding user
     * @return the applicationuser
     */
    ApplicationUser getUser(String token);

    /**
     * Delete a user.
     *
     * @param applicationUser the user which should be deleted from database
     */
    void delete(ApplicationUser applicationUser);

    /**
     * Block|Unblock a user in the system.
     *
     * @param applicationUser the applicationuser which should be blocked in database
     */
    void block(ApplicationUser applicationUser);

    /**
     * Get a list of users depending on their isLocked status.
     *
     * @param applicationUser the values to search for in database
     * @param token           the token of the logged-in admin
     * @param pageIndex       index of page to load
     * @return page of 20 user entries ordered ascending by the email
     */
    List<ApplicationUser> getBlockedUsers(ApplicationUser applicationUser, String token, int pageIndex);

    Long getUserIdFromToken(String token);

    ApplicationUser getUserById(Long id);

    /**
     * Reset the password for a user.
     *
     * @param user holds the information needed to reset the password
     */
    void resetPassword(ResetPasswordUser user);
}
