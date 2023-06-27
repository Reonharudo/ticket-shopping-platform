package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.DetailedHallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanSectionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanSectionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRowRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HallPlanServiceImpl implements HallPlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HallPlanRepository hallPlanRepository;

    private final HallPlanMapper hallPlanMapper;

    private final HallPlanSectionMapper hallPlanSectionMapper;

    private final HallPlanSectionRepository hallPlanSectionRepository;
    private final SeatRowRepository seatRowRepository;
    private final HallPlanSeatRepository hallPlanSeatRepository;

    private final HallPlanSeatRepository seatRepository;

    public HallPlanServiceImpl(HallPlanRepository hallPlanRepository, HallPlanMapper hallPlanMapper, HallPlanSectionMapper hallPlanSectionMapper,
                               HallPlanSectionRepository hallPlanSectionRepository, SeatRowRepository seatRowRepository,
                               HallPlanSeatRepository seatRepository) {
        this.hallPlanRepository = hallPlanRepository;
        this.hallPlanMapper = hallPlanMapper;
        this.hallPlanSectionMapper = hallPlanSectionMapper;
        this.hallPlanSectionRepository = hallPlanSectionRepository;
        this.seatRowRepository = seatRowRepository;
        this.hallPlanSeatRepository = seatRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<HallPlan> findAll() {
        LOGGER.debug("Find all hall plans");
        return hallPlanRepository.findAllHallPlans();
    }

    @Override
    public List<HallPlan> searchHallPlans(HallPlanSearchDto searchDto) {
        if (searchDto.getName() == null) {
            searchDto.setName("");
        }
        if (searchDto.getDescription() == null) {
            searchDto.setDescription("");
        }
        return hallPlanRepository.searchByNameAndDescriptionIgnoreCase(searchDto.getName(), searchDto.getDescription(), searchDto.getIsTemplate());
    }

    @Override
    public HallPlan createHallPlan(HallPlanDto hallplan) throws ValidationException {
        LOGGER.debug("Create new hall plan");
        Optional<HallPlan> existingHallPlan = hallPlanRepository.findHallPlanById(hallplan.getId());
        if (existingHallPlan.isPresent()) {
            throw new ValidationException("Hallplan with id:" + hallplan.getId() + " already exists");
        }
        return hallPlanRepository.save(hallPlanMapper.hallPlanDtoToHallPlan(hallplan));
    }

    @Override
    public DetailedHallPlanDto snapshotHallplan(HallPlanDto newHallplan, Long baseHallplanId) throws ValidationException {
        LOGGER.trace("snapshotHallplan({}, {})", newHallplan, baseHallplanId);
        Optional<HallPlan> optBaseHallplan = hallPlanRepository.findById(baseHallplanId);
        if (optBaseHallplan.isEmpty()) {
            throw new ValidationException("Based hallplan does not exist");
        }

        HallPlan baseHallplan = optBaseHallplan.get();

        if (newHallplan.getDescription() == null) {
            newHallplan.setDescription(baseHallplan.getDescription());
        }
        if (newHallplan.getName() == null) {
            newHallplan.setName(baseHallplan.getName());
        }

        if (baseHallplan.getIsTemplate()) {
            //create new hallplan
            HallPlan persitedHallplan = hallPlanRepository.save(hallPlanMapper.hallPlanDtoToHallPlan(newHallplan));

            //create deep snapshot - meaning copy over all tuples to the snapshot
            for (SeatRow seatRow : baseHallplan.getSeatRows()) {
                //persist seatrow
                SeatRow snapshotSeatRow = new SeatRow();
                snapshotSeatRow.setRowNr(seatRow.getRowNr());
                snapshotSeatRow.setHallPlanId(persitedHallplan.getId());
                SeatRow persistedSnapshotSeatRow = seatRowRepository.save(snapshotSeatRow);
                persistedSnapshotSeatRow.setSeats(new ArrayList<>());

                //aggregate all seats with the same section in a map
                //{key, value} where key is the section id and value is a list of seats
                //that have the same section (=key)
                Map<Long, List<HallPlanSeatDto>> aggregatedSeats = new HashMap<>();
                for (HallPlanSeat seat : seatRow.getSeats()) {
                    if (aggregatedSeats.containsKey(seat.getSection().getId())) {
                        HallPlanSeatDto clonedSeat = new HallPlanSeatDto();
                        clonedSeat.setStatus(seat.getStatus());
                        clonedSeat.setType(seat.getType());
                        clonedSeat.setCapacity(seat.getCapacity());
                        clonedSeat.setSeatNr(seat.getSeatNr());
                        clonedSeat.setOrderNr(seat.getOrderNr());
                        clonedSeat.setSeatrowId(persistedSnapshotSeatRow.getId());
                        clonedSeat.setBoughtNr(seat.getBoughtNr());
                        clonedSeat.setReservedNr(seat.getReservedNr());


                        HallPlanSectionDto clonedSection = new HallPlanSectionDto();
                        clonedSection.setHallPlanId(persitedHallplan.getId());
                        clonedSection.setPrice(seat.getSection().getPrice());
                        clonedSection.setName(seat.getSection().getName());
                        clonedSection.setColor(seat.getSection().getColor());
                        clonedSeat.setSection(clonedSection);

                        aggregatedSeats.get(seat.getSection().getId()).add(clonedSeat);
                    } else {
                        //we create a new seat object otherwise we will have an entitymanager
                        //no longer referenced error
                        HallPlanSeatDto clonedSeat = new HallPlanSeatDto();
                        clonedSeat.setStatus(seat.getStatus());
                        clonedSeat.setType(seat.getType());
                        clonedSeat.setCapacity(seat.getCapacity());
                        clonedSeat.setSeatNr(seat.getSeatNr());
                        clonedSeat.setOrderNr(seat.getOrderNr());
                        clonedSeat.setSeatrowId(persistedSnapshotSeatRow.getId());
                        clonedSeat.setBoughtNr(seat.getBoughtNr());
                        clonedSeat.setReservedNr(seat.getReservedNr());

                        HallPlanSectionDto clonedSection = new HallPlanSectionDto();
                        clonedSection.setHallPlanId(persitedHallplan.getId());
                        clonedSection.setPrice(seat.getSection().getPrice());
                        clonedSection.setName(seat.getSection().getName());
                        clonedSection.setColor(seat.getSection().getColor());
                        clonedSeat.setSection(clonedSection);

                        List<HallPlanSeatDto> list = new ArrayList<>();
                        list.add(clonedSeat);

                        aggregatedSeats.put(seat.getSection().getId(), list);
                    }
                }

                //now assign the previously generated snapshotSeats to seatrow
                for (List<HallPlanSeatDto> seatlist : aggregatedSeats.values()) {

                    HallPlanSection persistedSnapshotSection = null;
                    for (HallPlanSeatDto seat : seatlist) {
                        if (persistedSnapshotSection == null) {
                            //persist section which is shared by all seats in current iteration
                            HallPlanSectionDto basedSection = seat.getSection();

                            HallPlanSection snapshotSection = new HallPlanSection();
                            snapshotSection.setHallPlanId(persitedHallplan.getId());
                            snapshotSection.setPrice(basedSection.getPrice());
                            snapshotSection.setName(basedSection.getName());
                            snapshotSection.setColor(basedSection.getColor());

                            persistedSnapshotSection = hallPlanSectionRepository.save(snapshotSection);
                        }
                        //persist seat
                        HallPlanSeat snapshotSeat = new HallPlanSeat();
                        snapshotSeat.setStatus(seat.getStatus());
                        snapshotSeat.setType(seat.getType());
                        snapshotSeat.setCapacity(seat.getCapacity());
                        snapshotSeat.setSeatNr(seat.getSeatNr());
                        snapshotSeat.setOrderNr(seat.getOrderNr());
                        snapshotSeat.setSeatrowId(persistedSnapshotSeatRow.getId());
                        snapshotSeat.setSection(persistedSnapshotSection);
                        snapshotSeat.setBoughtNr(seat.getBoughtNr());
                        snapshotSeat.setReservedNr(seat.getReservedNr());
                        hallPlanSeatRepository.save(snapshotSeat);
                    }
                }
            }

            return getHallPlanById(persitedHallplan.getId());
        } else {
            throw new ValidationException("Given Hallplan with id: " + baseHallplanId + " is not a template");
        }
    }


    @Override
    public DetailedHallPlanDto getHallPlanById(Long id) {
        LOGGER.debug("Get hall plan by id: {}", id);
        Optional<HallPlan> hallPlanEntityWithoutSeats = hallPlanRepository.findHallPlanById(id);
        List<SeatRow> seatRows = seatRowRepository.findAllByHallplanIdWithSeats(id);
        if (hallPlanEntityWithoutSeats.isPresent()) {
            hallPlanEntityWithoutSeats.get().setSeatRows(seatRows);
        } else {
            throw new NotFoundException("Hall Plan with id " + id + " was not found in the system");
        }
        return hallPlanEntityWithoutSeats.map(hallPlanMapper::mapToDetailedHallPlanDto).orElse(null);
    }

    @Override
    public void deleteHallPlanById(Long id) {
        LOGGER.debug("Delete hall plan by id: {}", id);
        if (!hallPlanRepository.existsById(id)) {
            throw new NotFoundException("Hall Plan not found with id: " + id);
        }
        Optional<HallPlan> hallplan = hallPlanRepository.findHallPlanById(id);
        List<SeatRow> rows = hallplan.get().getSeatRows();
        if (rows != null) {
            for (SeatRow row : rows) {
                for (HallPlanSeat seat : row.getSeats()) {
                    seatRepository.deleteById(seat.getId());
                }
            }
            for (SeatRow row : rows) {
                seatRowRepository.deleteById(row.getId());
            }
        }

        List<HallPlanSection> sections = hallPlanSectionRepository.findAllByHallplanId(id);
        if (sections != null) {
            for (HallPlanSection sec : sections) {
                hallPlanSectionRepository.deleteById(sec.getId());
            }
        }
        hallPlanRepository.deleteById(id);
    }

    @Override
    public HallPlanDto updateHallPlanById(Long id, HallPlanDto hallPlanDto) {
        LOGGER.debug("Update hall plan by id: {}", id);
        Optional<HallPlan> hallPlanOptional = hallPlanRepository.findById(id);
        if (hallPlanOptional.isPresent()) {
            HallPlan hallPlan = hallPlanOptional.get();
            hallPlan.setName(hallPlanDto.getName());
            hallPlan.setDescription(hallPlanDto.getDescription());
            HallPlan updatedhallPlan = hallPlanRepository.save(hallPlan);
            return hallPlanMapper.hallPlanToHallPlanDto(updatedhallPlan);
        } else {
            throw new EntityNotFoundException("Hall Plan with id " + id + " was not found in the system");
        }
    }

    @Override
    public HallPlanSection createSection(HallPlanSectionDto sectionDto) {
        LOGGER.debug("Create new hall plan section");
        HallPlanSection section = new HallPlanSection();
        section.setName(sectionDto.getName());
        section.setColor(sectionDto.getColor());
        section.setPrice(sectionDto.getPrice());
        section.setHallPlanId(sectionDto.getHallPlanId());
        return hallPlanSectionRepository.save(section);
    }

    @Override
    public HallPlanSection updateSection(Long id, HallPlanSectionDto sectionDto) {
        LOGGER.debug("Update hall plan section by id: {}", id);
        HallPlanSection section = getSectionById(id);
        section.setName(sectionDto.getName());
        section.setColor(sectionDto.getColor());
        section.setPrice(sectionDto.getPrice());
        section.setHallPlanId(sectionDto.getHallPlanId());
        return hallPlanSectionRepository.save(section);
    }

    @Override
    public void deleteSection(Long id) {
        LOGGER.debug("Delete hall plan section by id: {}", id);
        HallPlanSection section = getSectionById(id);
        hallPlanSectionRepository.delete(section);
    }

    @Override
    public HallPlanSection getSection(Long id) {
        LOGGER.debug("Get hall plan section by id: {}", id);
        return getSectionById(id);
    }

    @Override
    public List<HallPlanSection> getAllSections() {
        LOGGER.debug("Get all hall plan sections");
        return hallPlanSectionRepository.findAll();
    }

    @Override
    public List<HallPlanSection> findAllByHallPlanId(Long id) {
        LOGGER.debug("Find all hall plan sections by hall plan id: {}", id);
        return Collections.emptyList();
    }

    @Override
    public List<HallPlanSectionDto> findAllSectionsByHallPlanIdWithCounts(Long hallplanId) {
        List<Object[]> counts = hallPlanRepository.findAllSectionsByHallPlanIdCounts(hallplanId);
        List<Object[]> zeroCounts = hallPlanRepository.findHallPlanCountsById(hallplanId);
        List<HallPlanSection> sections = new ArrayList<>();
        List<Long> existIdList = new ArrayList<>();
        for (Object[] count : counts) {
            HallPlanSection section = (HallPlanSection) count[0];
            section.setCount((Long) count[1]);
            sections.add((HallPlanSection) count[0]);
            existIdList.add(section.getId());
        }
        for (Object[] zeroCount : zeroCounts) {
            HallPlanSection section = (HallPlanSection) zeroCount[0];
            section.setCount(0L);
            boolean exist = false;
            for (Long id : existIdList) {
                if (id.longValue() == section.getId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                sections.add(section);
            }

        }
        return hallPlanSectionMapper.toDto(sections);
    }

    @Override
    public Page<HallPlan> findPageOfHallplans(int pageIndex, String search) {
        Pageable pageable = PageRequest.of(pageIndex, 5, Sort.by("name").ascending());
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.isTrue(root.get("id"));
            if (search != null) {
                predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%");
            }
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.get("isTemplate")));
            return predicate;
        };
        return hallPlanRepository.findAll(specification, pageable);
    }

    @Override
    public List<HallPlanSectionDto> findAllSectionsByHallPlanId(Long hallplanId) {
        return hallPlanSectionMapper.toDto(hallPlanRepository.findAllSectionsByHallPlanId(hallplanId));
    }


    private HallPlanSection getSectionById(Long id) {
        LOGGER.debug("Find section by id: {}", id);
        return hallPlanSectionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Hall Plan Section with id " + id + " not found"));
    }
}
