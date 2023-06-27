package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUnBlockDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminEndpointTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    static final String BASE_PATH = "/api/v1/admin";


    private final ApplicationUser applicationUser =
        new ApplicationUser("marty@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%", false,
            false);


    //Initial Setup for Tests
    @BeforeEach
    @Transactional
    @Rollback
    public void beforeEach() throws ValidationException {

        // Delete existing users
        applicationUserRepository.deleteAll();

        UserCreateDto unblockedUser = new UserCreateDto(-1000L, "John@email.com", "John", "Doe", LocalDate.parse("1988-12-12"),
            "Teststreet 44/7", 1010L, "Vienna", "Password123%", false, false);

        UserCreateDto blockedUser = new UserCreateDto(-1000L, "James@email.com", "James", "Doe", LocalDate.parse("1988-12-12"),
            "Teststreet 44/7", 1010L, "Vienna", "Password123%", false, true);


        UserCreateDto admin = new UserCreateDto(-1000L, "administrator@email.com", "Admin", "admin", LocalDate.parse("1988-12-12"),
            "Teststreet 44/7", 1010L, "Vienna", "Password123%", true, false);

        userService.register(userMapper.userCreateDtoToEntity(admin));
        userService.register(userMapper.userCreateDtoToEntity(unblockedUser));
        userService.register(userMapper.userCreateDtoToEntity(blockedUser));
    }

    @Transactional
    @Test
    public void givenOneApplicationuser_whenSave_UserIsCreated() throws Exception {

        UserCreateDto userCreateDto = userMapper.entityToUserCreateDto(applicationUser);
        String body = objectMapper.writeValueAsString(userCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }


    @Transactional
    @Test
    public void givenValidUserCreateDto_whenRegisterUser_UserIsCreated() throws Exception {
        UserCreateDto userCreateDto =
            new UserCreateDto(-1000L, "john@example.com", "John", "Doe", LocalDate.parse("1988-12-12"), "Teststreet", 1010L, "Vienna", "Password123%", false,
                false);
        String requestBody = objectMapper.writeValueAsString(userCreateDto);
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isCreated());


        ApplicationUser registeredUser = applicationUserRepository.findUserByEmail("john@example.com");
        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Doe", registeredUser.getLastName());
        assertEquals(LocalDate.parse("1988-12-12"), registeredUser.getBirthdate());
    }

    @Transactional
    @Test
    public void givenValidUserCreateDtoButMissingAdminRole_whenRegisterUser_isForbidden() throws Exception {
        UserCreateDto userCreateDto =
            new UserCreateDto(-1000L, "john@example.com", "John", "Doe", LocalDate.parse("1988-12-12"), "Teststreet", 1010L, "Vienna", "Password123%", false,
                false);
        String requestBody = objectMapper.writeValueAsString(userCreateDto);
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidFirstName_whenRegisterUser_ValidationFails() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(
            -1000L, "john@example.com", "John123", "Doe", LocalDate.parse("1990-01-01"),
            "Teststreet", 1010L, "Vienna", "Password123%", false, false
        );
        String requestBody = objectMapper.writeValueAsString(userCreateDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("First name must contain only letters"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidLastName_whenRegisterUser_ValidationFails() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(
            -1000L, "john@example.com", "John", "Doe123", LocalDate.parse("1990-01-01"),
            "Teststreet", 1010L, "Vienna", "Password123%", false, false
        );
        String requestBody = objectMapper.writeValueAsString(userCreateDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Last name must contain only letters"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidBirthdate_whenRegisterUser_ValidationFails() throws Exception {
        LocalDate futureDate = LocalDate.now().plusYears(1);
        UserCreateDto userCreateDto = new UserCreateDto(
            -1000L, "john@example.com", "John", "Example", futureDate,
            "Teststreet", 1010L, "Vienna", "Password123%", false, false
        );
        String requestBody = objectMapper.writeValueAsString(userCreateDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Birthdate must be in the past"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithMultipleInvalidFields_whenRegisterUser_ValidationFails() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(
            -1000L, "john@example.com", "John123", "Example123", LocalDate.parse("1988-12-12"),
            "Teststreet", 1010L, "Vienna", "Password123%", false, false
        );
        String requestBody = objectMapper.writeValueAsString(userCreateDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[*].message",
                containsInAnyOrder("First name must contain only letters", "Last name must contain only letters")));
    }

    @Test
    public void whenBlockingValidUser_Then_UserIsLockedIsTrue() throws Exception {
        UserUnBlockDto userUnBlockDto = new UserUnBlockDto("John@email.com", true);

        String requestBody = objectMapper.writeValueAsString(userUnBlockDto);
        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        ApplicationUser blockedUser = applicationUserRepository.findUserByEmail("John@email.com");
        assertNotNull(blockedUser);
        assertEquals(Boolean.TRUE, blockedUser.getLocked());

    }

    @Test
    public void whenUnblockingValidUser_Then_UserIsLockedIsFalse() throws Exception {
        UserUnBlockDto userUnBlockDto = new UserUnBlockDto("James@email.com", false);

        String requestBody = objectMapper.writeValueAsString(userUnBlockDto);
        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        ApplicationUser unblockedUser = applicationUserRepository.findUserByEmail("James@email.com");
        assertNotNull(unblockedUser);
        assertEquals(Boolean.FALSE, unblockedUser.getLocked());
    }
}
