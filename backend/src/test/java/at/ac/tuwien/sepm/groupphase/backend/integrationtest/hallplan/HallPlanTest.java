package at.ac.tuwien.sepm.groupphase.backend.integrationtest.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.DetailedHallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@RunWith(SpringRunner.class)
@SpringBootTest
@EnableWebMvc
@Transactional
@ActiveProfiles({"test", "datagen"})
public class HallPlanTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenId_WhenDelete_ThenDeleteHallplanWithId() throws Exception {
        // Create a test hall plan
        HallPlan hallPlan = new HallPlan();
        hallPlan.setName("Test Hall Plan");
        hallPlan = hallPlanRepository.save(hallPlan);

        // Send a DELETE request to the endpoint
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/hallplans/{id}", hallPlan.getId()))
            .andExpect(status().isNoContent());

        // Verify that the hall plan was deleted
        assertFalse(hallPlanRepository.findById(hallPlan.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenOneHallPlan_whenPost_thenHallPlanWithAllPropertiesSet() throws Exception {
        // Create a HallPlanDto for the request body
        HallPlanDto hallPlanDto = new HallPlanDto();
        hallPlanDto.setName("Test Hall Plan");

        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/hallplans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hallPlanDto)))
            .andExpect(status().isOk());

        // Check that the hall plan was created in the database
        List<HallPlan> hallPlans = hallPlanRepository.findAll();
        assertThat(hallPlans.size()).isEqualTo(9);
        assertThat(hallPlans.get(8).getName()).isEqualTo("Test Hall Plan");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void givenId_WhenGet_ThenReturnHallPlanById() throws Exception {
        Long validId = Long.valueOf(-1);
        // Perform GET request
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/hallplans/{id}", validId).accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<DetailedHallPlanDto> hallPlanResult = objectMapper.readerFor(DetailedHallPlanDto.class).<DetailedHallPlanDto>readValues(body).readAll();
        assertThat(hallPlanResult).isNotNull();
        assertThat(hallPlanResult).hasSize(1);
        assertThat(hallPlanResult.get(0).getId()).isEqualTo(validId);
        assertThat(hallPlanResult.get(0).getName()).isEqualTo("Hall A");
        assertThat(hallPlanResult.get(0).getDescription()).isEqualTo("Large concert hall");
        assertThat(hallPlanResult.get(0).getSeatRows()).hasSize(4);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void givenHallPlan_WhenUpdate_ThenUpdatedHallPlanFields() throws Exception {
        // Create a test hall plan
        HallPlan hallPlan = new HallPlan();
        hallPlan.setName("Test Hall Plan");
        hallPlan = hallPlanRepository.save(hallPlan);

        // Create a HallPlanDto for the request body
        HallPlanDto hallPlanDto = new HallPlanDto();
        hallPlanDto.setName("Updated Hall Plan");

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/hallplans/{id}", hallPlan.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hallPlanDto)))
            .andExpect(status().isOk());

        // Check that the hall plan was updated in the database
        Optional<HallPlan> updatedHallPlan = hallPlanRepository.findById(hallPlan.getId());
        assertTrue(updatedHallPlan.isPresent());
        assertThat(updatedHallPlan.get().getName()).isEqualTo("Updated Hall Plan");
    }


}
