package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.StringContains.containsString;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserRegisterEndpointTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    static final String BASE_PATH = "/api/v1/register";


    private final ApplicationUser applicationUser =
        new ApplicationUser("martin@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%", false,
            false);


    @Transactional
    @Test
    public void givenOneApplicationuser_whenSave_UserIsCreated() throws Exception {

        UserRegisterDto userRegisterDto = userMapper.entityToDto(applicationUser);
        String body = objectMapper.writeValueAsString(userRegisterDto);

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
    public void givenValidUserRegisterDto_whenRegisterUser_UserIsCreated() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto(-1000L, "john@example.com", "John", "Doe", LocalDate.parse("1988-12-12"), "Teststreet", 1010L, "Vienna", "Password123%");
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());


        ApplicationUser registeredUser = applicationUserRepository.findUserByEmail("john@example.com");
        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Doe", registeredUser.getLastName());
        assertEquals(LocalDate.parse("1988-12-12"), registeredUser.getBirthdate());
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidFirstName_whenRegisterUser_ValidationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John123", "Doe", LocalDate.parse("1990-01-01"),
            "Teststreet", 1010L, "Vienna", "Password123%"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("First name must contain only letters"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidLastName_whenRegisterUser_ValidationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John", "Doe123", LocalDate.parse("1990-01-01"),
            "Teststreet", 1010L, "Vienna", "Password123%"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Last name must contain only letters"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithInvalidBirthdate_whenRegisterUser_ValidationFails() throws Exception {
        LocalDate futureDate = LocalDate.now().plusYears(1);
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John", "Example", futureDate,
            "Teststreet", 1010L, "Vienna", "Password123%"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Birthdate must be in the past"));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithMultipleInvalidFields_whenRegisterUser_ValidationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John123", "Example123", LocalDate.parse("1988-12-12"),
            "Teststreet", 1010L, "Vienna", "Password123%"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[*].message",
                containsInAnyOrder("First name must contain only letters", "Last name must contain only letters")));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithWrongPasswordRequirements_whenRegisterUser_ValidationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John", "Example", LocalDate.parse("1988-12-12"),
            "Teststreet", 1010L, "Vienna", "PasswordIsWeak"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value(containsString("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")));
    }

    @Transactional
    @Test
    public void givenUserRegisterDtoWithCorrectPasswordRequirements_whenRegisterUser_UserIsRegistered() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
            -1000L, "john@example.com", "John", "Example", LocalDate.parse("1988-12-12"),
            "Teststreet", 1010L, "Vienna", "Password123%"
        );
        String requestBody = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        ApplicationUser registeredUser = applicationUserRepository.findUserByEmail("john@example.com");
        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Example", registeredUser.getLastName());
        assertEquals(LocalDate.parse("1988-12-12"), registeredUser.getBirthdate());

    }

}
