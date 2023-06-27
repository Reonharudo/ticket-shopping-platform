package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketPayloadDto;
import jakarta.xml.bind.ValidationException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface TicketValidatorService {

    /**
     * Retrieves the ticket payload for the given ticket.
     *
     * @param ticketDto The TicketDto object representing the ticket.
     * @return The TicketPayloadDto object representing the ticket payload.
     * @throws InvalidAlgorithmParameterException If an invalid algorithm parameter is encountered.
     * @throws NoSuchPaddingException             If no such padding is available.
     * @throws IllegalBlockSizeException          If an illegal block size is encountered.
     * @throws NoSuchAlgorithmException           If no such algorithm is available.
     * @throws BadPaddingException                If padding is invalid.
     * @throws InvalidKeyException                If an invalid key is encountered.
     * @throws ValidationException                If the payload fails validation.
     */
    TicketPayloadDto getTicketPayload(TicketDto ticketDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException,
        ValidationException;

    /**
     * Validates the ticket payload.
     *
     * @param payloadDto The TicketPayloadDto object representing the ticket payload to validate.
     * @return The validated TicketPayloadDto object.
     * @throws InvalidAlgorithmParameterException If an invalid algorithm parameter is encountered.
     * @throws NoSuchPaddingException             If no such padding is available.
     * @throws IllegalBlockSizeException          If an illegal block size is encountered.
     * @throws NoSuchAlgorithmException           If no such algorithm is available.
     * @throws BadPaddingException                If padding is invalid.
     * @throws InvalidKeyException                If an invalid key is encountered.
     * @throws ValidationException                If the payload fails validation.
     */
    TicketPayloadDto validatePayload(TicketPayloadDto payloadDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException,
        ValidationException;

    /**
     * Writes new access keys for encryption/decryption of payloads.
     */
    void writeNewAccessKeys();

}
