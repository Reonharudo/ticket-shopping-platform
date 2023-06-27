package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.ReservationSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReservationRepository;
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
class ReservationEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

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
    void givenNothing_whenGetReservations_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<ReservationDto> cartItemDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(0, cartItemDtoList.size());
    }

    @Test
    void getNonExistingReservation() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI + "/{id}", -1L)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void given1ReservationItem_whenGetReservations_then1ItemList() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<ReservationDto> cartItemDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, cartItemDtoList.size());
    }

    @Test
    void given1ReservationItem_whenGetReservation_then1Item() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI + "/{id}", reservationNr)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ReservationDto reservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        assertEquals(reservationNr, reservationDto.getReservationNr());
    }

    @Test
    void tryReserveNonExistingItem() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDtoList.add(seatDto);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void reserveSeat() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
    }

    @Test
    void reserveTwoSeats() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        SeatDto seatDto2 = new SeatDto();
        seatDto2.setId(-2L);
        seatDtoList.add(seatDto);
        seatDtoList.add(seatDto2);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(2, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).get(0).getReservationSeatsList().size());
    }

    @Test
    void tryReserveSeatAndNonExistingSeat() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        SeatDto seatDto2 = new SeatDto();
        seatDto2.setId(1L);
        seatDtoList.add(seatDto);
        seatDtoList.add(seatDto2);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(0, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
    }

    @Test
    void deleteReservation() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        MvcResult mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/{id}", reservationNr)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(0, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
    }

    @Test
    void tryReserveReservedSeat() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(0, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
    }

    @Test
    void deleteReservationOfDifferentUser() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId + 1);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId + 1);
        Long reservationNr = reservationList.get(0).getReservationNr();

        MvcResult mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/{id}", reservationNr)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId + 1).size());
    }


    @Test
    void deleteNonExistingReservation() throws Exception {
        Long reservationNr = 1L;

        MvcResult mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/{id}", reservationNr)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void fetchReservationOfDifferentUser() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId + 1);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId + 1);
        Long reservationNr = reservationList.get(0).getReservationNr();

        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI + "/{id}", reservationNr)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void purchaseReservation() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI + "/{reservationNr}/purchase", reservationNr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(0, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(1, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

    @Test
    void purchaseReservationPartially() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        optSeat = seatRepository.getSeatById(-2L);
        seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI + "/{reservationNr}/purchase", reservationNr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(0, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(1, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
        assertEquals(1, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).get(0).getTicketList().size());
    }

    @Test
    void emtpyPartialPurchaseReservation() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        optSeat = seatRepository.getSeatById(-2L);
        seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI + "/{reservationNr}/purchase", reservationNr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(0, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

    @Test
    void purchaseReservationPartiallyAndGiveNonExistingSeat() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI + "/{reservationNr}/purchase", reservationNr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(0, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

    @Test
    void purchaseReservationPartiallyAndGiveNonReservedSeat() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);
        List<ReservationSeat> reservationSeatList = new ArrayList<>();
        reservationSeatList.add(new ReservationSeat(-1L));
        reservation.setReservationSeatsList(reservationSeatList);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        Long reservationNr = reservationList.get(0).getReservationNr();

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-2L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI + "/{reservationNr}/purchase", reservationNr)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, reservationRepository.findReservationsByUserIdOrderByReservationNrDesc(userId).size());
        assertEquals(0, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

}
