package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DetailedNewsDto {

    private Long id;

    private String title;

    private String fullText;

    private LocalDateTime createdAt;

    private String coverImage;

    private List<String> images = new LinkedList<>();

    private Long eventId;

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void addImage(String image) {
        this.images.add(image);
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailedNewsDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(fullText, that.fullText)
            && Objects.equals(coverImage, that.coverImage)
            && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, fullText, coverImage, eventId);
    }

    @Override
    public String toString() {
        return "DetailedNewsDto{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", fullText='" + fullText + '\''
            + ", createdAt=" + createdAt + '\''
            + ", eventId=" + eventId
            + '}';
    }


    public static final class DetailedNewsDtoBuilder {
        private Long id;
        private String title;
        private String fullText;
        private LocalDateTime createdAt;
        private String coverImage;
        private List<String> images;
        private Long eventId;

        private DetailedNewsDtoBuilder() {
        }

        public static DetailedNewsDto.DetailedNewsDtoBuilder aDetailedNewsDto() {
            return new DetailedNewsDto.DetailedNewsDtoBuilder();
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withFullText(String fullText) {
            this.fullText = fullText;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withImages(List<String> images) {
            this.images = images;
            return this;
        }

        public DetailedNewsDto.DetailedNewsDtoBuilder withEventId(Long eventId) {
            this.eventId = eventId;
            return this;
        }

        public DetailedNewsDto build() {
            DetailedNewsDto detailedNewsDto = new DetailedNewsDto();
            detailedNewsDto.setId(id);
            detailedNewsDto.setTitle(title);
            detailedNewsDto.setFullText(fullText);
            detailedNewsDto.setCreatedAt(createdAt);
            detailedNewsDto.setCoverImage(coverImage);
            detailedNewsDto.setImages(images);
            detailedNewsDto.setEventId(eventId);
            return detailedNewsDto;
        }
    }
}
