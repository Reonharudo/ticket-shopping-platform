package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
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

import java.time.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NewsEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsImageRepository newsImageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private News news;

    private final Event event = new Event();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        newsRepository.deleteAll();
        newsImageRepository.deleteAll();
        eventRepository.deleteAll();

        event.setCategory("Rock");
        event.setArtist("Queen");
        event.setDuration(LocalTime.now());
        event.setTitle("Live Aid");
        eventRepository.save(event);

        news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText(TEST_NEWS_TEXT)
            .withCoverImage(TEST_COVER_IMAGE)
            .withEvent(event)
            .build();

        NewsImage img1 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(0)).build();
        NewsImage img2 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(1)).build();
        NewsImage img3 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(2)).build();
        List<NewsImage> testImageList = new LinkedList<>(Arrays.asList(img1, img2, img3));

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
    void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        // default pageIndex is 0
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead", "false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // map json string to list:
        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());
    }

    @Test
    void givenOneNews_whenFindAll_thenListWithSizeOneAndNewsWithAllPropertiesExceptFullTextAndImages()
        throws Exception {
        // default pageIndex is 0
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead", "false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(1, abbreviatedNewsDtos.size());
        AbbreviatedNewsDto abbreviatedNewsDto = abbreviatedNewsDtos.get(0);
        assertAll(
            () -> assertEquals(TEST_NEWS_TITLE, abbreviatedNewsDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, abbreviatedNewsDto.getShortText()),
            () -> assertEquals(TEST_COVER_IMAGE, abbreviatedNewsDto.getCoverImage()),
            () -> assertEquals(TEST_NEWS_IMAGE_DATA_LIST, newsImageRepository.findAll().stream().map(NewsImage::getImageData).toList())
        );
    }

    @Test
    void given21News_whenFindAllPage0AndPage1_thenListWithSize20AndListWithSize1()
        throws Exception {
        for (int i = 0; i < 21; i++) {
            news.setId((long) -i);
            newsRepository.save(news);
        }
        assertEquals(21, newsRepository.findAll().size());

        // default pageIndex is 0
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead", "false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(20, abbreviatedNewsDtos.size());

        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI).param("pageIndex", "1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead", "false"))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(1, abbreviatedNewsDtos.size());
    }

    @Test
    void given3NewsOneRead_whenFindAllReadAndFindAllNotRead_thenListWithSize1AndListWithSize2()
        throws Exception {

        News news1 = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        News news2 = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        News news3 = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        newsRepository.save(news1);
        newsRepository.save(news2);
        newsRepository.save(news3);

        assertEquals(3, newsRepository.findAll().size());

        MvcResult mvcResultPut = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news3.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut = mvcResultPut.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responsePut.getStatus());

        // default pageIndex is 0
        MvcResult mvcResultLoad = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead","false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responseLoad = mvcResultLoad.getResponse();

        assertEquals(HttpStatus.OK.value(), responseLoad.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, responseLoad.getContentType());

        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(responseLoad.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(2, abbreviatedNewsDtos.size());

        mvcResultLoad = this.mockMvc.perform(get(NEWS_BASE_URI).param("pageIndex", "0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead","true"))
            .andDo(print())
            .andReturn();
        responseLoad = mvcResultLoad.getResponse();

        assertEquals(HttpStatus.OK.value(), responseLoad.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, responseLoad.getContentType());

        abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(responseLoad.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(1, abbreviatedNewsDtos.size());
    }

    @Test
    void given1News_whenPutNewsReadRelationAndAgainNewsReadRelation_then201And200()
        throws Exception {

        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        newsRepository.save(news);

        assertEquals(1, newsRepository.findAll().size());

        MvcResult mvcResultPut1 = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut1 = mvcResultPut1.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responsePut1.getStatus());

        MvcResult mvcResultPut2 = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut2 = mvcResultPut2.getResponse();

        assertEquals(HttpStatus.OK.value(), responsePut2.getStatus());
    }

    @Test
    void given1News_whenPutNewsReadRelationUserNotFound_then404()
        throws Exception {

        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText("")
            .build();

        newsRepository.save(news);

        assertEquals(1, newsRepository.findAll().size());

        MvcResult mvcResultPut = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("invalid@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut = mvcResultPut.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), responsePut.getStatus());
    }

    @Test
    void givenNothing_whenPutNewsReadRelation_then404()
        throws Exception {

        MvcResult mvcResultPut = this.mockMvc.perform(put(NEWS_BASE_URI + "/" + -1000)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responsePut = mvcResultPut.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), responsePut.getStatus());
    }

    @Test
    void givenOneNews_whenFindById_thenNewsWithAllPropertiesExceptShortText() throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        DetailedNewsDto detailedNewsDto = objectMapper.readValue(response.getContentAsString(),
            DetailedNewsDto.class);

        assertAll(
            () -> assertEquals(detailedNewsDto, newsMapper.newsToDetailedNewsDto(news)),
            () -> assertEquals(detailedNewsDto.getImages(), news.getImages().stream().map(NewsImage::getImageData).toList())
        );
    }

    @Test
    void givenNothing_whenFindByNonExistingId_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void givenOneNews_whenDeleteById_thenGetAllLengthIs0And200() throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead", "false"))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // map json string to list:
        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());
    }

    @Test
    void givenOneNewsWithNewsRead_whenDeleteById_thenGetAllLengthIs0And200() throws Exception {
        newsRepository.save(news);

        MvcResult mvcResultAdmin = this.mockMvc.perform(put(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responseAdmin = mvcResultAdmin.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responseAdmin.getStatus());

        MvcResult mvcResultUser = this.mockMvc.perform(put(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse responseUser = mvcResultUser.getResponse();

        assertEquals(HttpStatus.CREATED.value(), responseUser.getStatus());

        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        final MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(2, userRepository.findAll().size())
        );

        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead","false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response2 = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response2.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response2.getContentType());

        // map json string to list:
        List<AbbreviatedNewsDto> abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response2.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());

        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("loadAlreadyRead","true"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response3 = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response3.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response3.getContentType());

        // map json string to list:
        abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response3.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());

        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("loadAlreadyRead","false"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response4 = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response4.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response4.getContentType());

        // map json string to list:
        abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response4.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());

        // default pageIndex is 0
        mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("loadAlreadyRead","true"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response5 = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response5.getStatus());
        // response in json format:
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response5.getContentType());

        // map json string to list:
        abbreviatedNewsDtos = Arrays.asList(objectMapper.readValue(response5.getContentAsString(),
            AbbreviatedNewsDto[].class));

        assertEquals(0, abbreviatedNewsDtos.size());
    }

    @Test
    void givenNothing_whenDeleteByNonExistingId_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(NEWS_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void givenNothing_whenPost_thenNewsWithAllSetPropertiesPlusIdAndPublishedDate() throws Exception {
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
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedNewsDto detailedNewsDto = objectMapper.readValue(response.getContentAsString(),
            DetailedNewsDto.class);

        assertNotNull(detailedNewsDto.getId());
        assertNotNull(detailedNewsDto.getCreatedAt());
        assertTrue(isNow(detailedNewsDto.getCreatedAt()));
        //Set generated properties to null to make the response comparable with the original input
        detailedNewsDto.setId(null);
        detailedNewsDto.setCreatedAt(null);

        assertAll(
            () -> assertEquals(detailedNewsDto, newsMapper.newsToDetailedNewsDto(news)),
            () -> assertEquals(detailedNewsDto.getImages(), news.getImages().stream().map(NewsImage::getImageData).toList())
        );
    }

    @Test
    void givenNothing_whenPostNewsAndTitleBlank_then422() throws Exception {
        news.setTitle("        ");
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors[0].message").value("Title must not be blank"))
            .andReturn();
    }

    @Test
    void givenNothing_whenPostNewsAndShortDescriptionBlank_then422() throws Exception {
        news.setShortText("        ");
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors[0].message").value("Short Text must not be blank"))
            .andReturn();
    }

    @Test
    void givenNothing_whenPostInvalid_then422() throws Exception {
        news.setTitle(null);
        news.setShortText(null);
        news.setFullText(null);
        news.setCoverImage("");
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors[*].message", containsInAnyOrder("Title must not be null",
                "Title must not be blank", "Short Text must not be null", "Short Text must not be blank",
                "Full Text must not be null", "Cover Image is not a valid base64 picture")))
            .andReturn();
    }

    @Test
    void givenNothing_whenPostInvalidCoverImage_then422() throws Exception {
        news.setCoverImage("I_AM_A_COVER_IMAGE");
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors[0].message").value("Cover Image is not a valid base64 picture"))
            .andReturn();
    }

    @Test
    void givenNothing_whenPostInvalidImages_then422() throws Exception {
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        newsInquiryDto.setImages(Arrays.asList("I_AM_IMAGE_ONE", "I_AM_IMAGE_TWO", "I_AM_IMAGE_THREE"));
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors", containsInAnyOrder(
                Map.of("field", "images[0]", "message", "An additional image is not a valid base64 picture"),
                Map.of("field", "images[1]", "message", "An additional image is not a valid base64 picture"),
                Map.of("field", "images[2]", "message", "An additional image is not a valid base64 picture"))))
            .andReturn();
    }

    @Test
    void givenNothing_whenPostInvalidEventId_then404() throws Exception {
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        newsInquiryDto.setEventId(-420L);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    void givenNothing_whenPostStringsTooLong_then422() throws Exception {
        news.setTitle("a".repeat(51));
        news.setShortText("a".repeat(101));
        news.setFullText("a".repeat(10001));
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String body = objectMapper.writeValueAsString(newsInquiryDto);

        this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(jsonPath("$.errors", containsInAnyOrder(
                Map.of("field", "title", "message", "size must be between 0 and 50"),
                Map.of("field", "shortText", "message", "size must be between 0 and 100"),
                Map.of("field", "fullText", "message", "size must be between 0 and 10000"))))
            .andReturn();
    }

    // bad if test is run between two hours
    private boolean isNow(LocalDateTime date) {
        LocalDateTime today = LocalDateTime.now();
        return date.getYear() == today.getYear() && date.getDayOfYear() == today.getDayOfYear() &&
            date.getHour() == today.getHour();
    }
}
