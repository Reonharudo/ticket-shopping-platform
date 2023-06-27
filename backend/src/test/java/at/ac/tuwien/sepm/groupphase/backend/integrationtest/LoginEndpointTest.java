package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginEndpointTest {


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


    @BeforeEach
    public void beforeEach() throws ValidationException {

        // Delete existing users
        applicationUserRepository.deleteAll();

        UserCreateDto loginUser = new UserCreateDto(-1000L, "John@email.com", "John", "Doe", LocalDate.parse("1988-12-12"),
            "Teststreet 44/7", 1010L, "Vienna", "Password123%", false, false);

        UserCreateDto loginAdmin = new UserCreateDto(-1000L, "James@email.com", "James", "Doe", LocalDate.parse("1988-12-12"),
            "Teststreet 44/7", 1010L, "Vienna", "Password123%", true, false);

        userService.register(userMapper.userCreateDtoToEntity(loginUser));
        userService.register(userMapper.userCreateDtoToEntity(loginAdmin));

    }

    @Test
    @Transactional
    public void testSuccessfulLogin() throws Exception {
        //Create valid UserLoginDto
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");

        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult result = mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String token = result.getResponse().getContentAsString();

        assertTrue(isValidToken(token));
    }

    @Test
    public void testUnsuccessfulLogin() throws Exception {
        //Create valid UserLoginDto
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("PasswordIsWrong");

        String body = objectMapper.writeValueAsString(userLoginDto);

        int attempts = 5;

        for (int attempt = 1; attempt <= attempts; attempt++) {

            mockMvc.perform(post("/api/v1/authentication")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andReturn();

            if (attempt == attempts) {
                ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());
                assertTrue(applicationUser.getLocked());
            } else {
                ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());
                assertFalse(applicationUser.getLocked());
            }
        }
    }

    @Test
    @Transactional
    public void testSuccessfulLoginAdmin() throws Exception {
        //Create valid UserLoginDto
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("James@email.com");
        userLoginDto.setPassword("Password123%");

        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult result = mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String token = result.getResponse().getContentAsString();
        ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());
        assertAll(
            () -> assertTrue(isValidToken(token)),
            () -> assertFalse(applicationUser.getLocked()),
            () -> assertTrue(applicationUser.getAdmin())
        );
    }

    @Test
    public void testUnsuccessfulLoginAdmin() throws Exception {
        //Create valid UserLoginDto
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("James@email.com");
        userLoginDto.setPassword("PasswordIsWrong");

        String body = objectMapper.writeValueAsString(userLoginDto);

        int attempts = 5;
        for (int attempt = 1; attempt <= attempts; attempt++) {

            mockMvc.perform(post("/api/v1/authentication")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andReturn();

            if (attempt == attempts) {
                ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());
                assertFalse(applicationUser.getLocked());
            } else {
                ApplicationUser applicationUser = applicationUserRepository.findUserByEmail(userLoginDto.getEmail());
                assertFalse(applicationUser.getLocked());
            }
        }
    }

    private boolean isValidToken(String token) {
        //Regular Expression to check token
        String tokenPattern = "^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$";
        return token.matches(tokenPattern);
    }
}
