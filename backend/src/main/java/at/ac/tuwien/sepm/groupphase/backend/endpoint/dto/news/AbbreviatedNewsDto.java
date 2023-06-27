package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import java.time.LocalDateTime;
import java.util.Objects;

public class AbbreviatedNewsDto {

    private Long id;

    private String title;

    private String shortText;

    private LocalDateTime createdAt;

    private String coverImage;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbbreviatedNewsDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(shortText, that.shortText)
            && Objects.equals(coverImage, that.coverImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, shortText, coverImage);
    }

    @Override
    public String toString() {
        return "AbbreviatedNewsDto{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", shortText='" + shortText + '\''
            + ", createdAt=" + createdAt
            + '}';
    }


    public static final class AbbreviatedNewsDtoBuilder {
        private Long id;
        private String title;
        private String shortText;
        private LocalDateTime createdAt;
        private String coverImage;

        private AbbreviatedNewsDtoBuilder() {
        }

        public static AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder anAbbreviatedNewsDto() {
            return new AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder();
        }

        public AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder withShortText(String shortText) {
            this.shortText = shortText;
            return this;
        }

        public AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AbbreviatedNewsDto.AbbreviatedNewsDtoBuilder withCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public AbbreviatedNewsDto build() {
            AbbreviatedNewsDto abbreviatedNewsDto = new AbbreviatedNewsDto();
            abbreviatedNewsDto.setId(id);
            abbreviatedNewsDto.setTitle(title);
            abbreviatedNewsDto.setShortText(shortText);
            abbreviatedNewsDto.setCreatedAt(createdAt);
            abbreviatedNewsDto.setCoverImage(coverImage);
            return abbreviatedNewsDto;
        }
    }
}
