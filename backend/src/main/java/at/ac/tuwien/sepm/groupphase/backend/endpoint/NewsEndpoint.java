package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.AbbreviatedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.DetailedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = NewsEndpoint.BASE_PATH)
public class NewsEndpoint {

    static final String BASE_PATH = "/api/v1/news";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final UserService userService;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper, UserService userService) {
        this.newsService = newsService;
        this.userService = userService;
        this.newsMapper = newsMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new news-entry", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto create(@Valid @RequestBody NewsInquiryDto newsDto) {
        LOGGER.info("POST {} body: {}", BASE_PATH, newsDto);

        return newsMapper.newsToDetailedNewsDto(
            newsService.publishNews(newsDto));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<AbbreviatedNewsDto> findAll(@RequestParam(defaultValue = "0") int pageIndex,
                                            @RequestParam(defaultValue = "false") boolean loadAlreadyRead,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        LOGGER.info("GET {}?pageIndex={}&loadAlreadyRead={}", BASE_PATH, pageIndex, loadAlreadyRead);

        return newsService.findAllPagedByCreatedAt(pageIndex, loadAlreadyRead, userService.getUser(token)).map(newsMapper::newsToAbbreviatedNewsDto).toList();
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    @Operation(summary = "Get detailed news by id without short description", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto findOneById(@PathVariable Long id) {
        LOGGER.info("GET {}/{}", BASE_PATH, id);

        return newsMapper.newsToDetailedNewsDto(newsService.getById(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}")
    @Operation(summary = "Delete news entry with the given id", security = @SecurityRequirement(name = "apiKey"))
    public void deleteOneById(@PathVariable Long id) {
        LOGGER.info("DELETE {}/{}", BASE_PATH, id);

        newsService.deleteById(id);
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    @Operation(summary = "Set news entry relation to user", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> putRelation(@PathVariable Long id,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        LOGGER.info("PUT {}/{}", BASE_PATH, id);

        ApplicationUser user = userService.getUser(token);
        return newsService.putRelation(id, user);
    }
}
