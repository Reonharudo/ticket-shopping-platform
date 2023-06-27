package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;

import java.util.List;

public interface PurchaseService {

    /**
     * Fetches the Purchase with the given purchaseNr.
     * Will only return the purchase if the purchase belongs to the given user.
     *
     * @param purchaseNr the id of the purchase to fetch
     * @param userId     the id of the user that tries to fetch the purchae
     * @return the requested Purchase
     */
    PurchaseDto getPurchaseByPurchaseNr(Long purchaseNr, Long userId);

    /**
     * Deletes the Purchase with the given purchaseNr.
     * Will only delete the purchase if the purchase belongs to the given user.
     *
     * @param purchaseNr the id of the purchase to fetch
     * @param userId     the id of the user that tries to delete the purchase
     */
    void deletePurchase(Long purchaseNr, Long userId);

    /**
     * Fetches all the Purchases of a given user.
     *
     * @param userId the id of the user
     * @return a List of PurchaseDtos
     */
    List<PurchaseDto> getPurchasesOfUser(Long userId);

    /**
     * Creates a new Purchase with the items in the Cart of the User.
     * Sets the Seats from RESERVED to OCCUPIED.
     *
     * @param userId              the id of the user
     * @param purchaseCreationDto additional information of the user
     * @return returns true if the cart was purchased (at least partially)
     */
    boolean purchaseCartOfUser(Long userId, PurchaseCreationDto purchaseCreationDto);

    /**
     * Creates a new Purchase with the given items of the User.
     * Sets the Seats from RESERVED to OCCUPIED. (the rest of the Reservation is set to FREE)
     * These Items must be part of a single Reservation.
     * This Reservation must be owned by the user
     * If a part of the Reservation is given this part is set to OCCUPIED while the rest is being set to FREE.
     *
     * @param purchaseCreationDto additional information of the user
     * @param reservationNr       the id of the reservation
     * @param userId              the id of the user
     * @return returns true if the reservation was purchased (at least partially)
     */
    boolean purchaseReservationOfUser(Long purchaseCreationDto, PurchaseCreationDto reservationNr, Long userId);

}
