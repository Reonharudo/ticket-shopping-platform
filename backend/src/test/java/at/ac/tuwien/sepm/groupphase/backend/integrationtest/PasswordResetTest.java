package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PasswordResetTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    private SecurityProperties securityProperties;

    static final String BASE_PATH_ADMIN = "/api/v1/admin";
    static final String BASE_PATH_USER = "/api/v1/edit";

    private final ApplicationUser applicationUser =
        new ApplicationUser("marty@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%", false,
            false);

    @BeforeEach
    public void beforeEach() {
        String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
        applicationUser.setPassword(encodedPassword);
        applicationUserRepository.save(applicationUser);
    }

    @Transactional
    @Test
    public void testSendResetMail() throws Exception {
        String email = "marty@email.com";

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_PATH_ADMIN + "/send-reset-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(email)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );

    }

    @Test
    public void testResetPassword() throws Exception {
        String email = "marty@email.com";
        ResetPasswordUser resetPasswordUser = new ResetPasswordUser("marty@email.com", "passwordHasChanged123%", "Token");
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(resetPasswordUser.token());
        passwordResetToken.setEmail(resetPasswordUser.email());
        passwordResetToken.setExpirationTime(LocalDateTime.now().plusMinutes(15L));


        String body = objectMapper.writeValueAsString(resetPasswordUser);
        tokenRepository.save(passwordResetToken);
        MvcResult resetResult = this.mockMvc.perform(post(BASE_PATH_USER + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andDo(print())
            .andReturn();

        MockHttpServletResponse resetResultResponse = resetResult.getResponse();
        ApplicationUser afterReset = applicationUserRepository.findUserByEmail(email);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), resetResultResponse.getStatus()),
            () -> assertTrue(passwordEncoder.matches("passwordHasChanged123%", afterReset.getPassword()))
        );

    }
}
