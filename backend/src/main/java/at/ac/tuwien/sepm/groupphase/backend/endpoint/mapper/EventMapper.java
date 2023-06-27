package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AbbreviatedEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventDate;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface EventMapper {
    @Named("event")
    @Mapping(target = "id", source = "id")
    EventDetailDto eventToEventDetailDto(Event event);

    @IterableMapping(qualifiedByName = "event")
    @Mapping(target = "id", source = "id")
    List<EventDetailDto> eventToEventDetailDto(List<Event> events);

    @IterableMapping(qualifiedByName = "event")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventDatesLocation", source = "eventDatesLocation")
    Event eventDetailDtoToEvent(EventDetailDto event);

    @Named("mapEventDateDtoToEventDate")
    default List<EventDate> mapEventDates(List<EventDateDto> eventDates) {
        if (eventDates == null) {
            return Collections.emptyList();
        }
        return eventDates.stream()
            .map(this::mapEventDate)
            .collect(Collectors.toList());
    }

    default EventDate mapEventDate(EventDateDto event) {
        EventDate eventDate = new EventDate();
        eventDate.setDate(event.getDate());
        eventDate.setStartingTime(event.getStartingTime());
        eventDate.setAddress(event.getAddress());
        eventDate.setCity(event.getCity());
        eventDate.setAreaCode(event.getAreaCode());
        eventDate.setRoom(event.getRoom());
        return eventDate;
    }

    AbbreviatedEventDto eventToAbbreviatedEventDto(Event event);


}
