package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AbbreviatedEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
    }

    @Test
    public void givenFiveEvents_whenFindByName_thenListWithSizeThree()
        throws Exception {
        Event event1 = new Event();
        event1.setCategory("Rock");
        event1.setArtist("Queen");
        event1.setDuration(LocalTime.now());
        event1.setTitle("Live Aid");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setCategory("Rock");
        event2.setArtist("Queen");
        event2.setDuration(LocalTime.now());
        event2.setTitle("Live Aid");
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setCategory("Rock");
        event3.setArtist("Queen");
        event3.setDuration(LocalTime.now());
        event3.setTitle("Live Aid");
        eventRepository.save(event3);

        Event event4 = new Event();
        event4.setCategory("Rock");
        event4.setArtist("Queen");
        event4.setDuration(LocalTime.now());
        event4.setTitle("Life Aid");
        eventRepository.save(event4);

        Event event5 = new Event();
        event5.setCategory("Rock");
        event5.setArtist("Queen");
        event5.setDuration(LocalTime.now());
        event5.setTitle("Love Aid");
        eventRepository.save(event5);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI + "/search")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("searchString","iv"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AbbreviatedEventDto> abbreviatedEventDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedEventDto[].class));

        assertEquals(3, abbreviatedEventDtos.size());
    }

    @Test
    public void givenSixEvents_whenFindByNameEmptyAndNameNull_thenListWithSizeFiveAndListWithSizeFive()
        throws Exception {
        Event event1 = new Event();
        event1.setCategory("Rock");
        event1.setArtist("Queen");
        event1.setDuration(LocalTime.now());
        event1.setTitle("Alpha");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setCategory("Rock");
        event2.setArtist("Queen");
        event2.setDuration(LocalTime.now());
        event2.setTitle("Bravo");
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setCategory("Rock");
        event3.setArtist("Queen");
        event3.setDuration(LocalTime.now());
        event3.setTitle("Charlie");
        eventRepository.save(event3);

        Event event4 = new Event();
        event4.setCategory("Rock");
        event4.setArtist("Queen");
        event4.setDuration(LocalTime.now());
        event4.setTitle("Delta");
        eventRepository.save(event4);

        Event event5 = new Event();
        event5.setCategory("Rock");
        event5.setArtist("Queen");
        event5.setDuration(LocalTime.now());
        event5.setTitle("Echo");
        eventRepository.save(event5);

        Event event6 = new Event();
        event6.setCategory("Rock");
        event6.setArtist("Queen");
        event6.setDuration(LocalTime.now());
        event6.setTitle("Foxtrot");
        eventRepository.save(event6);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI + "/search")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("searchString",""))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AbbreviatedEventDto> abbreviatedEventDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedEventDto[].class));

        assertEquals(5, abbreviatedEventDtos.size());

        // now searchString is null:
        MvcResult mvcResult2 = this.mockMvc.perform(get(EVENTS_BASE_URI + "/search")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();

        assertEquals(HttpStatus.OK.value(), response2.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response2.getContentType());

        List<AbbreviatedEventDto> abbreviatedEventDtos2 = Arrays.asList(objectMapper.readValue(response2.getContentAsString(),
            AbbreviatedEventDto[].class));

        assertEquals(5, abbreviatedEventDtos2.size());
    }

    @Test
    public void givenFiveEvents_whenFindByNameNullAndNumberFour_thenListWithSizeFourOrderedAlphabeticallyAscending()
        throws Exception {
        Event event1 = new Event();
        event1.setCategory("Rock");
        event1.setArtist("Queen");
        event1.setDuration(LocalTime.now());
        event1.setTitle("Alpha");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setCategory("Rock");
        event2.setArtist("Queen");
        event2.setDuration(LocalTime.now());
        event2.setTitle("Bravo");
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setCategory("Rock");
        event3.setArtist("Queen");
        event3.setDuration(LocalTime.now());
        event3.setTitle("Charlie");
        eventRepository.save(event3);

        Event event4 = new Event();
        event4.setCategory("Rock");
        event4.setArtist("Queen");
        event4.setDuration(LocalTime.now());
        event4.setTitle("Delta");
        eventRepository.save(event4);

        Event event5 = new Event();
        event5.setCategory("Rock");
        event5.setArtist("Queen");
        event5.setDuration(LocalTime.now());
        event5.setTitle("Echo");
        eventRepository.save(event5);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI + "/search")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("number","4"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AbbreviatedEventDto> abbreviatedEventDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            AbbreviatedEventDto[].class));

        assertEquals(4, abbreviatedEventDtos.size());

        assertAll(
            () -> assertEquals(abbreviatedEventDtos.get(0).getTitle(), "Alpha"),
            () -> assertEquals(abbreviatedEventDtos.get(1).getTitle(), "Bravo"),
            () -> assertEquals(abbreviatedEventDtos.get(2).getTitle(), "Charlie"),
            () -> assertEquals(abbreviatedEventDtos.get(3).getTitle(), "Delta")
        );
    }
}
