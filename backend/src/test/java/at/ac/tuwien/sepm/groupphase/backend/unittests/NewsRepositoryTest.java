package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
class NewsRepositoryTest implements TestData {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private NewsImageRepository newsImageRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ApplicationUserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    private static final Event event = new Event();

    @BeforeEach
    public void clearDatabase() {
        userRepository.deleteAll();
        newsRepository.deleteAll();
        newsImageRepository.deleteAll();
        eventRepository.deleteAll();

        event.setCategory("Rock");
        event.setArtist("Queen");
        event.setDuration(LocalTime.now());
        event.setTitle("Live Aid");
        eventRepository.save(event);
    }

    @Test
    void givenNothing_whenSaveNewsWithoutImages_thenFindListWithOneElementAndFindNewsByIdAndFindNoImagesInNewsImageRepository() {
        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText(TEST_NEWS_TEXT)
            // cannot be null:
            .withImages(new LinkedList<>())
            .build();

        newsRepository.save(news);

        // test if news present
        assertAll(
            () -> assertEquals(1, newsRepository.findAll().size()),
            () -> assertEquals(0, newsImageRepository.findAll().size()),
            () -> assertNotNull(newsRepository.findById(news.getId())),

        // test properties of news if present
            () -> assertNotNull(newsRepository.findById(news.getId()).get().getCreatedAt()),
            () -> assertNull(newsRepository.findById(news.getId()).get().getCoverImage()),
            () -> assertNull(newsRepository.findById(news.getId()).get().getEvent()),
            () -> assertTrue(newsRepository.findById(news.getId()).get().getImages().isEmpty()),
            () -> assertEquals(TEST_NEWS_TITLE, newsRepository.findById(news.getId()).get().getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, newsRepository.findById(news.getId()).get().getShortText()),
            () -> assertEquals(TEST_NEWS_TEXT, newsRepository.findById(news.getId()).get().getFullText())
        );
    }

    @Test
    void givenNothing_whenSaveNewsWithCoverImageAndAdditionalImages_thenFindListWithOneElementAndFindNewsByIdAndFindImagesInNewsImageRepository() {
        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText(TEST_NEWS_TEXT)
            .withCoverImage(TEST_COVER_IMAGE)
            .withEvent(event)
            .build();

        NewsImage img1 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(0)).build();
        NewsImage img2 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(1)).build();
        NewsImage img3 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(2)).build();
        List<NewsImage> testImageList = new LinkedList<>(Arrays.asList(img1,img2,img3));

        news.setImages(testImageList);

        newsRepository.save(news);

        // test if news present
        assertAll(
            () -> assertEquals(1, newsRepository.findAll().size()),
            () -> assertEquals(3, newsImageRepository.findAll().size()),
            () -> assertNotNull(newsRepository.findById(news.getId())),

        // test properties of news if present
            () -> assertNotNull(newsRepository.findById(news.getId()).get().getCreatedAt()),
            () -> assertNotNull(newsRepository.findById(news.getId()).get().getCoverImage()),
            () -> assertEquals(TEST_COVER_IMAGE, newsRepository.findById(news.getId()).get().getCoverImage()),
            () -> assertEquals(3, newsRepository.findById(news.getId()).get().getImages().size()),
            () -> assertEquals(TEST_NEWS_TITLE, newsRepository.findById(news.getId()).get().getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, newsRepository.findById(news.getId()).get().getShortText()),
            () -> assertEquals(TEST_NEWS_TEXT, newsRepository.findById(news.getId()).get().getFullText()),
            () -> assertEquals(event, newsRepository.findById(news.getId()).get().getEvent())
        );
    }

    @Test
    void givenNothing_whenSave3NewsOneRead_thenFindListWithTwoElementsAndFindTwoNews() {

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

        // user is created in method because saving a user outside the method before adding the news would prevent jpa
        // from accessing the new version
        ApplicationUser user = new ApplicationUser();
        user.setEmail(DEFAULT_USER);
        user.setAdmin(false);

        userRepository.save(user);

        news3.addUser(user);
        user.addNews(news3);

        newsRepository.save(news3);

        // write changes from cache to database
        entityManager.flush();
        // clear first level cache so that subsequent find calls hit the database
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        assertAll(
            () -> assertEquals(1, newsRepository.findNewsByReadByUsersId(user.getId(), pageable).stream().toList().size()),
            () -> assertEquals(2, newsRepository.findAllNotRead(user.getId(), pageable).stream().toList().size())
        );
    }

    @Test
    void givenNothing_whenSaveNewsWithBlankTitle_thenThrowException() {
        News news = News.NewsBuilder.aNews()
            .withTitle("       ")
            .withShortText(TEST_NEWS_SUMMARY)
            .withFullText(TEST_NEWS_TEXT)
            .withCoverImage(TEST_COVER_IMAGE)
            .build();

        NewsImage img1 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(0)).build();
        NewsImage img2 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(1)).build();
        NewsImage img3 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(2)).build();
        List<NewsImage> testImageList = new LinkedList<>(Arrays.asList(img1,img2,img3));

        news.setImages(testImageList);

        assertThrows(ConstraintViolationException.class, () -> newsRepository.save(news));
    }

    @Test
    void givenNothing_whenSaveNewsWithBlankSummary_thenThrowException() {
        News news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withShortText("       ")
            .withFullText(TEST_NEWS_TEXT)
            .withCoverImage(TEST_COVER_IMAGE)
            .build();

        NewsImage img1 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(0)).build();
        NewsImage img2 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(1)).build();
        NewsImage img3 = NewsImage.NewsImageBuilder.aNewsImage().withNews(news).withImageData(TEST_NEWS_IMAGE_DATA_LIST.get(2)).build();
        List<NewsImage> testImageList = new LinkedList<>(Arrays.asList(img1,img2,img3));

        news.setImages(testImageList);

        assertThrows(ConstraintViolationException.class, () -> newsRepository.save(news));
    }

    // getById, delete and findNewsByReadByUsersId are JPA methods and therefore not tested in this layer
    // putRelation is realized only by jpa methods and therefore not tested in this layer
}
