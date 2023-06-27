package at.ac.tuwien.sepm.groupphase.backend.integrationtest.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRowRepository;
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
@ActiveProfiles({"test", "datagen"})
public class SeatRowTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SeatRowRepository seatRowRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenSeatRow_WhenPost_ThenCreateNewSeatRow() throws Exception {
        // Create a SeatRowDto for the request body
        SeatRowDto seatRowDto = new SeatRowDto();
        seatRowDto.setRowNr(18L);

        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/hallplans/-1/seatrows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatRowDto)))
            .andExpect(status().isOk());

        // Check that the seat row was created in the database
        List<SeatRow> seatRows = seatRowRepository.findAll();
        assertThat(seatRows.size()).isEqualTo(9);
        assertThat(seatRows.get(8).getRowNr()).isEqualTo(18L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenId_WhenDelete_ThenRemoveSeatRowWithIdFromSystem() throws Exception {
        // Create a SeatRow entity in the database
        SeatRow seatRow = new SeatRow();
        seatRow.setRowNr(-1L);
        seatRow.setHallPlanId(-1L);
        seatRowRepository.save(seatRow);

        // Perform DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/hallplans/-1/seatrows/-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Check that the seat row was deleted from the database
        Optional<SeatRow> seatRows = seatRowRepository.findById(-1L);
        assertThat(!seatRows.isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenOneSeatRow_WhenPut_ThenUpdateSeatRow() throws Exception {
        // Create a SeatRow entity in the database
        SeatRow seatRow = new SeatRow();
        seatRow.setRowNr(-1L);
        seatRow.setHallPlanId(-1L);
        seatRowRepository.save(seatRow);

        // Create a SeatRowDto for the request body
        SeatRowDto seatRowDto = new SeatRowDto();
        seatRowDto.setRowNr(-2L);

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/hallplans/-1/seatrows/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatRowDto)))
            .andExpect(status().isOk());

        // Check that the seat row was updated in the database
        SeatRow updatedSeatRow = seatRowRepository.getOne(-1L);
        assertThat(updatedSeatRow.getRowNr()).isEqualTo(-2L);
    }

}
