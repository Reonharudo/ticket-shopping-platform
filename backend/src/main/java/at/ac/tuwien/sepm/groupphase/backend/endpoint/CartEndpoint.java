package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/cart")
public class CartEndpoint {

    private static final String USER_CANT_BE_RESOLVED = "User with ROLE_USER could not be resolved";
    private static final String REQUEST_CANT_BE_RESOLVED = "Request could not be resolved!";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CartService service;
    private final PurchaseService purchaseService;
    private final CustomUserDetailService userService;

    @Autowired
    public CartEndpoint(CartService cartService, PurchaseService purchaseService, CustomUserDetailService userService) {
        this.service = cartService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a list of all CartItems", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> getCart(@RequestHeader("Authorization") String token) {
        LOGGER.info("GET /api/v1/cart");

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        return ResponseEntity.status(HttpStatus.OK).body(service.getItems(userId));
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Add a list of Seats to the cart", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> addToCart(@RequestBody List<SeatDto> seatDtoList, @RequestHeader("Authorization") String token) {
        LOGGER.info("Post /api/v1/cart");

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        try {
            service.addItemList(seatDtoList, userId);
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{id}")
    @Operation(summary = "Removes a Seat from the cart", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> removeFromCart(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        LOGGER.info("Delete /api/v1/cart/{}", id);

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        this.service.deleteItem(id, userId, true);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/purchase")
    @Operation(summary = "Purchases the tickets in the cart", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> buyCart(@RequestBody PurchaseCreationDto purchaseCreationDto, @RequestHeader("Authorization") String token) {
        LOGGER.info("Post /api/v1/cart/purchase");

        Long userId = userService.getUserIdFromToken(token);

        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        if (purchaseService.purchaseCartOfUser(userId, purchaseCreationDto)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
