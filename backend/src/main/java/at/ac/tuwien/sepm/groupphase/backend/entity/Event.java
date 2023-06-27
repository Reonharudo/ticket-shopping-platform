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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;
import java.util.List;
import java.util.LinkedList;

@Entity
@Table(name = "event")
public class Event {
    private static final String base64Pattern = "^(data:image/(gif|png|jpeg|webp|svg\\+xml);base64,.*={0,2})?$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<EventDate> eventDatesLocation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
    private List<News> newsEntries = new LinkedList<>();

    @NotNull
    private LocalTime duration;

    @NotBlank
    private String category;

    @Column(length = 10000)
    private String description;

    @Column(columnDefinition = "Long default 0")
    private Long soldTickets;

    @NotBlank
    private String artist;

    @Lob
    @Pattern(regexp = base64Pattern)
    @Column(columnDefinition = "BLOB")
    private String image;

    public Event() {
    }

    public Event(
        Long id,
        String title,
        List<EventDate> eventDatesLocation,
        LocalTime duration,
        String category,
        String description,
        String image,
        String artist) {
        this.id = id;
        this.title = title;
        this.eventDatesLocation = eventDatesLocation;
        this.duration = duration;
        this.category = category;
        this.description = description;
        this.image = image;
        this.artist = artist;
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

    public List<EventDate> getEventDatesLocation() {
        return eventDatesLocation;
    }

    public void setEventDatesLocation(List<EventDate> eventsDatesLocation) {
        this.eventDatesLocation = eventsDatesLocation;
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

    public List<News> getNewsEntries() {
        return newsEntries;
    }

    public void setNewsEntries(List<News> newsEntries) {
        this.newsEntries = newsEntries;
    }

    public void addNewsEntry(News newsEntry) {
        this.newsEntries.add(newsEntry);
    }

    public Long getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Long soldTickets) {
        this.soldTickets = soldTickets;
    }
}
