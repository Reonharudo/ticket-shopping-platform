package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private PurchaseRepository repository;
    private HallPlanSeatService seatService;
    private TicketService ticketService;
    private CartService cartService;
    private ReservationService reservationService;
    private CustomUserDetailService customUserDetailService;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository repository, HallPlanSeatService seatService, TicketService ticketService, CartService cartService, ReservationService reservationService, CustomUserDetailService customerUserDetailService) {
        this.repository = repository;
        this.seatService = seatService;
        this.cartService = cartService;
        this.ticketService = ticketService;
        this.reservationService = reservationService;
        this.customUserDetailService = customerUserDetailService;
    }

    @Override
    public PurchaseDto getPurchaseByPurchaseNr(Long purchaseNr, Long userId) {
        LOGGER.debug("Fetch Purchase with nr {} of user {}", purchaseNr, userId);
        Purchase purchase = repository.findPurchasesByPurchaseNr(purchaseNr);

        if (purchase == null) {
            throw new NotFoundException();
        }

        if (!Objects.equals(purchase.getUserId(), userId)) {
            throw new NotFoundException();
        }

        List<Ticket> ticketList = purchase.getTicketList();
        List<TicketDto> ticketDtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            ticketDtoList.add(ticketService.ticketDtoFromTicket(ticket));
        }
        return new PurchaseDto(ticketDtoList, purchase);
    }

    @Override
    @Transactional
    public void deletePurchase(Long purchaseNr, Long userId) {
        LOGGER.debug("Delete cart of user {}", userId);
        Purchase purchase = repository.findPurchasesByPurchaseNr(purchaseNr);

        if (purchase == null) {
            return;
        }

        if (!purchase.getUserId().equals(userId)) {
            LOGGER.warn("user {} tries to remove Purchase that doesnt belong to him", userId);
            return;
        }

        for (Ticket ticket : purchase.getTicketList()) {
            if (!seatService.freePurchasedSeat(ticket.getSeatId())) {
                LOGGER.error("unable to free a seat that was bought");
            }
        }
        purchase.setCanceled(true);
        repository.save(purchase);
    }


    @Override
    public List<PurchaseDto> getPurchasesOfUser(Long userId) {
        LOGGER.debug("Fetch all purchases of user {}", userId);
        List<Purchase> purchaseList = repository.findPurchasesByUserIdOrderByPurchaseNrDesc(userId);
        List<PurchaseDto> purchaseDtoList = new ArrayList<>();

        for (Purchase purchase : purchaseList) {
            if (purchase.getTicketList().isEmpty()) {
                LOGGER.error("purchase doesnt have tickets");
                continue;
            }

            List<Ticket> ticketList = purchase.getTicketList();
            List<TicketDto> ticketDtoList = new ArrayList<>();
            for (Ticket ticket : ticketList) {
                ticketDtoList.add(ticketService.ticketDtoFromTicket(ticket));
            }

            purchaseDtoList.add(new PurchaseDto(ticketDtoList, purchase));
        }
        return purchaseDtoList;
    }

    @Override
    public boolean purchaseCartOfUser(Long userId, PurchaseCreationDto purchaseCreationDto) {
        LOGGER.debug("Purchase cart of user {}", userId);
        List<Ticket> ticketList = new ArrayList<>();

        if (purchaseCreationDto.getSeats() == null) {
            return false;
        }

        if (purchaseCreationDto.getSeats().isEmpty()) {
            return false;
        }

        for (SeatDto seatDto : purchaseCreationDto.getSeats()) {
            if (!cartService.itemBelongsToUserCart(seatDto.getId(), userId)) {
                continue;
            }

            if (seatService.purchaseReservedSeat(seatDto.getId())) {
                ticketList.add(new Ticket(seatDto.getId()));
                cartService.deleteItem(seatDto.getId(), userId, false);
            }
        }

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId);

        if (!purchaseCreationDto.getUseUserAddress()) {
            ApplicationUser user = customUserDetailService.getUserById(userId);
            purchase.setBillAddress(user.getAddress());
            purchase.setBillAreaCode(user.getAreaCode());
            purchase.setBillCityName(user.getCityName());
        } else {
            purchase.setBillAddress(purchaseCreationDto.getAddress());
            purchase.setBillAreaCode(purchaseCreationDto.getAreaCode());
            purchase.setBillCityName(purchaseCreationDto.getCity());
        }

        purchase.setExpiration(purchaseCreationDto.getExpiration());
        purchase.setSecurityCode(purchaseCreationDto.getSecurityCode());
        purchase.setCreditCardNr(purchaseCreationDto.getCreditCardNr());

        purchase.setTicketList(ticketList);
        if (!purchase.getTicketList().isEmpty()) {
            repository.save(purchase);
            return true;
        }
        return false;
    }

    @Override
    public boolean purchaseReservationOfUser(Long reservationNr, PurchaseCreationDto purchaseCreationDto, Long userId) {
        LOGGER.debug("Purchase reservation of user {}", userId);

        if (purchaseCreationDto.getSeats() == null || purchaseCreationDto.getSeats().isEmpty()) {
            return false;
        }

        //fetch reservation and check if the reservation belongs to user and exists
        ReservationDto reservationDto;
        try {
            reservationDto = reservationService.getReservationOfUser(reservationNr, userId);
        } catch (NotFoundException e) {
            return false;
        }

        //check if the items belong to the reservation

        List<SeatDto> reservedSeats = reservationDto.getReservedSeats();

        for (SeatDto seatDto : purchaseCreationDto.getSeats()) {
            if (!reservedSeats.remove(seatDto)) {
                LOGGER.warn("item doesnt belong to reservation");
                return false;
            }
        }

        //purchase tickets
        List<Ticket> purchasedTicketList = new ArrayList<>();
        for (SeatDto seatDto : purchaseCreationDto.getSeats()) {
            if (seatService.purchaseReservedSeat(seatDto.getId())) {
                purchasedTicketList.add(new Ticket(seatDto.getId()));
            }
        }

        reservationService.deleteReservation(reservationNr, userId);

        Purchase purchase = new Purchase();
        purchase.setDate(LocalDate.now());
        purchase.setUserId(userId);

        if (!purchaseCreationDto.getUseUserAddress()) {
            ApplicationUser user = customUserDetailService.getUserById(userId);
            purchase.setBillAddress(user.getAddress());
            purchase.setBillAreaCode(user.getAreaCode());
            purchase.setBillCityName(user.getCityName());
        } else {
            purchase.setBillAddress(purchaseCreationDto.getAddress());
            purchase.setBillAreaCode(purchaseCreationDto.getAreaCode());
            purchase.setBillCityName(purchaseCreationDto.getCity());
        }

        purchase.setExpiration(purchaseCreationDto.getExpiration());
        purchase.setSecurityCode(purchaseCreationDto.getSecurityCode());
        purchase.setCreditCardNr(purchaseCreationDto.getCreditCardNr());

        purchase.setTicketList(purchasedTicketList);
        repository.save(purchase);
        return true;
    }

}
