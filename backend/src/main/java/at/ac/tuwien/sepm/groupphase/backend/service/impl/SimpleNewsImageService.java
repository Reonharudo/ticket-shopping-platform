package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsImageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SimpleNewsImageService implements NewsImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsImageRepository newsImageRepository;

    public SimpleNewsImageService(NewsImageRepository newsImageRepository) {
        this.newsImageRepository = newsImageRepository;
    }

    @Override
    public NewsImage storeNewsImage(NewsImage newsImage) {
        LOGGER.debug("Store new newsImage {}", newsImage);
        return newsImageRepository.save(newsImage);
    }
}
