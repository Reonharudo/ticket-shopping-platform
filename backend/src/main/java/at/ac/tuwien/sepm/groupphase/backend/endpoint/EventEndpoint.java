package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AbbreviatedEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.xml.bind.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/events")
public class EventEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Autowired
    public EventEndpoint(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create a new event entry in the system", security = @SecurityRequirement(name = "apiKey"))
    public EventDetailDto createEvent(@RequestBody EventDetailDto event) {
        LOG.info("POST /api/v1/events");
        try {
            return eventMapper.eventToEventDetailDto(eventService.create(event));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/performance/{id}")
    @Operation(summary = "Get Information of Performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto getPerformance(@PathVariable Long id) {
        LOG.info("GET /api/v1/events/performance/{}", id);
        return eventService.getPerformanceFromHallplanId(id);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of events without details", security = @SecurityRequirement(name = "apiKey"))
    public List<AbbreviatedEventDto> findAllDefault(
        @RequestParam(defaultValue = "0") int pageIndex,
        @RequestParam(required = false) String artist,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate untilDate,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String titleCategory,
        @RequestParam(required = false) LocalTime startTime,
        @RequestParam(required = false) LocalTime duration
    ) {
        LOG.info("GET {}/events", "/api/v1/events");

        if (artist != null || fromDate != null || untilDate != null || location != null || titleCategory != null
            || startTime != null || duration != null) {
            LOG.info("called with param");
            return eventService.findAllPagesByDateAndAuthorAndLocation(pageIndex, fromDate, untilDate, artist, location,
                    titleCategory, startTime, duration)
                .map(eventMapper::eventToAbbreviatedEventDto)
                .toList();
        } else {
            LOG.info("called without param");
            return eventService.findAllPagesByDate(pageIndex)
                .map(eventMapper::eventToAbbreviatedEventDto)
                .toList();
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/byId")
    @Operation(summary = "Get an event by id", security = @SecurityRequirement(name = "apiKey"))
    public EventDetailDto findById(
        @RequestParam(required = true) Long id
    ) {
        try {
            return eventService.getEventById(id);
        } catch (NotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/topEvents")
    @Operation(summary = "Get the 10 most sold out", security = @SecurityRequirement(name = "apiKey"))
    public List<EventDetailDto> findTopEvents() {
        try {
            return eventService.getTopEvent()
                .map(eventMapper::eventToEventDetailDto)
                .toList();
        } catch (NotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/search")
    @Operation(summary = "Get list of events without details by event title", security = @SecurityRequirement(name = "apiKey"))
    public List<AbbreviatedEventDto> findByName(
        @RequestParam(required = false) String searchString,
        @RequestParam(defaultValue = "5") int number
    ) {
        LOG.info("GET /api/v1/events/search");

        return eventService.findPageByTitleSubstring(searchString, number)
            .map(eventMapper::eventToAbbreviatedEventDto)
            .toList();

    }
}
