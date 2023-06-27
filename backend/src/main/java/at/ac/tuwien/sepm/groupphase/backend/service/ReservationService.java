package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ReservationService {

    /**
     * Fetches all the Reservations of a given user.
     *
     * @param userId the id of the user
     * @return a List of ReservationDtos
     */
    List<ReservationDto> getReservationsOfUser(Long userId);

    /**
     * Fetches the Reservation with the given reservationNr.
     * Will only return the reservation if the reservation belongs to the given user.
     *
     * @param reservationNr the id of the reservation to fetch
     * @param userId        the id of the user that tries to fetch the reservation
     * @return the requested Reservation
     */
    ReservationDto getReservationOfUser(Long reservationNr, Long userId);

    /**
     * Deletes the Reservation with the given reservationNr.
     * Will only delete the reservation if the reservation belongs to the given user.
     *
     * @param reservationNr the id of the reservation to fetch
     * @param userId        the id of the user that tries to fetch the reservation
     */
    @Transactional
    void deleteReservation(Long reservationNr, Long userId);

    /**
     * Reserves the given items. (sets the status of the seats to RESERVED)
     * Items will only get added if they are FREE.
     *
     * @param itemDtoList the list of items to be added
     * @param userId      the id of the user tries to delete the reservation
     */
    void addReservation(List<SeatDto> itemDtoList, Long userId);

    boolean checkReservationList(List<SeatDto> itemDtoList, Long userId);
}
