package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
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
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"checkout-test-data"})
@AutoConfigureMockMvc
class PurchaseEndpointTest implements TestData {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private SeatRowRepository seatRowRepository;

    @Autowired
    private HallPlanSectionRepository sectionRepository;

    @Autowired
    private HallPlanSeatRepository seatRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        purchaseRepository.deleteAll();
        cartRepository.deleteAll();
        reservationRepository.deleteAll();

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        Optional<HallPlanSeat> optSeat2 = seatRepository.getSeatById(-2L);
        HallPlanSeat seat = optSeat.get();
        HallPlanSeat seat2 = optSeat2.get();
        seat.setReservedNr(0L);
        seat.setBoughtNr(0L);
        seat2.setReservedNr(0L);
        seat2.setBoughtNr(0L);
        seatRepository.save(seat);
        seatRepository.save(seat2);

        ApplicationUser user = new ApplicationUser();
        user.setEmail(DEFAULT_USER);
        user.setAdmin(false);
        userRepository.save(user);
        userId = userRepository.findUserByEmail(DEFAULT_USER).getId();
    }

    @Test
    void givenNothing_whenGetPurchases_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(PURCHASE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<PurchaseDto> cartItemDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            PurchaseDto[].class));

        assertEquals(0, cartItemDtoList.size());
    }

    @Test
    void getNonExistingPurchase() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(PURCHASE_BASE_URI + "/{id}", -1L)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void given1PurchaseItem_whenGetPurchases_then1ItemList() throws Exception {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(new Ticket(-1L));

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(1L);
        seatRepository.save(seat);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId);
        purchase.setBillAddress(PURCHASE_ADDR);
        purchase.setBillAreaCode(PURCHASE_AREA_CODE);
        purchase.setBillCityName(PURCHASE_CITY);
        purchase.setTicketList(ticketList);
        purchaseRepository.save(purchase);

        MvcResult mvcResult = this.mockMvc.perform(get(PURCHASE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<PurchaseDto> purchaseDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            PurchaseDto[].class));

        assertEquals(1, purchaseDtoList.size());
    }

    @Test
    void given1PurchaseItem_whenGetPurchase_then1Item() throws Exception {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(new Ticket(-1L));

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(1L);
        seatRepository.save(seat);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId);
        purchase.setBillAddress(PURCHASE_ADDR);
        purchase.setBillAreaCode(PURCHASE_AREA_CODE);
        purchase.setBillCityName(PURCHASE_CITY);
        purchase.setTicketList(ticketList);
        purchaseRepository.save(purchase);

        List<Purchase> purchaseList = purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId);
        Long purchaseNr = purchaseList.get(0).getPurchaseNr();

        MvcResult mvcResult = this.mockMvc.perform(get(PURCHASE_BASE_URI + "/{id}", purchaseNr)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PurchaseDto purchaseDto = objectMapper.readValue(response.getContentAsString(),
            PurchaseDto.class);

        assertEquals(purchaseNr, purchaseDto.getPurchaseNr());
    }

    @Test
    void cancelPurchase() throws Exception {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(new Ticket(-1L));

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(1L);
        seatRepository.save(seat);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId);
        purchase.setBillAddress(PURCHASE_ADDR);
        purchase.setBillAreaCode(PURCHASE_AREA_CODE);
        purchase.setBillCityName(PURCHASE_CITY);
        purchase.setTicketList(ticketList);
        purchaseRepository.save(purchase);

        List<Purchase> purchaseList = purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId);
        Long purchaseNr = purchaseList.get(0).getPurchaseNr();

        MvcResult mvcResult = this.mockMvc.perform(delete(PURCHASE_BASE_URI + "/{id}", purchaseNr)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(true, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).get(0).isCanceled());
    }

    @Test
    void cancelNonExistingPurchase() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(PURCHASE_BASE_URI + "/{id}", 1L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void fetchPurchaseFromDifferentUser() throws Exception {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(new Ticket(-1L));

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(1L);
        seatRepository.save(seat);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId + 1L);
        purchase.setBillAddress(PURCHASE_ADDR);
        purchase.setBillAreaCode(PURCHASE_AREA_CODE);
        purchase.setBillCityName(PURCHASE_CITY);
        purchase.setTicketList(ticketList);
        purchaseRepository.save(purchase);

        List<Purchase> purchaseList = purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId + 1);
        Long purchaseNr = purchaseList.get(0).getPurchaseNr();

        MvcResult mvcResult = this.mockMvc.perform(get(PURCHASE_BASE_URI + "/{id}", purchaseNr)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void cancelPurchaseFromDifferentUser() throws Exception {
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(new Ticket(-1L));

        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(1L);
        seatRepository.save(seat);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId + 1L);
        purchase.setBillAddress(PURCHASE_ADDR);
        purchase.setBillAreaCode(PURCHASE_AREA_CODE);
        purchase.setBillCityName(PURCHASE_CITY);
        purchase.setTicketList(ticketList);
        purchaseRepository.save(purchase);

        List<Purchase> purchaseList = purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId + 1L);
        Long purchaseNr = purchaseList.get(0).getPurchaseNr();

        MvcResult mvcResult = this.mockMvc.perform(delete(PURCHASE_BASE_URI + "/{id}", purchaseNr)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(false, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId + 1L).get(0).isCanceled());
    }
}