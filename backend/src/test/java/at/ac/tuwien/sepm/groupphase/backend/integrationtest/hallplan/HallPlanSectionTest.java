package at.ac.tuwien.sepm.groupphase.backend.integrationtest.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanSectionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSectionRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@EnableWebMvc
@Transactional
@ActiveProfiles({"test", "datagen"})
public class HallPlanSectionTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private HallPlanSectionRepository hallPlanSectionRepository;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private HallPlanSectionMapper hallPlanSectionMapper;
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    private static final String BASE_URI = "/api/v1/hallplans/";
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenNothing_whenDelete_thenRemoveSectorFromSystem() throws Exception {
        // Create a test hall plan
        HallPlanSection section = new HallPlanSection();
        section.setName("Test Hall Plan");
        section.setColor("Red");
        section.setPrice(100D);
        section.setHallPlanId(-1L);
        HallPlanSection sectionEntity = hallPlanSectionRepository.save(section);

        // Send a DELETE request to the endpoint
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/hallplans/sections/" + sectionEntity.getId()))
            .andExpect(status().isOk());

        // Verify that the hall plan was deleted
        assertFalse(hallPlanSectionRepository.findById(sectionEntity.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenId_WhenGet_thenReturnSectionById() throws Exception {
        Optional<HallPlanSection> section = hallPlanSectionRepository.findById(-1L);
        String body = "";

        // Send a GET request to the endpoint sections with id
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "sections/" + -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert Return type status and format
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        HallPlanSectionDto sectionDto = objectMapper.readValue(response.getContentAsString(), HallPlanSectionDto.class);

        assertNotNull(sectionDto);
        assertNotNull(section.get());

        // Verify that the hall plan sector was correctly received
        boolean areFieldsEqual = compareObjectsByFields(section.get(),sectionDto, "name", "color", "price","hallPlanId");
        assertTrue(areFieldsEqual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenNothing_WhenGet_thenReturnAllSections() throws Exception {
        List<HallPlanSection> section = hallPlanSectionRepository.findAll();
        String body = "";

        // Send a GET request to the endpoint which retrieves all Sections in the system
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert Return type status and format
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<HallPlanSectionDto> sectionDto = objectMapper.readValue(
            response.getContentAsString(),
            new TypeReference<List<HallPlanSectionDto>>() {
            }
        );

        assertNotNull(sectionDto);
        assertNotNull(section);

        // Verify that the hall plan sections were correctly received
        assertEquals(section.size(), sectionDto.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenOneSection_WhenPost_thenCreateNewSection() throws Exception {
        HallPlanSectionDto insertDto = new HallPlanSectionDto();
        insertDto.setHallPlanId(-1L);
        insertDto.setColor("red");
        insertDto.setName("VIP Plus");
        insertDto.setPrice(100D);
        String body = objectMapper.writeValueAsString(insertDto);

        // Send a GET request to the endpoint sections with id
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI + -1 + "/" + "sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert Return type status and format
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        HallPlanSectionDto sectionDto = objectMapper.readValue(response.getContentAsString(), HallPlanSectionDto.class);

        assertNotNull(sectionDto);

        // Verify that the hall plan sector was correctly received
        boolean areFieldsEqual = compareObjectsByFields(insertDto,sectionDto, "name", "color", "price","hallPlanId");
        assertTrue(areFieldsEqual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenOneSection_WhenPut_thenUpdateSection() throws Exception {
        HallPlanSectionDto updateDto = new HallPlanSectionDto();
        updateDto.setHallPlanId(-1L);
        updateDto.setColor("red");
        updateDto.setName("VIP Plus");
        updateDto.setPrice(100D);
        String body = objectMapper.writeValueAsString(updateDto);

        // Send a GET request to the endpoint sections with id
        MvcResult mvcResult = this.mockMvc.perform(put(BASE_URI + -1 + "/" + "sections" + "/" + -1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //Assert Return type status and format
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        HallPlanSectionDto sectionDto = objectMapper.readValue(response.getContentAsString(), HallPlanSectionDto.class);

        assertNotNull(sectionDto);

        // Verify that the hall plan sector was correctly received
        boolean areFieldsEqual = compareObjectsByFields(updateDto,sectionDto, "name", "color", "price","hallPlanId");
        assertTrue(areFieldsEqual);
    }

    public static boolean compareObjectsByFields(Object obj1, Object obj2, String... fields) throws NoSuchFieldException, IllegalAccessException {
        EqualsBuilder equalsBuilder = new EqualsBuilder();

        for (String field : fields) {
            Field field1 = obj1.getClass().getDeclaredField(field);
            field1.setAccessible(true);
            Field field2 = obj2.getClass().getDeclaredField(field);
            field2.setAccessible(true);

            Object value1 = field1.get(obj1);
            Object value2 = field2.get(obj2);

            equalsBuilder.append(value1, value2);
        }
        return equalsBuilder.isEquals();
    }


}
