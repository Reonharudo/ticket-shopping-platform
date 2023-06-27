package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage;

public interface NewsImageService {

    /**
     * Store a newsImage entry.
     *
     * @param newsImage to persist
     * @return stored newsImage entry
     */
    NewsImage storeNewsImage(NewsImage newsImage);
}
