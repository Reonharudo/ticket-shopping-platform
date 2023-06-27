package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordUser;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtAuthorizationFilter;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import jakarta.xml.bind.ValidationException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtTokenizer jwtTokenizer;
    private final TokenRepository tokenRepository;

    @Autowired
    public CustomUserDetailService(ApplicationUserRepository applicationUserRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, JwtAuthorizationFilter jwtAuthorizationFilter, TokenRepository tokenRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtTokenizer = jwtTokenizer;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.trace("loadUserByUsername({})", email);
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            if (applicationUser.getLocked()) {
                return new User(applicationUser.getEmail(), applicationUser.getPassword(), true, true, true, false, grantedAuthorities);
            } else {
                return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
            }


        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.trace("findApplicationUserByEmail({})", email);
        ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public void checkForExistingUserByEmail(String email) throws ValidationException {
        LOGGER.trace("checkForExistingUserByEmail({})", email);
        ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(email);
        if (applicationUser != null) {
            throw new ValidationException("Email already in use!");
        }
    }


    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.trace("login({})", userLoginDto);
        try {
            UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
            ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());

            if (userDetails == null) {
                throw new BadCredentialsException("Email or password is incorrect!");
            }

            if (!passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())) {
                if (applicationUser.getAdmin() == Boolean.TRUE) {
                    throw new BadCredentialsException("Email or password is incorrect");
                }
                applicationUser.setFailedLoginAttempts(applicationUser.getFailedLoginAttempts() + 1);
                if (applicationUser.getFailedLoginAttempts() >= 5) {
                    applicationUser.setLocked(true);
                    applicationUserRepository.save(applicationUser);
                    throw new LockedException("Too many failed login attempts! Your account is locked. Contact an administrator!");
                } else {
                    applicationUserRepository.save(applicationUser);
                    throw new BadCredentialsException("Email or password is incorrect!");
                }
            }

            if (!userDetails.isAccountNonLocked()) {
                throw new LockedException("Account is locked. Contact an administrator!");
            }
            if (!userDetails.isCredentialsNonExpired()) {
                throw new BadCredentialsException("Email or password is incorrect!");
            }
            applicationUser.setFailedLoginAttempts(0);
            applicationUserRepository.save(applicationUser);
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Email or password is incorrect or account is locked");
        }
    }

    @Override
    public ApplicationUser register(ApplicationUser applicationUser) throws ValidationException {
        LOGGER.trace("register({})", applicationUser);
        if (!isValidPassword(applicationUser.getPassword())) {
            throw new ValidationException("Invalid Password!");
        }
        String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
        applicationUser.setPassword(encodedPassword);
        checkForExistingUserByEmail(applicationUser.getEmail());
        return applicationUserRepository.save(applicationUser);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    @Override
    public ApplicationUser edit(ApplicationUser applicationUser, String token) {
        LOGGER.trace("edit({})", applicationUser);
        UserDetails currentUser = loadUserByUsername(applicationUser.getEmail());
        if (passwordEncoder.matches(applicationUser.getPassword(), currentUser.getPassword())) {
            String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
            applicationUser.setPassword(encodedPassword);
            return applicationUserRepository.save(applicationUser);
        }
        throw new BadCredentialsException("Password was wrong!");
    }

    @Override
    public ApplicationUser getUser(String token) {
        LOGGER.trace("getUser({})", token);
        String email = jwtAuthorizationFilter.getUsernameFromToken(token);
        return applicationUserRepository.findUserByEmail(email);
    }

    @Override
    public void delete(ApplicationUser applicationUser) {
        LOGGER.trace("delete({})", applicationUser);
        UserDetails userDetails = loadUserByUsername(applicationUser.getEmail());
        if (!passwordEncoder.matches(applicationUser.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }
        applicationUserRepository.deleteById(applicationUser.getId());
    }

    @Override
    public void block(ApplicationUser applicationUser) {
        LOGGER.trace("block({})", applicationUser);
        applicationUserRepository.updateIsLocked(applicationUser.getEmail(), applicationUser.getLocked());
    }

    @Override
    public List<ApplicationUser> getBlockedUsers(ApplicationUser applicationUser, String token, int pageIndex) {
        LOGGER.trace("getBlockedUsers({})", applicationUser);
        ApplicationUser admin = getUser(token);
        Pageable pageable = PageRequest.of(pageIndex, 20, Sort.by("email").ascending());
        if (applicationUser.getLocked().equals(Boolean.TRUE)) {
            return applicationUserRepository.findUserByIsLockedIsTrueAndEmail(applicationUser.getEmail(), admin.getEmail(), pageable);
        } else {
            return applicationUserRepository.findUserByIsLockedIsFalseAndEmail(applicationUser.getEmail(), admin.getEmail(), pageable);
        }

    }

    @Override
    public Long getUserIdFromToken(String token) {
        LOGGER.trace("getUserIdFromToken({})", token);
        ApplicationUser user = this.getUser(token);
        return user == null ? null : user.getId();
    }

    @Override
    public ApplicationUser getUserById(Long id) {
        LOGGER.trace("getUserById({})", id);
        return applicationUserRepository.getApplicationUserById(id);
    }

    @Override
    public void resetPassword(ResetPasswordUser user) {
        LOGGER.trace("resetPassword({})", user);
        PasswordResetToken actualToken = tokenRepository.getTokenByEmail(user.email());

        //Check if the token which was received through email is the same as the one saved in the database
        if (!actualToken.getToken().equals(user.token()) || actualToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InsufficientAuthenticationException("Token is invalid");
        }

        //Update user with new encoded password
        String encodedPassword = passwordEncoder.encode(user.newPassword());
        applicationUserRepository.updatePassword(user.email(), encodedPassword);

        //When password is successfully changed delete the token entry
        tokenRepository.deleteByEmail(user.email());
    }


}
