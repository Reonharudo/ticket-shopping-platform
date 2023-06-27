package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CartItemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Cart;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CartRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PurchaseRepository;
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
class CartEndpointTest implements TestData {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

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
        cartRepository.deleteAll();
        purchaseRepository.deleteAll();

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
    void givenNothing_whenGetCart_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(CART_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<CartItemDto> cartItemDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            CartItemDto[].class));

        assertEquals(0, cartItemDtoList.size());
    }

    @Test
    void given1CartItem_whenGetCart_then1ItemList() throws Exception {
        Cart cart = new Cart(userRepository.findUserByEmail(DEFAULT_USER).getId(), -1L);
        cartRepository.save(cart);
        MvcResult mvcResult = this.mockMvc.perform(get(CART_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer
                .getAuthToken(DEFAULT_USER, USER_ROLES))).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<CartItemDto> cartItemDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            CartItemDto[].class));

        assertEquals(1, cartItemDtoList.size());
    }

    @Test
    void tryAddNonExistingItemToCart() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDtoList.add(seatDto);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void addExistingSeatToCart() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(1, cartRepository.findByUserId(userId).size());
    }

    @Test
    void addTwoExistingSeatsToCart() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        SeatDto seatDto2 = new SeatDto();
        seatDto2.setId(-2L);
        seatDtoList.add(seatDto);
        seatDtoList.add(seatDto2);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(2, cartRepository.findByUserId(userId).size());
    }

    @Test
    void addOneExistingAndOneNoneExistingSeatToCart() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        SeatDto seatDto2 = new SeatDto();
        seatDto2.setId(1L);
        seatDtoList.add(seatDto);
        seatDtoList.add(seatDto2);

        String body = objectMapper.writeValueAsString(seatDtoList);
        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals( 0, cartRepository.findByUserId(userId).size());
    }

    @Test
    void deleteCartItem() throws Exception {
        cartRepository.save(new Cart(userId, -1L));

        MvcResult mvcResult = this.mockMvc.perform(delete(CART_BASE_URI + "/{id}", -1L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(0, cartRepository.findByUserId(userId).size());
    }

    @Test
    void addReservedSeatToCart() throws Exception {
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
        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(0, cartRepository.findByUserId(userId).size());
    }

    @Test
    void deleteCartItemOfDifferentUser() throws Exception {
        cartRepository.save(new Cart(userId + 1L, -1L));

        MvcResult mvcResult = this.mockMvc.perform(delete(CART_BASE_URI + "/{id}", -1L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(1, cartRepository.findByUserId(userId + 1L).size());
    }

    @Test
    void deleteNonExistingItem() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(CART_BASE_URI + "/{id}", -1L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void purchaseCart() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(1L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        cartRepository.save(new Cart(userId, -1L));

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI + "/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(0, cartRepository.findByUserId(userId).size());
        assertEquals(1, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

    @Test
    void purchaseCartWithNonExistingItem() throws Exception {
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI + "/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(0, cartRepository.findByUserId(userId).size());
        assertEquals(0, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

    @Test
    void purchaseCartWithNonReservedItem() throws Exception {
        Optional<HallPlanSeat> optSeat = seatRepository.getSeatById(-1L);
        HallPlanSeat seat = optSeat.get();
        seat.setReservedNr(0L);
        seat.setOrderNr(0L);
        seatRepository.save(seat);

        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setId(-1L);
        seatDtoList.add(seatDto);

        PurchaseCreationDto purchaseCreationDto = new PurchaseCreationDto(true, null, null, null, seatDtoList);

        String body = objectMapper.writeValueAsString(purchaseCreationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(CART_BASE_URI + "/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(0, cartRepository.findByUserId(userId).size());
        assertEquals(0, purchaseRepository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId).size());
    }

}
