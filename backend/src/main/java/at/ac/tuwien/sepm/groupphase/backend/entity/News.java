package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "news")
public class News {

    private static final String BASE64PATTERN = "^data:image/(gif|png|jpeg|webp|svg\\+xml);base64,.*={0,2}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String title;

    @NotBlank
    @Column(nullable = false, name = "abbreviated_text", length = 100)
    private String shortText;

    @Column(nullable = false, name = "full_text", length = 10000)
    private String fullText;

    @Column(nullable = false, name = "created_on_timestamp")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Lob
    @Pattern(regexp = BASE64PATTERN)
    @Column(name = "cover_image", columnDefinition = "BLOB")
    private String coverImage;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "news")
    private List<NewsImage> images;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "readNews")
    Set<ApplicationUser> readByUsers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<NewsImage> getImages() {
        return images;
    }

    public void setImages(List<NewsImage> images) {
        this.images = images;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void addUser(ApplicationUser user) {
        readByUsers.add(user);
    }

    public void remove(ApplicationUser user) {
        readByUsers.remove(user);
    }

    public Set<ApplicationUser> getUsers() {
        return readByUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News news)) {
            return false;
        }
        return Objects.equals(id, news.id)
            && Objects.equals(title, news.title)
            && Objects.equals(shortText, news.shortText)
            && Objects.equals(fullText, news.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, shortText, fullText);
    }

    @Override
    public String toString() {
        return "News{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", shortText='" + shortText + '\''
            + ", fullText='" + fullText + '\''
            + ", createdAt=" + createdAt + '\''
            + ", event=" + event
            + '}';
    }


    public static final class NewsBuilder {
        private Long id;
        private String title;
        private String shortText;
        private String fullText;
        private LocalDateTime createdAt;
        private String coverImage;
        private List<NewsImage> images;
        private Event event;

        private NewsBuilder() {
        }

        public static NewsBuilder aNews() {
            return new NewsBuilder();
        }

        public NewsBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NewsBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsBuilder withShortText(String shortText) {
            this.shortText = shortText;
            return this;
        }

        public NewsBuilder withFullText(String fullText) {
            this.fullText = fullText;
            return this;
        }

        public NewsBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NewsBuilder withCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public NewsBuilder withImages(List<NewsImage> images) {
            this.images = images;
            return this;
        }

        public NewsBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public News build() {
            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setFullText(fullText);
            news.setShortText(shortText);
            news.setCreatedAt(createdAt);
            news.setCoverImage(coverImage);
            news.setImages(images);
            news.setEvent(event);
            return news;
        }
    }
}
