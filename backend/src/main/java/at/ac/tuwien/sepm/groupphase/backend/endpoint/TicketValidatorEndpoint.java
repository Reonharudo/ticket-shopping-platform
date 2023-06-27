package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketPayloadDto;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.invoke.MethodHandles;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/api/v1/ticket-validator")
public class TicketValidatorEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketValidatorService ticketValidatorService;

    @Autowired
    public TicketValidatorEndpoint(TicketValidatorService ticketValidatorService) {
        this.ticketValidatorService = ticketValidatorService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Get a QR Code Payload as a string ", security = @SecurityRequirement(name = "apiKey"))
    public TicketPayloadDto getTicketPayload(@RequestBody TicketDto ticketDto)
        throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ValidationException {
        LOGGER.info("GET /api/v1/ticket-validator");
        return ticketValidatorService.getTicketPayload(ticketDto);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/validate")
    @Operation(summary = "Get a QR Code Payload as a string ", security = @SecurityRequirement(name = "apiKey"))
    public TicketPayloadDto validateTicketPayload(TicketPayloadDto payloadDto)
        throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ValidationException {
        LOGGER.info("GET /api/v1/ticket-validator/validate");
        return ticketValidatorService.validatePayload(payloadDto);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/keys")
    @Operation(summary = "Generate new access keys", security = @SecurityRequirement(name = "apiKey"))
    public void generateNewAccessKey() {
        LOGGER.info("GET /api/v1/ticket-validator");
        ticketValidatorService.writeNewAccessKeys();
    }
}
