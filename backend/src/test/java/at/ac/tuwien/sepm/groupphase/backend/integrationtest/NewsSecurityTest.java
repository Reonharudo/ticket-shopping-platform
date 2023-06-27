package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsImageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NewsSecurityTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private NewsImageRepository newsImageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private News news;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        newsRepository.deleteAll();
        newsImageRepository.deleteAll();

        news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText(TEST_NEWS_TEXT)
            .withCreatedAt(TEST_NEWS_PUBLISHED_AT)
            .withCoverImage(TEST_COVER_IMAGE)
            .build();

        NewsImage img1 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(0)).build();
        NewsImage img2 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(1)).build();
        NewsImage img3 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(2)).build();
        List<NewsImage> testImageList = new LinkedList<>(Arrays.asList(img1,img2,img3));

        news.setImages(testImageList);

        ApplicationUser user = new ApplicationUser();
        user.setEmail(DEFAULT_USER);
        user.setAdmin(false);

        ApplicationUser admin = new ApplicationUser();
        admin.setEmail(ADMIN_USER);
        admin.setAdmin(true);

        userRepository.save(user);
        userRepository.save(admin);
    }

    @Test
    void givenUserLoggedIn_whenFindAll_then200() throws Exception {
        // default pageIndex is 0
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("loadAlreadyRead","true"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    void givenNoOneLoggedIn_whenFindAll_then403() throws Exception {
        // default pageIndex is 0
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI).param("loadAlreadyRead", "true"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenAdminLoggedIn_whenPost_then201() throws Exception {
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void givenNoOneLoggedIn_whenPost_then403() throws Exception {
        news.setCreatedAt(null);
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenUserLoggedIn_whenPost_then403() throws Exception {
        news.setCreatedAt(null);
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenNoOneLoggedIn_whenFindOneById_then403() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + '/' + id))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenUserLoggedInAndNewsWithIdInDatabase_whenFindOneById_then200() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + '/' + id)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    void givenNoOneLoggedIn_whenDeleteOneById_then403() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + '/' + id))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenUserLoggedInAndNewsWithIdInDatabase_whenDeleteOneById_then403() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + '/' + id)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void givenAdminLoggedInAndNewsWithIdInDatabase_whenDeleteOneById_then200() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + '/' + id)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    void givenNoOneLoggedInAndNewsWithIdInDatabase_whenPutOneById_then403() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(put(NEWS_BASE_URI + '/' + id))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    void givenUserLoggedInAndNewsWithIdInDatabaseAndUserInDatabase_whenPutOneById_then201() throws Exception {
        newsRepository.save(news);
        Long id = news.getId();

        MvcResult mvcResult = this.mockMvc.perform(put(NEWS_BASE_URI + '/' + id)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }
}