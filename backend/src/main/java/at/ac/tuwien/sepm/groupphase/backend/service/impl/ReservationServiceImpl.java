package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.ReservationSeat;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
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

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservationRepository repository;
    private final HallPlanSeatService seatService;
    private final EventService eventService;
    private final SeatRowServiceImpl rowService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository repository, HallPlanSeatService seatService, EventService eventService, SeatRowServiceImpl rowService) {
        this.repository = repository;
        this.seatService = seatService;
        this.eventService = eventService;
        this.rowService = rowService;
    }

    @Override
    public List<ReservationDto> getReservationsOfUser(Long userId) {
        LOGGER.debug("Fetch the List of reservations of user {}", userId);
        List<Reservation> reservationList = repository.findReservationsByUserIdOrderByReservationNrDesc(userId);
        List<ReservationDto> reservationDtoList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if (reservation.getReservationSeatsList().isEmpty()) {
                LOGGER.error("reservation doesnt have tickets");
                continue;
            }

            reservationDtoList.add(getReservationOfUser(reservation.getReservationNr(), userId));
        }

        return reservationDtoList;
    }

    @Override
    public ReservationDto getReservationOfUser(Long reservationNr, Long userId) throws NotFoundException {
        LOGGER.debug("Fetch reservation {} of user {}", reservationNr, userId);
        Reservation reservation = repository.findReservationByReservationNr(reservationNr);
        if (reservation == null) {
            LOGGER.warn("reservation with the given nr doesnt exist");
            throw new NotFoundException();
        }

        if (!reservation.getUserId().equals(userId)) {
            LOGGER.warn("user is not the owner of the reservation");
            throw new NotFoundException();
        }

        if (reservation.getReservationSeatsList().isEmpty()) {
            LOGGER.error("reservation doesnt have tickets");
            throw new NotFoundException();
        }

        List<SeatDto> seatDtoList = new ArrayList<>();
        List<ReservationSeat> reservationSeatList = reservation.getReservationSeatsList();
        SeatRowDto rowDto = null;
        for (ReservationSeat reservationSeat : reservationSeatList) {
            HallPlanSeatDto hallPlanSeatDto = seatService.getSeatById(reservationSeat.getSeatId());
            rowDto = rowService.getSeatRowById(hallPlanSeatDto.getSeatrowId());
            seatDtoList.add(new SeatDto(hallPlanSeatDto, rowDto));
        }

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setReservedSeats(seatDtoList);

        if (rowDto == null) {
            throw new NotFoundException();
        }

        EventDetailDto event = eventService.getEventFromHallplanId(rowDto.getHallPlanId());
        reservationDto.setEvent(event);

        reservationDto.setReservationDate(reservation.getDate());
        reservationDto.setReservationNr(reservation.getReservationNr());

        return reservationDto;
    }


    @Override
    @Transactional
    public void deleteReservation(Long reservationNr, Long userId) {
        LOGGER.debug("Delete reservation {} of user {}", reservationNr, userId);
        Reservation reservation = repository.findReservationByReservationNr(reservationNr);

        if (reservation == null) {
            throw new NotFoundException();
        }

        if (!reservation.getUserId().equals(userId)) {
            throw new NotFoundException();
        }

        for (ReservationSeat reservationSeat : reservation.getReservationSeatsList()) {
            if (!seatService.cancelReservation(reservationSeat.getSeatId())) {
                LOGGER.error("unable to free a seat that was reserved");
            }
        }
        repository.deleteReservationByReservationNr(reservationNr);
    }

    @Override
    public void addReservation(List<SeatDto> itemDtoList, Long userId) {
        LOGGER.debug("Add a new reservation with a List of Seats");

        if (!checkReservationList(itemDtoList, userId)) {
            throw new NotFoundException();
        }

        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setUserId(userId);

        List<ReservationSeat> reservationSeatList = new ArrayList<>();

        for (SeatDto item : itemDtoList) {
            if (seatService.tryReserveSeat(item.getId())) {
                reservationSeatList.add(new ReservationSeat(item.getId()));
            }
        }

        reservation.setReservationSeatsList(reservationSeatList);

        if (!reservation.getReservationSeatsList().isEmpty()) {
            repository.save(reservation);
        } else {
            LOGGER.warn("reservation has no items");
        }
    }

    @Override
    public boolean checkReservationList(List<SeatDto> itemDtoList, Long userId) {
        if (itemDtoList.isEmpty()) {
            return false;
        }

        for (SeatDto item : itemDtoList) {
            if (!seatService.doesSeatExist(item.getId())) {
                return false;
            }
        }

        HallPlanSeatDto initialHallPlanSeatDto = seatService.getSeatById(itemDtoList.get(0).getId());
        SeatRowDto initialRowDto = rowService.getSeatRowById(initialHallPlanSeatDto.getSeatrowId());
        EventDetailDto prevEventDetailDto = eventService.getEventFromHallplanId(initialRowDto.getHallPlanId());

        for (SeatDto item : itemDtoList) {
            if (seatService.doesSeatExist(item.getId())) {
                HallPlanSeatDto hallPlanSeatDto = seatService.getSeatById(item.getId());
                SeatRowDto rowDto = rowService.getSeatRowById(hallPlanSeatDto.getSeatrowId());
                EventDetailDto eventDetailDto = eventService.getEventFromHallplanId(rowDto.getHallPlanId());

                if (!eventDetailDto.getId().equals(prevEventDetailDto.getId())) {
                    LOGGER.warn("Tickets were not part of the same event");
                    return false;
                }

            } else {
                return false;
            }

        }
        return true;

    }

}
