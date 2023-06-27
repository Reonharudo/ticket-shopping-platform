package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ApplicationUserEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.RegisterEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDeleteDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.AbbreviatedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
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
import org.springframework.http.HttpHeaders;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ApplicationUserEndpointTest implements TestData {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    static final String BASE_PATH = ApplicationUserEndpoint.BASE_PATH;

    static final String REGISTER_PATH = RegisterEndpoint.BASE_PATH;

    private final UserCreateDto testUser1 = new UserCreateDto(-1000L, "John@email.com", "John", "Doe", LocalDate.parse("1988-12-12"),
        "Teststreet 44/7", 1010L, "Vienna", "Password123%", false, false);

    private final UserCreateDto testUser2 = new UserCreateDto(-1000L, "James@email.com", "James", "Doe", LocalDate.parse("1988-12-12"),
        "Teststreet 44/7", 1010L, "Vienna", "Password123%", false, true);

    //Initial Setup for Tests
    @BeforeEach
    @Transactional
    public void beforeEach() throws ValidationException {

        // Delete existing users
        applicationUserRepository.deleteAll();
        userService.register(userMapper.userCreateDtoToEntity(testUser1));
        userService.register(userMapper.userCreateDtoToEntity(testUser2));
    }


    @Test
    @Transactional
    public void givenUserDetailDtoAndAuthorizationToken_whenUpdateUser_thenUpdateUser() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        MvcResult mvcResult = this.mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    @Transactional
    public void givenUserDetailDtoAndAuthorizationTokenButWrongPassword_whenUpdateUser_isForbidden() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna", "ThisIsAWrongPassword123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    public void givenUserDetailDtoWithWrongFirstNameAndAuthorizationToken_Then_ValidationException() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan123", "Frakes", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("First name must contain only letters"));
    }

    @Test
    @Transactional
    public void givenUserDetailDtoWithWrongLastNameAndAuthorizationToken_Then_ValidationException() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes123", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Last name must contain only letters"));
    }

    @Test
    @Transactional
    public void givenUserDetailDtoWithWrongAreaCodeAndAuthorizationToken_Then_ValidationException() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", -10000L, "Vienna", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Area Code must be a positive number"));
    }

    @Test
    @Transactional
    public void givenUserDetailDtoWithWrongCityNameAndAuthorizationToken_Then_ValidationException() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes", LocalDate.parse("1988-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna123", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("City name must contain only letters"));
    }

    @Test
    @Transactional
    public void givenUserDetailDtoWithBirthdateInTheFutureAndAuthorizationToken_Then_ValidationException() throws Exception {
        UserDetailDto userDetailDto = new UserDetailDto(
            1L, false, "John@email.com", "Jonathan", "Frakes", LocalDate.parse("2030-12-12"),
            "Blumenstraße 1-2/44", 10000L, "Vienna", "Password123%", false
        );
        //Receive the token for the user before performing edit
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("John@email.com");
        userLoginDto.setPassword("Password123%");
        String token = userService.login(userLoginDto);

        mockMvc.perform(put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDetailDto)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].message").value("Birthdate must be in the past"));
    }

    @Test
    @Transactional
    public void giveUserDeleteDtoWithExistingUser_WhenDelete_ThenDeleteUser() throws Exception {

        UserDeleteDto userDeleteDto = new UserDeleteDto(testUser1.id(), testUser1.email(), testUser1.password());
        //Receive the token for the user before performing delete
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(testUser1.email());
        userLoginDto.setPassword(testUser1.password());
        String token = userService.login(userLoginDto);

        mockMvc.perform(delete(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(userDeleteDto)))
            .andExpect(status().isOk());

        // Assert that the user no longer exists in the repository
        assertFalse(applicationUserRepository.existsById(testUser1.id()));
    }

    @Test
    public void givenApplicationUsersWithNewsRead_whenDelete_thenGetAllLengthIs0And200() throws Exception {
        applicationUserRepository.deleteAll();

        UserRegisterDto user = new UserRegisterDto(null, DEFAULT_USER, "Martin",
            "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L,
            "Vienna", "passworD123%");

        String registerBody = objectMapper.writeValueAsString(user);

        MvcResult mvcResultPost = this.mockMvc.perform(post(REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse responsePost = mvcResultPost.getResponse();

        // List<ApplicationUser> userList = userRepository.findAll();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), responsePost.getStatus()),
            () -> assertEquals(applicationUserRepository.findAll().size(),1)
        );

        ApplicationUser admin = new ApplicationUser();
        admin.setEmail(ADMIN_USER);
        admin.setAdmin(true);

        applicationUserRepository.save(admin);

        assertEquals(applicationUserRepository.findAll().size(),2);

        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        newsRepository.save(news);

        assertEquals(newsRepository.findAll().size(),1);


        MvcResult mvcResultPut1 = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut1 = mvcResultPut1.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responsePut1.getStatus());

        MvcResult mvcResultPut2 = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut2 = mvcResultPut2.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responsePut2.getStatus());

        UserDeleteDto userDeleteDto = new UserDeleteDto(applicationUserRepository.findUserByEmail(DEFAULT_USER).getId(),
            DEFAULT_USER, "passworD123%");

        String deleteBody = objectMapper.writeValueAsString(userDeleteDto);

        MvcResult mvcResult = this.mockMvc.perform(delete(BASE_PATH)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(deleteBody))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());


        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead","true"))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // map json string to list:
        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(1, abbreviatedNewsDtos.size());


        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("loadAlreadyRead","false"))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

}
