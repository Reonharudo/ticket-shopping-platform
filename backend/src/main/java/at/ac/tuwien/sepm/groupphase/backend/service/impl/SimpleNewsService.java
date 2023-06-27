package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SimpleNewsService implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final ApplicationUserRepository userRepository;
    private final NewsMapper newsMapper;

    public SimpleNewsService(NewsRepository newsRepository,
                             EventRepository eventRepository,
                             ApplicationUserRepository userRepository,
                             NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.newsMapper = newsMapper;
    }

    @Override
    @Transactional
    public News publishNews(NewsInquiryDto newsDto) {

        Long eventId = newsDto.getEventId();

        News news = newsMapper.newsInquiryDtoWithImagesToNewsWithoutEvent(newsDto);

        if (eventId != null) {

            if (!eventRepository.existsById(eventId)) {
                throw new NotFoundException("Event not found with id: " + eventId);
            }

            Optional<Event> eventOpt = eventRepository.findById(eventId);

            if (eventOpt.isPresent()) {
                news.setEvent(eventOpt.get());
            } else {
                news.setEvent(null);
            }
        } else {
            news.setEvent(null);
        }

        LOGGER.debug("Publish new news {}", news);

        for (NewsImage img : news.getImages()) {
            img.setNews(news);
        }

        return newsRepository.save(news);
    }

    @Override
    @Transactional
    public Page<News> findAllPagedByCreatedAt(int pageIndex, boolean loadAlreadyRead, ApplicationUser user) {
        Pageable pageable = PageRequest.of(pageIndex, 20, Sort.by("createdAt").descending());

        if (user == null) {
            throw new NotFoundException("The user specified by the token was not found");
        }

        Long userId = user.getId();

        LOGGER.debug("Find all news entries by pageable: {}, userId: {}, loadAlreadyRead: {}", pageable, userId, loadAlreadyRead);

        if (loadAlreadyRead) {
            return newsRepository.findNewsByReadByUsersId(userId, pageable);
        } else {
            return newsRepository.findAllNotRead(userId, pageable);
        }
    }

    @Override
    @Transactional
    public News getById(Long id) {
        LOGGER.debug("Find news by id {}", id);
        Optional<News> newsOptional = newsRepository.findById(id);
        if (newsOptional.isPresent()) {
            News news = newsOptional.get();
            news.getImages().size(); // lazy loading
            return news;
        } else {
            throw new NotFoundException(String.format("Could not find news with id %d", id));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        LOGGER.debug("Delete news by id {}", id);

        Optional<News> newsOpt = newsRepository.findById(id);

        if (newsOpt.isEmpty()) {
            throw new NotFoundException(String.format("News not found with id: %d", id));
        }

        News news = newsOpt.get();

        // get all users who read this news
        Set<ApplicationUser> users = new HashSet<>(news.getUsers());

        for (ApplicationUser user : users) {
            user.remove(news);
            news.remove(user);

            userRepository.save(user);
        }

        newsRepository.save(news);
        newsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> putRelation(Long id, ApplicationUser user) {

        if (user == null) {
            throw new NotFoundException("The user specified by the token was not found");
        }

        LOGGER.debug("Put news relation with newsId {} and userId {}", id, user.getId());

        Optional<News> newsOptional = newsRepository.findById(id);

        if (newsOptional.isPresent()) {
            News news = newsOptional.get();
            if (!user.hasRead(news)) {
                news.addUser(user);
                user.addNews(news);
                newsRepository.save(news);

                String uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/user/{userId}").buildAndExpand(user.getId())
                    .toUriString();
                return ResponseEntity.created(URI.create(uri)).build();
            } else {
                return ResponseEntity.ok().build();
            }
        } else {
            throw new NotFoundException(String.format("Could not find news with id %d", id));
        }
    }
}
