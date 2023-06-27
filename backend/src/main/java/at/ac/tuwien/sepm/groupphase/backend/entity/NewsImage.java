package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

@Entity
public class NewsImage {

    private static final String BASE64PATTERN = "^data:image/(gif|png|jpeg|webp|svg\\+xml);base64,.*={0,2}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Pattern(regexp = BASE64PATTERN)
    @Column(name = "image_data", columnDefinition = "BLOB")
    private String imageData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage newsImage)) {
            return false;
        }
        return Objects.equals(id, newsImage.id)
            && Objects.equals(imageData, newsImage.imageData)
            && Objects.equals(news, newsImage.news);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageData);
    }

    @Override
    public String toString() {
        return "NewsImage{"
            + "id=" + id
            + '}';
    }


    public static final class NewsImageBuilder {
        private Long id;
        private String imageData;
        private News news;

        private NewsImageBuilder() {
        }

        public static at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage.NewsImageBuilder aNewsImage() {
            return new at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage.NewsImageBuilder();
        }

        public at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage.NewsImageBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage.NewsImageBuilder withImageData(String imageData) {
            this.imageData = imageData;
            return this;
        }

        public at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage.NewsImageBuilder withNews(News news) {
            this.news = news;
            return this;
        }

        public at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage build() {
            at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage newsImage = new at.ac.tuwien.sepm.groupphase.backend.entity.NewsImage();
            newsImage.setId(id);
            newsImage.setImageData(imageData);
            newsImage.setNews(news);
            return newsImage;
        }
    }

}
