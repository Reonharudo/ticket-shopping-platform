package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalTime;
import java.util.List;

public class EventDetailDto {
    private Long id;
    private String title;
    private List<EventDateDto> eventDatesLocation;
    private LocalTime duration;
    private String category;
    private String description;
    private String image;
    private String artist;
    private Long soldTickets;

    public List<EventDateDto> getEventDatesLocation() {
        return eventDatesLocation;
    }

    public void setEventDatesLocation(List<EventDateDto> eventDatesLocation) {
        this.eventDatesLocation = eventDatesLocation;
    }

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

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Long soldTickets) {
        this.soldTickets = soldTickets;
    }
}

