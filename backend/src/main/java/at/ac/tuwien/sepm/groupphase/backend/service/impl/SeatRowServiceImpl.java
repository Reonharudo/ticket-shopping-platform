package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatRowBulkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatRowMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SeatRowService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class SeatRowServiceImpl implements SeatRowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SeatRowRepository seatRowRepository;
    private final SeatRowMapper seatRowMapper;

    private final HallPlanRepository hallPlanRepository;

    @Autowired
    public SeatRowServiceImpl(SeatRowRepository seatRowRepository, SeatRowMapper seatRowMapper, HallPlanRepository hallPlanRepository) {
        this.seatRowRepository = seatRowRepository;
        this.seatRowMapper = seatRowMapper;
        this.hallPlanRepository = hallPlanRepository;
    }

    @Override
    public List<SeatRowDto> findAllSeatRows() {
        LOGGER.debug("Finding all seat rows with hall plans");
        List<SeatRow> seatRowEntities = seatRowRepository.findAllSeatRowsWithHallPlans();
        return seatRowMapper.toDto(seatRowEntities);
    }

    @Override
    public List<SeatRowDto> findAllSeatRowsOfHallPlan(Long id) {
        LOGGER.debug("Finding all seat rows for hall plan with id: {}", id);
        List<SeatRow> seatRowEntities = seatRowRepository.findAllByHallplanId(id);
        return seatRowMapper.toDto(seatRowEntities);
    }

    @Override
    public SeatRowDto createSeatRow(SeatRowDto seatRowDto) throws ValidationException {
        Long hallPlanId = seatRowDto.getHallPlanId();
        LOGGER.debug("Creating seat row for hall plan with id: {}", hallPlanId);
        HallPlan hallPlan = hallPlanRepository.findById(hallPlanId)
            .orElseThrow(() -> new NotFoundException("HallPlan with id " + hallPlanId + " not found"));

        Optional<SeatRow> existingSeatRow = seatRowRepository.findByRowNrAndHallPlanId(seatRowDto.getRowNr(), hallPlan.getId());
        if (existingSeatRow.isPresent()) {
            throw new ValidationException("SeatRow with rowNr " + seatRowDto.getRowNr() + " and hallPlanId " + hallPlanId + " already exists");
        }

        SeatRow seatRow = seatRowMapper.toEntity(seatRowDto);
        seatRow.setId(null); //Create method shall not overwrite id

        SeatRow savedSeatRow = seatRowRepository.save(seatRow);
        return seatRowMapper.toDto(savedSeatRow);
    }

    @Override
    public SeatRowDto getSeatRowById(Long id) {
        LOGGER.debug("Finding seat row with id: {}", id);
        Optional<SeatRow> seatRowEntityOptional = seatRowRepository.findById(id);
        return seatRowEntityOptional.map(seatRowMapper::toDto).orElse(null);
    }

    @Override
    public SeatRowDto updateSeatRow(SeatRowDto seatRowDto) throws ValidationException {
        Long id = seatRowDto.getId();
        LOGGER.debug("Updating seat row with id: {}", id);
        Optional<SeatRow> seatRowEntityOptional = seatRowRepository.findById(id);

        if (seatRowEntityOptional.isPresent()) {
            SeatRow seatRowEntity = seatRowMapper.toEntity(seatRowDto);
            SeatRow updatedSeatRow = seatRowRepository.save(seatRowEntity);
            return seatRowMapper.toDto(updatedSeatRow);
        }
        return null;
    }

    @Override
    public boolean deleteSeatRowById(Long id) {
        LOGGER.debug("Deleting seat row with id: {}", id);
        Optional<SeatRow> seatRowEntityOptional = seatRowRepository.findById(id);
        if (seatRowEntityOptional.isPresent()) {
            seatRowRepository.deleteById(id);
            return true;
        } else {
            throw new NotFoundException("The seat with id " + id + "is not defined");
        }
    }

    @Override
    public List<SeatRowDto> bulkCreateSeatRow(HallPlanSeatRowBulkDto seatRowBulkDto) {
        LOGGER.debug("Bulk create seatrows in hallplan");
        for (SeatRowDto seatRow : seatRowBulkDto.getSeatRows()) {
            seatRow.setId(null);
            seatRowRepository.save(seatRowMapper.toEntity(seatRow));
        }
        return seatRowBulkDto.getSeatRows();
    }

    @Override
    public List<SeatRowDto> bulkUpdateSeatRow(HallPlanSeatRowBulkDto seatRowBulkDto) {
        LOGGER.debug("Bulk update seatrows in hallplan");
        for (SeatRowDto seatRow : seatRowBulkDto.getSeatRows()) {
            seatRowRepository.save(seatRowMapper.toEntity(seatRow));
        }
        return seatRowBulkDto.getSeatRows();
    }


}