package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class NewsInquiryDto {

    private static final String BASE64PATTERN = "^data:image/(gif|png|jpeg|webp|svg\\+xml);base64,.*={0,2}$";

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 50)
    private String title;

    @NotNull(message = "Short Text must not be null")
    @NotBlank(message = "Short Text must not be blank")
    @Size(max = 100)
    private String shortText;

    @NotNull(message = "Full Text must not be null")
    @Size(max = 10000)
    private String fullText;

    @Pattern(regexp = BASE64PATTERN, message = "Cover Image is not a valid base64 picture")
    private String coverImage;

    @NotNull
    private List<@Pattern(regexp = BASE64PATTERN, message = "An additional image is not a valid base64 picture")
        String> images = new LinkedList<>();

    private Long eventId;

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
        if (!(o instanceof NewsInquiryDto that)) {
            return false;
        }
        return Objects.equals(title, that.title)
            && Objects.equals(shortText, that.shortText)
            && Objects.equals(fullText, that.fullText)
            && Objects.equals(coverImage, that.coverImage)
            && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, shortText, fullText, coverImage, eventId);
    }

    @Override
    public String toString() {
        return "NewsInquiryDto{"
            + "title='" + title + '\''
            + ", shortText='" + shortText + '\''
            + ", fullText='" + fullText + '\''
            + ", eventId='" + eventId
            + '}';
    }


    public static final class NewsInquiryDtoBuilder {
        private String title;
        private String shortText;
        private String fullText;
        private String coverImage;
        private List<String> images;
        private Long eventId;

        private NewsInquiryDtoBuilder() {
        }

        public static NewsInquiryDto.NewsInquiryDtoBuilder aNewsInquiryDto() {
            return new NewsInquiryDto.NewsInquiryDtoBuilder();
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withShortText(String shortText) {
            this.shortText = shortText;
            return this;
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withFullText(String fullText) {
            this.fullText = fullText;
            return this;
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withImages(List<String> images) {
            this.images = images;
            return this;
        }

        public NewsInquiryDto.NewsInquiryDtoBuilder withEventId(Long eventId) {
            this.eventId = eventId;
            return this;
        }

        public NewsInquiryDto build() {
            NewsInquiryDto newsInquiryDto = new NewsInquiryDto();
            newsInquiryDto.setTitle(title);
            newsInquiryDto.setShortText(shortText);
            newsInquiryDto.setFullText(fullText);
            newsInquiryDto.setCoverImage(coverImage);
            newsInquiryDto.setImages(images);
            newsInquiryDto.setEventId(eventId);
            return newsInquiryDto;
        }
    }
}
