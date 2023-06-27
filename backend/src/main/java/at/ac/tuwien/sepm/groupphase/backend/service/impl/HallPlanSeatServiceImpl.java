package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatBulkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanSeatMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class HallPlanSeatServiceImpl implements HallPlanSeatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallPlanSeatRepository seatRepository;
    private final HallPlanSeatMapper seatMapper;
    private final SeatRowRepository seatRowRepository;
    private final EventService eventService;

    @Autowired
    public HallPlanSeatServiceImpl(HallPlanSeatRepository seatRepository, HallPlanSeatMapper seatMapper, SeatRowRepository seatRowRepository, EventService eventService) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
        this.seatRowRepository = seatRowRepository;
        this.eventService = eventService;
    }

    @Override
    public HallPlanSeatDto addSeat(HallPlanSeatDto seatDto) {
        LOGGER.debug("Add new seat");
        Optional<SeatRow> seatRow = seatRowRepository.findById(seatDto.getSeatrowId());
        if (seatRow.isEmpty()) {
            throw new EntityNotFoundException("Seat row not found with id: " + seatDto.getSeatrowId());
        }
        if (seatDto.getId() != null) {
            seatDto.setId(null);
        }
        HallPlanSeat seat = seatMapper.toEntity(seatDto);
        seat = seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    @Override
    public HallPlanSeatDto getSeatById(Long seatId) {
        LOGGER.debug("Get seat by ID");
        Optional<HallPlanSeat> seat = seatRepository.getSeatById(seatId);
        return seat.map(seatMapper::toDto).orElse(null);
    }


    @Override
    public HallPlanSeatDto updateSeat(HallPlanSeatDto seatDto) throws ValidationException {
        LOGGER.debug("Update seat by ID");
        HallPlanSeat seat = seatMapper.toEntity(seatDto);

        List<HallPlanSeat> existingSeat = seatRepository.findAllBySeatRowIdAndSeatNr(seatDto.getSeatrowId(), seatDto.getSeatNr());

        if (!existingSeat.isEmpty() && existingSeat.get(0) != null
            && (existingSeat.get(0).getId().longValue() != seatDto.getId().longValue())
            && !Objects.equals(seatDto.getSeatNr(), existingSeat.get(0).getSeatNr())) {
            throw new ValidationException("SeatRow with seatrowId " + seatDto.getSeatrowId() + " and seatNr " + seatDto.getSeatNr() + " already exists");
        }

        seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    @Override
    public void deleteSeat(Long seatId) {
        LOGGER.debug("Delete seat by ID");
        seatRepository.deleteById(seatId);
    }

    @Override
    public List<HallPlanSeatDto> bulkAddSeats(HallPlanSeatBulkDto bulkDto) {
        LOGGER.debug("Bulk add seats in seatrow");
        for (HallPlanSeatDto seat : bulkDto.getSeats()) {
            seat.setId(null);
            seat.setSeatrowId(bulkDto.getSeatRowId());
            seatRepository.save(seatMapper.toEntity(seat));
        }
        return bulkDto.getSeats();
    }

    @Override
    public List<HallPlanSeatDto> bulkUpdateSeats(HallPlanSeatBulkDto bulkDto) {
        LOGGER.debug("Bulk update seats in seatrow");
        for (HallPlanSeatDto seat : bulkDto.getSeats()) {
            seatRepository.save(seatMapper.toEntity(seat));
        }
        return bulkDto.getSeats();
    }

    @Override
    public boolean doesSeatExist(Long seatId) {
        LOGGER.debug("Check if Seat exists and is purchaseabkle");
        Optional<HallPlanSeat> optionalHallPlanSeat = seatRepository.getSeatById(seatId);
        if (optionalHallPlanSeat.isEmpty()) {
            return false;
        }
        HallPlanSeat seat = optionalHallPlanSeat.get();
        return !HallPlanSeatType.VACANT_SEAT.equals(seat.getType());
    }

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean purchaseReservedSeat(Long seatId) {
        LOGGER.debug("If possible, buy seat");
        Optional<HallPlanSeat> optionalHallPlanSeat = seatRepository.getSeatById(seatId);
        if (optionalHallPlanSeat.isEmpty()) {
            return false;
        }
        HallPlanSeat seat = optionalHallPlanSeat.get();
        if (HallPlanSeatType.VACANT_SEAT.equals(seat.getType())) {
            return false;
        }

        if (seat.getReservedNr().equals(0L)) {
            LOGGER.error("User tries to buy reserved seat but there are no reserved seats persisted.");
            return false;
        }

        seat.setBoughtNr(seat.getBoughtNr() + 1L);
        seat.setReservedNr(seat.getReservedNr() - 1L);

        seatRepository.save(seat);
        SeatRow seatRow = seatRowRepository.getReferenceById(seat.getSeatrowId());
        eventService.incrementSoldTickets(seatRow.getHallPlanId());
        return true;
    }

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean tryReserveSeat(Long seatId) {
        LOGGER.debug("If possible, reserve seat");
        Optional<HallPlanSeat> optionalHallPlanSeat = seatRepository.getSeatById(seatId);
        if (optionalHallPlanSeat.isEmpty()) {
            return false;
        }
        HallPlanSeat seat = optionalHallPlanSeat.get();
        if (HallPlanSeatType.VACANT_SEAT.equals(seat.getType())) {
            return false;
        }

        if (seat.getCapacity() <= (seat.getReservedNr() + seat.getBoughtNr())) {
            return false;
        }

        seat.setReservedNr(seat.getReservedNr() + 1L);
        seatRepository.save(seat);
        return true;
    }

    @Override
    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    public boolean cancelReservation(Long seatId) {
        LOGGER.debug("cancel reservation of seat");
        Optional<HallPlanSeat> optionalHallPlanSeat = seatRepository.getSeatById(seatId);
        if (optionalHallPlanSeat.isEmpty()) {
            return false;
        }
        HallPlanSeat seat = optionalHallPlanSeat.get();
        if (HallPlanSeatType.VACANT_SEAT.equals(seat.getType())) {
            return false;
        }

        if (seat.getReservedNr() <= 0) {
            return false;
        }

        seat.setReservedNr(seat.getReservedNr() - 1L);
        seatRepository.save(seat);
        return true;
    }

    @Override
    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    public boolean freePurchasedSeat(Long seatId) {
        LOGGER.debug("Free purchased seat");
        Optional<HallPlanSeat> optionalHallPlanSeat = seatRepository.getSeatById(seatId);
        if (optionalHallPlanSeat.isEmpty()) {
            return false;
        }
        HallPlanSeat seat = optionalHallPlanSeat.get();
        if (HallPlanSeatType.VACANT_SEAT.equals(seat.getType())) {
            return false;
        }

        if (seat.getBoughtNr() <= 0) {
            return false;
        }

        seat.setBoughtNr(seat.getBoughtNr() - 1L);
        seatRepository.save(seat);

        SeatRow seatRow = seatRowRepository.getReferenceById(seat.getSeatrowId());
        eventService.decrementSoldTickets(seatRow.getHallPlanId());
        return true;
    }

}

