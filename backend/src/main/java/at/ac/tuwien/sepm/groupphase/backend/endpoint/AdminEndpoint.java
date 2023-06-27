package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUnBlockDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.PasswordResetService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = AdminEndpoint.BASE_PATH)
public class AdminEndpoint {

    static final String BASE_PATH = "/api/v1/admin";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    private final UserMapper userMapper;
    private final PasswordResetService passwordResetService;

    @Autowired
    public AdminEndpoint(UserService userService, UserMapper userMapper, PasswordResetService passwordResetService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = "apiKey"))
    public UserCreateDto post(@Valid @RequestBody UserCreateDto userCreateDto) {
        LOGGER.info("POST: " + BASE_PATH + "USER: {}", userCreateDto);
        try {
            return userMapper.entityToUserCreateDto(userService.register(userMapper.userCreateDtoToEntity(userCreateDto)));
        } catch (ValidationException e) {
            LOGGER.error("User could not be validated", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN"})
    @PostMapping("send-reset-mail")
    public void resetPassword(@RequestBody String email) {
        LOGGER.info("SEND RESET MAIL: " + BASE_PATH + "MAIL: {}", email);
        passwordResetService.initiatePasswordReset(email);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Block/Unblock a user", security = @SecurityRequirement(name = "apiKey"))
    public void put(@Valid @RequestBody UserUnBlockDto userUnBlockDto) {
        LOGGER.info("BLOCK|UNBLOCK USER " + BASE_PATH + "USER: {}", userUnBlockDto.email());
        userService.block(userMapper.userUnBlockDtoToEntity(userUnBlockDto));
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<UserUnBlockDto> getBlockedUsers(UserUnBlockDto userUnBlockDto, @RequestParam(defaultValue = "0") int pageIndex,
                                                @RequestHeader("Authorization") String token) {
        LOGGER.info("GET BLOCKED USERS " + BASE_PATH);
        return userMapper.entityToStreamUserUnBlockDto(userService.getBlockedUsers(userMapper.userUnBlockDtoToEntity(userUnBlockDto), token, pageIndex));
    }


}
