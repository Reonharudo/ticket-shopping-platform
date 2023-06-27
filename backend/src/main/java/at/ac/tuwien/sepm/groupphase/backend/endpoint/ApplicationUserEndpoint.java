package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordUser;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDeleteDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.PasswordResetService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = ApplicationUserEndpoint.BASE_PATH)
public class ApplicationUserEndpoint {

    public static final String BASE_PATH = "/api/v1/user";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordResetService passwordResetService;

    @Autowired
    public ApplicationUserEndpoint(UserService userService, UserMapper userMapper, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordResetService = passwordResetService;
    }

    @DeleteMapping
    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestBody UserDeleteDto userDeleteDto) {
        LOGGER.info("DELETE :" + BASE_PATH + " USER: {}", userDeleteDto);
        userService.delete(userMapper.userDeleteDtoToEntity(userDeleteDto));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PermitAll
    @Operation(summary = "Edit a user")
    public UserDetailDto update(@Valid @RequestBody UserDetailDto userDetailDto, @RequestHeader("Authorization") String token) {
        LOGGER.info("EDIT : " + BASE_PATH + " USER: {}", userDetailDto);
        return userMapper.entityToUserDetailDto(userService.edit(userMapper.userDetailDtoToEntity(userDetailDto), token));
    }

    @GetMapping
    @PermitAll
    public UserDetailDto getUser(@RequestHeader("Authorization") String token) {
        LOGGER.info("GET USER :" + BASE_PATH);
        return userMapper.entityToUserDetailDto(userService.getUser(token));
    }


    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordUser user) {
        LOGGER.info("RESET PASSWORD: " + BASE_PATH + " USER: {}", user);
        userService.resetPassword(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @PostMapping("send-reset-mail")
    public void sendResetMail(@RequestBody String email) {
        LOGGER.info("SEND RESET MAIL: " + BASE_PATH + " EMAIL: {}", email);
        passwordResetService.initiatePasswordReset(email);
    }
}
