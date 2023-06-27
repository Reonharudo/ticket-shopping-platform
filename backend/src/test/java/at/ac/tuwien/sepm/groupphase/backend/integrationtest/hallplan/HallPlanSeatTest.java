package at.ac.tuwien.sepm.groupphase.backend.integrationtest.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanSectionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSectionRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatStatus;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@EnableWebMvc
@Transactional
@ActiveProfiles({"test", "hallplan"})
public class HallPlanSeatTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HallPlanSeatRepository hallPlanSeatRepository;

    @Autowired
    private HallPlanSectionRepository hallPlanSectionRepository;

    @Autowired
    private HallPlanSectionMapper hallPlanSectionMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    private static final String BASE_URL = "/api/v1/hallplans/";
    private static final long HALL_PLAN_ID = -1L;
    private static final long SEAT_ROW_ID = -1L;
    private static final long SEAT_ID = -1L;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenOneSeat_WhenPost_ThenCreateSeat() throws Exception {
        // Create a HallPlanSeatDto for the request body
        HallPlanSeatDto seatDto = new HallPlanSeatDto();
        seatDto.setId(SEAT_ID);
        seatDto.setSeatrowId(SEAT_ROW_ID);
        seatDto.setOrderNr(7L);
        seatDto.setSeatNr(7L);
        seatDto.setCapacity(1L);
        seatDto.setOrderNr(1L);
        seatDto.setBoughtNr(0L);
        seatDto.setReservedNr(0L);

        Optional<HallPlanSection> section = hallPlanSectionRepository.findById(-1L);
        if (section.isPresent()) {
            HallPlanSectionDto sectionDto = hallPlanSectionMapper.toDto(section.get());
            seatDto.setSection(sectionDto);
        }
        seatDto.setSeatrowId(SEAT_ROW_ID);
        seatDto.setStatus(HallPlanSeatStatus.FREE);
        seatDto.setType(HallPlanSeatType.SEAT);

        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + HALL_PLAN_ID + "/seatrows/" + SEAT_ROW_ID + "/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatDto)))
            .andExpect(status().isCreated());

        // Check that seat was created in data base
        List<HallPlanSeat> seats = hallPlanSeatRepository.findAll();
        assertThat(seats.size()).isEqualTo(9);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenNothing_WhenDelete_ThenDeleteSeatFromSystem() throws Exception {
        // Perform DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + HALL_PLAN_ID + "/seatrows/" + SEAT_ROW_ID + "/seats/" + SEAT_ID))
            .andExpect(status().isNoContent());

        // Verify that hallPlanSeatService.deleteSeat() was called with the correct parameter
        List<HallPlanSeat> seats = hallPlanSeatRepository.findAll();
        assertThat(seats.size()).isEqualTo(7);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void givenOneSeat_WhenPut_ThenUpdateSeatInSystem() throws Exception {

        // Create a HallPlanSeatDto for the request body
        HallPlanSeatDto seatDto = new HallPlanSeatDto();
        seatDto.setOrderNr(7L);
        seatDto.setSeatNr(7L);
        seatDto.setCapacity(1L);
        seatDto.setSection(null);
        seatDto.setSeatrowId(-1L);
        seatDto.setStatus(HallPlanSeatStatus.FREE);
        seatDto.setType(HallPlanSeatType.SEAT);
        seatDto.setBoughtNr(0L);
        seatDto.setReservedNr(0L);

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + HALL_PLAN_ID + "/seatrows/" + SEAT_ROW_ID + "/seats/" + SEAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatDto)))
            .andExpect(status().isOk());

        // Check that the seat was updated in the database
        Optional<HallPlanSeat> updatedSeat = hallPlanSeatRepository.findById(SEAT_ID);
        assertThat(updatedSeat.get().getSeatNr()).isEqualTo(7L);
        assertThat(updatedSeat.get().getSeatrowId()).isEqualTo(-1L);
        assertThat(updatedSeat.get().getSection()).isEqualTo(null);
        assertThat(updatedSeat.get().getSeatrowId()).isEqualTo(-1L);
        assertThat(updatedSeat.get().getStatus()).isEqualTo(HallPlanSeatStatus.FREE);
        assertThat(updatedSeat.get().getType()).isEqualTo(HallPlanSeatType.SEAT);
    }


}
