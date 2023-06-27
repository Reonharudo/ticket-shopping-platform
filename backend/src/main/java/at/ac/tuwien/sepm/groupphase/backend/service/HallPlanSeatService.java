package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import jakarta.transaction.Transactional;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatBulkDto;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public interface HallPlanSeatService {

    /**
     * Adds a seat to a seat row.
     *
     * @param seatDto specifies the hallPlan and SeatRow to where the seat should be inserted
     * @return the inserted seatDto
     */
    HallPlanSeatDto addSeat(HallPlanSeatDto seatDto);

    /**
     * Retrieves a seat by its ID.
     *
     * @param seatId the ID of the seat to retrieve
     * @return the seatDto associated with the given ID
     */
    HallPlanSeatDto getSeatById(Long seatId);

    /**
     * Updates a seat with new information.
     *
     * @param seatDto the seatDto containing the updated information
     * @return the updated seatDto
     */
    HallPlanSeatDto updateSeat(HallPlanSeatDto seatDto) throws ValidationException;

    /**
     * Deletes a seat by its ID.
     *
     * @param seatId the ID of the seat to delete
     */
    void deleteSeat(Long seatId);

    /**
     * Checks if a seat exists and is not of the type VACANT_SEAT.
     *
     * @param seatId the id of the seat to check
     * @return true if the seat exists and isn't of type VACANT_SEAT
     */
    boolean doesSeatExist(Long seatId);

    /**
     * Tries to purchase a seat that has been reserved before.
     *
     * @param seatId the seat to purchase
     * @return true if the seat was purchased
     */
    boolean purchaseReservedSeat(Long seatId);

    /**
     * Tries to reserve a seat that has been reserved before.
     *
     * @param seatId the seat to reserve
     * @return true if the seat was reserved
     */
    boolean tryReserveSeat(Long seatId);

    /**
     * Cancels the reservation of a seat that was reserved before.
     *
     * @param seatId the seat to free
     * @return true if successful
     */
    @Transactional
    boolean cancelReservation(Long seatId);

    /**
     * Cancels the purchase of a seat that was reserved before.
     *
     * @param seatId the seat to free
     * @return true if successful
     */
    @Transactional
    boolean freePurchasedSeat(Long seatId);

    /**
     * Adds multiple seats to the hall plan.
     *
     * @param bulkDto the bulkDto containing the seats to be added
     * @return a list of inserted seatDtos
     */
    List<HallPlanSeatDto> bulkAddSeats(HallPlanSeatBulkDto bulkDto);

    /**
     * Updates multiple seats with new information.
     *
     * @param bulkDto the bulkDto containing the seats to be updated
     * @return a list of updated seatDtos
     */
    List<HallPlanSeatDto> bulkUpdateSeats(HallPlanSeatBulkDto bulkDto);

}
