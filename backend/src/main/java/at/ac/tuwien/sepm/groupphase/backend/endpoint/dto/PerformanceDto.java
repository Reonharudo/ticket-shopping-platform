package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventDate;

import java.time.LocalTime;

public class PerformanceDto {
    private Long id;
    private String title;
    private EventDateDto performanceDate;
    private LocalTime duration;
    private String category;
    private String description;
    private String image;
    private String artist;

    public PerformanceDto() {

    }

    public PerformanceDto(Long id, String title, EventDateDto performanceDate, LocalTime duration, String category, String description, String image, String artist) {
        this.id = id;
        this.title = title;
        this.performanceDate = performanceDate;
        this.duration = duration;
        this.category = category;
        this.description = description;
        this.image = image;
        this.artist = artist;
    }

    public PerformanceDto(Event event, EventDate date) {
        EventDateDto eventDateDto = new EventDateDto();
        eventDateDto.setCity(date.getCity());
        eventDateDto.setDate(date.getDate());
        eventDateDto.setRoom(date.getRoom());
        eventDateDto.setAddress(date.getAddress());
        eventDateDto.setAreaCode(date.getAreaCode());
        eventDateDto.setStartingTime(date.getStartingTime());
        eventDateDto.setEvent(date.getEvent());

        this.id = event.getId();
        this.title = event.getTitle();
        this.performanceDate = eventDateDto;
        this.duration = event.getDuration();
        this.category = event.getCategory();
        this.description = event.getDescription();
        this.image = event.getImage();
        this.artist = event.getArtist();
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public EventDateDto getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(EventDateDto performanceDate) {
        this.performanceDate = performanceDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
