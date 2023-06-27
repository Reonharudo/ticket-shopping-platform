package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/v1/reservation")
public class ReservationEndpoint {
    private static final String USER_CANT_BE_RESOLVED = "User with ROLE_USER could not be resolved";
    private static final String REQUEST_CANT_BE_RESOLVED = "Request could not be resolved!";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservationService service;
    private final PurchaseService purchaseService;
    private final CustomUserDetailService userService;

    @Autowired
    public ReservationEndpoint(ReservationService reservationService, PurchaseService purchaseService, CustomUserDetailService userService) {
        this.service = reservationService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Gets a list of all Reservations from that user", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> getReservations(@RequestHeader("Authorization") String token) {
        LOGGER.info("GET /api/v1/reservation");

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        return ResponseEntity.ok(service.getReservationsOfUser(userId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{reservationNr}")
    @Operation(summary = "Gets a list of all Reservations from that user", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> getReservation(@PathVariable Long reservationNr, @RequestHeader("Authorization") String token) {
        LOGGER.info("GET /api/v1/reservation/{}", reservationNr);

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }
        ReservationDto reservation;
        try {
            reservation = service.getReservationOfUser(reservationNr, userId);
        } catch (NotFoundException e) {
            LOGGER.warn("User requested non existing Reservation.");
            return ResponseEntity.noContent().build();
        }

        if (reservation == null) {
            LOGGER.error("Successfully fetched reservation is Null");
            return ResponseEntity.internalServerError().body("There was an issue retrieving your Reservation!");
        }

        return ResponseEntity.ok(reservation);
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Create a new Reservation with the given seats", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> newReservation(@RequestBody List<SeatDto> seatDtoList, @RequestHeader("Authorization") String token) {
        LOGGER.info("Post /api/v1/reservation");

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        try {
            service.addReservation(seatDtoList, userId);
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{reservationNr}")
    @Operation(summary = "Removes a Reservation by reservationnumber", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> deleteReservation(@PathVariable Long reservationNr, @RequestHeader("Authorization") String token) {
        LOGGER.info("Delete /api/v1/reservation/{}", reservationNr);

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        try {
            this.service.deleteReservation(reservationNr, userId);
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/{reservationNr}/purchase")
    @Operation(summary = "Purchases a given amount of tickets of a Reservation", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Object> buyReservation(@RequestBody PurchaseCreationDto purchaseCreationDto, @PathVariable Long reservationNr, @RequestHeader("Authorization") String token) {
        LOGGER.info("Post /api/v1/cart/purchase");

        Long userId = userService.getUserIdFromToken(token);
        if (userId == null) {
            LOGGER.error(USER_CANT_BE_RESOLVED);
            return ResponseEntity.internalServerError().body(REQUEST_CANT_BE_RESOLVED);
        }

        if (!purchaseService.purchaseReservationOfUser(reservationNr, purchaseCreationDto, userId)) {
            return ResponseEntity.badRequest().body("Unable to purchase Reservation");
        }
        return ResponseEntity.ok().build();
    }


}
