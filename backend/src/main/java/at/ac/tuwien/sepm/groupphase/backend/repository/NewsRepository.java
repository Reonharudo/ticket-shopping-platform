package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Find all news entries specified by {@code pageable} that the user with the id {@code id} has already read.
     *
     * @param userId   specifies the user who read the news entries
     * @param pageable specifies parameters and index of page
     * @return ordered page of news entries
     */
    @NonNull
    Page<News> findNewsByReadByUsersId(@NonNull Long userId, @NonNull Pageable pageable);

    /**
     * Find all news entries specified by {@code pageable} that the user with the id {@code id} hasn't already read.
     *
     * @param userId   specifies the user who has not seen the news entries
     * @param pageable specifies parameters and index of page
     * @return ordered page of news entries
     */
    @NonNull
    @Query("SELECT distinct n from News n WHERE n.id not in (SELECT n.id from ApplicationUser u join u.readNews n where u.id = :userId)")
    Page<News> findAllNotRead(@NonNull @Param("userId") Long userId, @NonNull Pageable pageable);
}
